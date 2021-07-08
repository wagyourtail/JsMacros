package xyz.wagyourtail.webdoclet;

import com.sun.javadoc.*;
import xyz.wagyourtail.FileHandler;
import xyz.wagyourtail.XMLBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Main {
    public static File outDir;
    /**
     * package -> base url of package,
     * use with {@code + "ClassName.html"} to get final url.
     */
     public static String version;
     public static String mappingViewerBaseURL;
    public static Map<String, String> externalPackages = new HashMap<>();
    public static Set<String> internalPackages = new LinkedHashSet<>();
    public static Set<ClassDoc> classes = new LinkedHashSet<>();
    
    public static boolean start(RootDoc root) {
        try {
            //create package-list
            for (ClassDoc clazz : root.specifiedClasses()) {
                internalPackages.add(clazz.containingPackage().name());
                classes.add(clazz);
            }
            for (PackageDoc pkg : root.specifiedPackages()) {
                internalPackages.add(pkg.name());
                classes.addAll(Arrays.asList(pkg.allClasses()));
            }
            outDir = new File(outDir, version);
            if (!outDir.exists() && !outDir.mkdirs()) {
                System.err.println("failed to create version folder");
                return false;
            } else if (outDir.exists() && !outDir.isDirectory()) {
                System.err.println("file with version folder name");
                return false;
            }
            
            StringBuilder pkglist = new StringBuilder();
            for (String pkg : internalPackages) {
                if (!externalPackages.containsKey(pkg)) {
                    File pkgFolder = new File(outDir, pkg.replaceAll("\\.", "/"));
                    if (pkgFolder.exists() && !pkgFolder.isDirectory()) {
                        System.err.println("package exists and is a file: " + pkg);
                        return false;
                    } else if (!pkgFolder.exists() && !pkgFolder.mkdirs()) {
                        System.err.println("failed to create package folder : " + pkg);
                        return false;
                    }
                    pkglist.append(pkg).append("\n");
                }
            }
            new FileHandler(new File(outDir, "package-list")).write(pkglist.toString());
            /* spec
             * C\t<searchname>\t<linkname>\t<?group>\t<?alias>
             * F\t<fieldname>\t<fieldlink>
             * M\t<methodname>\t<methodlink>
             */
            StringBuilder searchList = new StringBuilder();
            
            for (ClassDoc clazz : classes) {
                if (!clazz.isPublic()) continue;
                Optional<AnnotationDesc> eventAnnotation = Arrays.stream(clazz.annotations()).filter(e -> e.annotationType().simpleTypeName().equals("Event")).findFirst();
                Optional<AnnotationDesc> libraryAnnotation = Arrays.stream(clazz.annotations()).filter(e -> e.annotationType().simpleTypeName().equals("Library")).findFirst();
                searchList.append("C\t").append(clazz.typeName()).append("\t").append(clazz.containingPackage().name().replaceAll("\\.", "/")).append("/").append(clazz.typeName());
                if (eventAnnotation.isPresent()) {
                    searchList.append("\tEvent");
                } else if (libraryAnnotation.isPresent()) {
                    searchList.append("\tLibrary");
                } else {
                    searchList.append("\tClass");
                }
                if (eventAnnotation.isPresent()) {
                    for (AnnotationDesc.ElementValuePair ep : eventAnnotation.get().elementValues()) {
                        if (ep.element().name().equals("value")) {
                            searchList.append("\t").append(ep.value().value().toString());
                        }
                    }
                } else if (libraryAnnotation.isPresent()) {
                    for (AnnotationDesc.ElementValuePair ep : libraryAnnotation.get().elementValues()) {
                        if (ep.element().name().equals("value")) {
                            searchList.append("\t").append(ep.value().value().toString());
                        }
                    }
                }
                searchList.append("\n");
                for (FieldDoc field : clazz.fields()) {
                    searchList.append("F\t").append(clazz.typeName()).append("#").append(field.name());
                    searchList.append("\t");
                    searchList.append(clazz.containingPackage().name().replaceAll("\\.", "/")).append("/").append(clazz.typeName()).append("#").append(WebParser.memberId(field));
                    searchList.append("\n");
                    
                }
                for (MethodDoc method : clazz.methods()) {
                    searchList.append("M\t").append(clazz.typeName()).append("#").append(method.name()).append("(").append(Arrays.stream(method.parameters()).map(Parameter::name).collect(Collectors.joining(", ")));
                    searchList.append(")\t");
                    searchList.append(clazz.containingPackage().name().replaceAll("\\.", "/")).append("/").append(clazz.typeName()).append("#").append(WebParser.memberId(method));
                    searchList.append("\n");
                }
                StringBuilder upDir = new StringBuilder("../");
                for (String ingored : clazz.containingPackage().name().split("\\.")) {
                    upDir.append("../");
                }
                new FileHandler(new File(outDir, clazz.containingPackage().name().replaceAll("\\.", "/") + "/" + clazz.typeName() + ".html")).write(
                    "<!DOCTYPE html>\n" +
                    new XMLBuilder("html").append(
                        new XMLBuilder("head").append(
                            new XMLBuilder("link", true, true).addStringOption("rel", "stylesheet").addStringOption("href", upDir + "classContent.css")
                        ),
                        new XMLBuilder("body").append(
                            new XMLBuilder("header").append(
                                new XMLBuilder("a").addStringOption("href", upDir.toString()).append(
                                    "<----- Return to main JsMacros docs page."
                                )
                            ),
                            WebParser.parseClass(clazz)
                        )
                    ).toString()
                );
            }
            new FileHandler(new File(outDir, "search-list")).write(searchList.toString());
    
    
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static int optionLength(String var0) {
        return 2;
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
            } else if (option[0].equals("-link")) {
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(option[1] + "package-list").openStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        externalPackages.put(line, option[1] + "index.html?" + line.replaceAll("\\.", "/") + "/");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (option[0].equals("-v")) {
                if (version != null) {
                    reporter.printError("version set more than once");
                    return false;
                }
                version = option[1];
            } else if (option[0].equals("-mcv")) {
                if (mappingViewerBaseURL != null) {
                    reporter.printError("mc version set more than once");
                }
                mappingViewerBaseURL = "https://wagyourtail.xyz/Projects/Minecraft%20Mappings%20Viewer/App?mapping=mcp,srg&version=" + option[1] + "&search=";
            }
        }
        return true;
    }
    
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
}
