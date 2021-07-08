package xyz.wagyourtail.jsmacros.core.config;

import com.google.common.io.Files;
import com.google.gson.*;
import org.apache.logging.log4j.Logger;
import xyz.wagyourtail.jsmacros.core.Core;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigManager {
    protected final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public final Map<String, Class<?>> optionClasses = new LinkedHashMap<>();
    public final Map<Class<?>, Object> options = new LinkedHashMap<>();
    public final File configFolder;
    public final File macroFolder;
    public final File configFile;
    public final Logger LOGGER;
    boolean loadedAsV1 = false;
    public JsonObject rawOptions = null;

    public ConfigManager(File configFolder, File macroFolder, Logger logger) {
        this.configFolder = configFolder.getAbsoluteFile();
        this.macroFolder = macroFolder.getAbsoluteFile();
        this.LOGGER = logger;
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        this.configFile = new File(configFolder, "options.json");
        if (!macroFolder.exists()) {
            macroFolder.mkdirs();
            final File tf = new File(macroFolder, "index.js");
            if (!tf.exists()) try {
                tf.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        optionClasses.put("core", CoreConfigV2.class);
        
        try {
            loadConfig();
        } catch (IllegalAccessException | InstantiationException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public synchronized void reloadRawConfigFromFile() throws IOException {
        try (FileReader reader = new FileReader(configFile)) {
            rawOptions = new JsonParser().parse(reader).getAsJsonObject();
            JsonElement version = rawOptions.get("version");
            loadedAsV1 = (version == null || version.getAsInt() != 2);
        }
    }
    
    public synchronized void convertConfigFormat() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        for (Map.Entry<String, Class<?>> optionClass : optionClasses.entrySet()) {
            convertConfigFormat(optionClass.getValue());
        }
        rawOptions.addProperty("version", 2);
    }
    
    public synchronized void convertConfigFormat(Class<?> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        try {
            Method m = clazz.getDeclaredMethod("fromV1", JsonObject.class);
            Object option = clazz.getDeclaredConstructor().newInstance();
            m.invoke(option, rawOptions);
            options.put(clazz, option);
        } catch (NoSuchMethodException ignored) {
            options.put(clazz, clazz.getDeclaredConstructor().newInstance());
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getOptions(Class<T> optionClass) {
        if (!options.containsKey(optionClass)) return null;
        return (T) options.get(optionClass);
    }
    
    public synchronized void addOptions(String key, Class<?> optionClass) throws IllegalAccessException, InstantiationException {
        if (optionClasses.containsKey(key)) throw new IllegalStateException("Key \""+ key +"\" already in config manager!");
        optionClasses.put(key, optionClass);
        try {
            if (loadedAsV1) {
                convertConfigFormat(optionClass);
            } else {
                if (!rawOptions.has(key)) throw new NullPointerException();
                options.put(optionClass, gson.fromJson(rawOptions.get(key), optionClass));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            options.put(optionClass, optionClass.newInstance());
            saveConfig();
        }
    }
    
    public synchronized void loadConfig() throws IllegalAccessException, InstantiationException, IOException {
        try {
            options.clear();
            if (rawOptions == null) reloadRawConfigFromFile();
            if (loadedAsV1) {
                try {
                    convertConfigFormat();
                } finally {
                    final File back = new File(configFolder, "options.json.v1.bak");
                    if (back.exists()) back.delete();
                    Files.move(configFile, back);
                    saveConfig();
                }
            } else {
                for (Map.Entry<String, Class<?>> optionClass : optionClasses.entrySet()) {
                    try {
                        if (!rawOptions.has(optionClass.getKey())) throw new NullPointerException();
                        options.put(optionClass.getValue(), gson.fromJson(rawOptions.get(optionClass.getKey()), optionClass.getValue()));
                    } catch (JsonSyntaxException | NullPointerException ignored) {
                        options.put(optionClass.getValue(), optionClass.getValue().newInstance());
                        saveConfig();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Config Failed To Load.");
            e.printStackTrace();
            if (configFile.exists()) {
                final File back = new File(configFolder, "options.json.bak");
                if (back.exists()) back.delete();
                Files.move(configFile, back);
            }
            loadDefaults();
            saveConfig();
        }
        LOGGER.info("Loaded Profiles:");
        for (String key : getOptions(CoreConfigV2.class).profileOptions()) {
            LOGGER.info("    " + key);
        }

    }
    
    public void loadDefaults() throws IllegalAccessException, InstantiationException {
        for (Map.Entry<String, Class<?>> optionClass : optionClasses.entrySet()) {
            options.put(optionClass.getValue(), optionClass.getValue().newInstance());
            rawOptions = new JsonObject();
            rawOptions.addProperty("version", 2);
        }
    }

    public void saveConfig() {
        try {
            for (Map.Entry<String, Class<?>> optionClass : optionClasses.entrySet()) {
                rawOptions.add(optionClass.getKey(), gson.toJsonTree(options.get(optionClass.getValue())));
            }
            final FileWriter fw = new FileWriter(configFile);
            fw.write(gson.toJson(rawOptions));
            fw.close();
        } catch (Exception e) {
            LOGGER.error("Config Failed To Save.");
            e.printStackTrace();
        }
    }
}