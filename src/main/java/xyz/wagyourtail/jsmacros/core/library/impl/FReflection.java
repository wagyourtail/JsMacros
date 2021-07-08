package xyz.wagyourtail.jsmacros.core.library.impl;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.classes.Mappings;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Functions for getting and using raw java classes, methods and functions.
 *
 * An instance of this class is passed to scripts as the {@code Reflection} variable.
 *
 * @author Wagyourtail
 * @since 1.2.3
 */
 @Library("Reflection")
 @SuppressWarnings("unused")
public class FReflection extends BaseLibrary {
    private static final CombinedVariableClassLoader classLoader = new CombinedVariableClassLoader(FReflection.class.getClassLoader());
    private static Mappings remapper = null;
    
    /**
     * @param name name of class like {@code path.to.class}
     *
     * @return resolved class
     *
     * @throws ClassNotFoundException
     * @see FReflection#getClass(String, String)
     * @since 1.2.3
     */
    public Class<?> getClass(String name) throws ClassNotFoundException {
        switch (name) {
            case "boolean":
                return boolean.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "char":
                return char.class;
            case "void":
                return void.class;
            default:
                return Class.forName(name, true, classLoader);
        }
    }
    
    /**
     * Use this to specify a class with intermediary and yarn names of classes for cleaner code. also has support for
     * java primitives by using their name in lower case.
     *
     * @param name first try
     * @param name2 second try
     *
     * @return a {@link java.lang.Class Class} reference.
     *
     * @throws ClassNotFoundException
     * @since 1.2.3
     */
    public Class<?> getClass(String name, String name2) throws ClassNotFoundException {
        try {
            return getClass(name);
        } catch (ClassNotFoundException e) {
            return Class.forName(name2);
        }
    }
    
    /**
     * @param c
     * @param name
     * @param parameterTypes
     *
     * @return
     *
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @see FReflection#getDeclaredMethod(Class, String, String, Class...)
     * @since 1.2.3
     */
    public Method getDeclaredMethod(Class<?> c, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        return c.getDeclaredMethod(name, parameterTypes);
    }
    
    /**
     * Use this to specify a method with intermediary and yarn names of classes for cleaner code.
     *
     * @param c
     * @param name
     * @param name2
     * @param parameterTypes
     *
     * @return a {@link java.lang.reflect.Method Method} reference.
     *
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @since 1.2.3
     */
    public Method getDeclaredMethod(Class<?> c, String name, String name2, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        try {
            return c.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            return c.getDeclaredMethod(name2, parameterTypes);
        }
    }
    
    /**
     * @param c
     * @param name
     *
     * @return
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @see FReflection#getDeclaredField(Class, String, String)
     * @since 1.2.3
     */
    public Field getDeclaredField(Class<?> c, String name) throws NoSuchFieldException, SecurityException {
        return c.getDeclaredField(name);
    }
    
    /**
     * Use this to specify a field with intermediary and yarn names of classes for cleaner code.
     *
     * @param c
     * @param name
     * @param name2
     *
     * @return a {@link java.lang.reflect.Field Field} reference.
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @since 1.2.3
     */
    public Field getDeclaredField(Class<?> c, String name, String name2) throws NoSuchFieldException, SecurityException {
        try {
            return c.getDeclaredField(name);
        } catch (NoSuchFieldException | SecurityException e) {
            return c.getDeclaredField(name2);
        }
    }
    
    /**
     * Invoke a method on an object with auto type coercion for numbers.
     *
     * @param m method
     * @param c object (can be {@code null} for statics)
     * @param objects
     *
     * @return
     *
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @since 1.2.3
     */
    public Object invokeMethod(Method m, Object c, Object... objects) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?>[] params = m.getParameterTypes();
        for (int i = 0; i < objects.length; ++i) {
            objects[i] = tryAutoCastNumber(params[i], objects[i]);
        }
        return m.invoke(c, objects);
    }
    
    /**
     * Attempts to create a new instance of a class. You probably don't have to use this one and can just call {@code
     * new} on a {@link java.lang.Class Class} unless you're in LUA, but then you also have the (kinda poorly
     * doccumented, can someone find a better docs link for me)
     * <a target="_blank" href= "http://luaj.sourceforge.net/api/3.2/org/luaj/vm2/lib/jse/LuajavaLib.html">LuaJava Library</a>.
     *
     * @param c
     * @param objects
     *
     * @return
     *
     * @since 1.2.7
     */
    public <T> T newInstance(Class<T> c, Object... objects) {
        Class<?>[] params = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; ++i) {
            params[i] = objects[i].getClass();
        }
        try {
            Constructor<T> con = c.getConstructor(params);
            return con.newInstance(objects);
        } catch (Exception e) {
            for (Constructor<?> con : c.getConstructors()) {
                if (con.getParameterTypes().length != objects.length) continue;
                params = con.getParameterTypes();
                Object[] tempObjects = new Object[objects.length];
                try {
                    for (int i = 0; i < objects.length; ++i) {
                        tempObjects[i] = tryAutoCastNumber(params[i], objects[i]);
                    }
                    return (T) con.newInstance(tempObjects);
                } catch (Exception ignored) {
                }
            }
            throw new RuntimeException("Failed to create new instance, bad parameters?", e);
        }
    }
    
    /**
     * Loads a jar file to be accessible with this library.
     *
     * @param file relative to the macro folder.
     *
     * @return success value
     *
     * @throws IOException
     * @since 1.2.6
     */
    public boolean loadJarFile(String file) throws IOException {
        File jarFile = new File(Core.instance.config.macroFolder, file);
        if (classLoader.hasJar(jarFile)) return true;
        if (!jarFile.exists()) throw new FileNotFoundException("Jar File Not Found");
        return classLoader.addClassLoader(jarFile, new URLClassLoader(new URL[] {new URL("jar:file:" + jarFile.getCanonicalPath() + "!/")}));
    }
    
    protected Object tryAutoCastNumber(Class<?> returnType, Object number) {
        if ((returnType == int.class || returnType == Integer.class) && !(number instanceof Integer)) {
            number = ((Number) number).intValue();
        } else if ((returnType == float.class || returnType == Float.class) && !(number instanceof Float)) {
            number = ((Number) number).floatValue();
        } else if ((returnType == double.class || returnType == Double.class) && !(number instanceof Double)) {
            number = ((Number) number).doubleValue();
        } else if ((returnType == short.class || returnType == Short.class) && !(number instanceof Short)) {
            number = ((Number) number).shortValue();
        } else if ((returnType == long.class || returnType == Long.class) && !(number instanceof Long)) {
            number = ((Number) number).longValue();
        } else if ((returnType == char.class || returnType == Character.class) && !(number instanceof Character)) {
            number = (char) ((Number) number).intValue();
        } else if ((returnType == byte.class || returnType == Byte.class) && !(number instanceof Byte)) {
            number = ((Number) number).byteValue();
        }
        return number;
    }
    
    /**
     * @since 1.3.1
     * @return the previous mapping helper generated with {@link #loadMappingHelper(String)}
     */
    public Mappings loadCurrentMappingHelper() {
        return remapper;
    }
    
    
    /**
     * @param o class you want the name of
     * @since 1.3.1
     * @return the fully qualified class name (with "."'s not "/"'s)
     */
    public String getClassName(Object o) {
        if (o instanceof Class) return ((Class<?>) o).getCanonicalName();
        else return o.getClass().getCanonicalName();
    }
    
    /**
     * @param urlorfile a url or file path the the yarn mappings {@code -v2.jar} file, or {@code .tiny} file. for example {@code https://maven.fabricmc.net/net/fabricmc/yarn/1.16.5%2Bbuild.3/yarn-1.16.5%2Bbuild.3-v2.jar}, if same url/path as previous this will load from cache.
     * @since 1.3.1
     * @return the associated mapping helper.
     */
    public Mappings loadMappingHelper(String urlorfile) {
        if (remapper != null && remapper.mappingsource.equals(urlorfile)) {
            return remapper;
        }
        return remapper = new Mappings(urlorfile);
    }
    
    /**
     * I know this is probably bad practice, but lets be real, this whole library is bad practice, So I can make it
     * worse, right? at least this should work better than {@code try/catch}'ing using
     * {@link ClassLoader#loadClass(String)} to search through every {@link URLClassLoader} that
     * {@link FReflection#loadJarFile(String)} would make, or how I was previously doing it by pre-loading and caching
     * all the classes to a {@link Map}
     *
     * This class is a modification to
     * <a target="_blank" href="https://www.source-code.biz/snippets/java/12.htm">Christian d'Heureuse's JoinClassLoader</a>, under the
     * <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0">Apache-2.0 license</a> to change it from a Class array to a
     * {@link Set}, to allow for modifications to the {@link ClassLoader ClassLoaders} contained in the classLoader.
     *
     * @author Wagyourtail, Christian d'Heureuse
     * @since 1.2.8
     */
    protected static class CombinedVariableClassLoader extends ClassLoader {
        private final Map<File, ClassLoader> siblingDelegates = new LinkedHashMap<>();
        
        public CombinedVariableClassLoader(ClassLoader parent) {
            super(parent);
        }
        
        public boolean addClassLoader(File jarPath, ClassLoader loader) throws IOException {
            return siblingDelegates.putIfAbsent(jarPath.getCanonicalFile(), loader) == null;
        }
        
        public boolean hasJar(File path) throws IOException {
            return siblingDelegates.containsKey(path.getCanonicalFile());
        }
        
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            String path = name.replace('.', '/') + ".class";
            URL url = findResource(path);
            if (url == null) {
                throw new ClassNotFoundException(name);
            }
            ByteBuffer byteCode;
            try {
                byteCode = loadResource(url);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
            return defineClass(name, byteCode, null);
        }
        
        private ByteBuffer loadResource(URL url) throws IOException {
            try (InputStream stream = url.openStream()) {
                int initialBufferCapacity = Math.min(0x40000, stream.available() + 1);
                if (initialBufferCapacity <= 2) {
                    initialBufferCapacity = 0x10000;
                } else {
                    initialBufferCapacity = Math.max(initialBufferCapacity, 0x200);
                }
                ByteBuffer buf = ByteBuffer.allocate(initialBufferCapacity);
                while (true) {
                    if (!buf.hasRemaining()) {
                        ByteBuffer newBuf = ByteBuffer.allocate(2 * buf.capacity());
                        buf.flip();
                        newBuf.put(buf);
                        buf = newBuf;
                    }
                    int len = stream.read(buf.array(), buf.position(), buf.remaining());
                    if (len <= 0) {
                        break;
                    }
                    buf.position(buf.position() + len);
                }
                buf.flip();
                return buf;
            }
        }
        
        @Override
        protected URL findResource(String name) {
            for (ClassLoader delegate : siblingDelegates.values()) {
                URL resource = delegate.getResource(name);
                if (resource != null) {
                    return resource;
                }
            }
            return null;
        }
        
        @Override
        protected Enumeration<URL> findResources(String name) throws IOException {
            Vector<URL> vector = new Vector<>();
            for (ClassLoader delegate : siblingDelegates.values()) {
                Enumeration<URL> enumeration = delegate.getResources(name);
                while (enumeration.hasMoreElements()) {
                    vector.add(enumeration.nextElement());
                }
            }
            return vector.elements();
        }
    }
    
}
