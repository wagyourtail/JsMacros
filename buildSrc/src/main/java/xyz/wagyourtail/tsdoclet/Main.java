package xyz.wagyourtail.tsdoclet;

import com.sun.javadoc.*;
import xyz.wagyourtail.FileHandler;
import xyz.wagyourtail.tsdoclet.parsers.ClassTSParser;
import xyz.wagyourtail.tsdoclet.parsers.EventTSParser;
import xyz.wagyourtail.tsdoclet.parsers.LibraryTSParser;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public class Main {
    public static File outDir;
    public static FileHandler outputTS;
    
    public static final List<LibraryTSParser> libs = new LinkedList<>();
    public static final Map<String, Set<ClassTSParser>> classes = new LinkedHashMap<>();
    public static final List<EventTSParser> events = new LinkedList<>();
    public static String version;
    public static RootDoc root;

    public static boolean start (RootDoc root) {
        Main.root = root;
        outputTS = new FileHandler(new File(outDir, "JsMacros-" + version + ".d.ts"));
        
        if (!outDir.exists() && !outDir.mkdirs()) {
            System.err.println("Failed to create version dir");
            return false;
        }
    
        if (outputTS.getFile().exists() && !outputTS.getFile().delete()) {
            System.err.println("Failed to remove old ts output");
            return false;
        }
    
        for (ClassDoc clazz : root.classes()) {
            for (AnnotationDesc desc : clazz.annotations()) {
                if (desc.annotationType().name().equals("Library")) {
                    String value = null;
                    for (AnnotationDesc.ElementValuePair val : desc.elementValues()) {
                        if (val.element().name().equals("value")) {
                            value = (String) val.value().value();
                        }
                    }
                    genLib(clazz, value);
                    break;
                }
                if (desc.annotationType().name().equals("Event")) {
                    String name = null;
                    String oldName = null;
                    for (AnnotationDesc.ElementValuePair val : desc.elementValues()) {
                        if (val.element().name().equals("value")) {
                            name = (String) val.value().value();
                        } else {
                            oldName = (String)val.value().value();
                        }
                    }
                    genEvent(clazz, name, oldName);
                    break;
                }
            }
        }
    
        try {
            outputTS.append("declare const event: Events.BaseEvent;\n" +
                             "declare const file: Java.java.io.File;\n" +
                             "declare const context: Java.xyz.wagyourtail.jsmacros.core.language.ContextContainer<any>;\n\n" +
                             
                             "declare namespace Events {\n" +
                                "\texport interface BaseEvent extends Java.Object {\n" +
                                    "\t\tgetEventName(): string;\n" +
                                "\t}");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        for (EventTSParser event : events) {
            try {
                outputTS.append("\n\n" + event.genTypeScript());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        
        try {
            outputTS.append("\n}");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    
        for (LibraryTSParser lib : libs) {
            try {
                outputTS.append("\n\n" + lib.genTypeScript());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    
        ClassTSParser.TSPackage pkg = null;
        while (ClassTSParser.topLevelPackage.dirty) {
            pkg = ClassTSParser.topLevelPackage.genTypeScript();
        }
    
        try {
            outputTS.append("\n\ndeclare" + pkg.genTypeScript().substring(6));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    
        return true;
    }
    public static int optionLength(String var0) {
        if (var0.equals("-d")) return 2;
        else return 2;
    }
    
    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        for (String[] option : options) {
            if (option[0].equals("-d")) {
                if (outDir != null) {
                    reporter.printError("outdir set more than once");
                    return false;
                }
                outDir = new File(option[1]);
                if (outDir.exists()) {
                    if (!outDir.isDirectory()) {
                        reporter.printError("output is an existing file");
                        return false;
                    }
                } else {
                    return outDir.mkdirs();
                }
            } else if (option[0].equals("-v")) {
                if (version != null) {
                    reporter.printError("version set more than once");
                    return false;
                }
                version = option[1];
            }
        }
        return true;
    }
    
    public static void genLib(ClassDoc lib, String libName) {
        libs.add(new LibraryTSParser(lib, libName));
    }
    
    public static void genEvent(ClassDoc event, String name, String oldName) {
        events.add(new EventTSParser(event, name, oldName));
    }
    
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
}
