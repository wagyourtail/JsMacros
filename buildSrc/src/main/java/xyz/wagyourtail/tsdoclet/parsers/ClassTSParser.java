package xyz.wagyourtail.tsdoclet.parsers;

import com.sun.javadoc.*;
import xyz.wagyourtail.tsdoclet.AbstractTSParser;

import java.util.*;
import java.util.stream.Collectors;

public class ClassTSParser extends AbstractTSParser {
    public static Package topLevelPackage = new Package("Java", null);
    
    public final String pkg;
    public final String cname;
    public String cachedTS = null;
    
    public final static String[] predefinedClasses = new String[]{"java.util.Collection", "java.util.List", "java.util.Map", "java.util.Set", "java.io.File", "java.net.URL", "java.net.URI", "java.lang.Object", "java.lang.Class", "java.lang.Throwable", "java.io.Serializable", "java.lang.StackTraceElement"};
    
    protected ClassTSParser(ClassDoc clazz, String cname, String pkg) {
        super(clazz);
        this.pkg = pkg;
        this.cname = cname;
    }
    
    @Override
    public String genTypeScript() {
        if (cachedTS != null) return cachedTS;
        StringBuilder s = new StringBuilder();
        Tag[] classtags = clazz.inlineTags();
        if (classtags.length > 0) s.append(AbstractTSParser.genCommentTypeScript(classtags, false,1));
        s.append("export interface ").append(cname);
        TypeVariable[] params = clazz.typeParameters();
        if (params.length > 0) {
            s.append("<").append(Arrays.stream(params).map(AbstractTSParser::parseType).collect(Collectors.joining(", "))).append(">");
        }
        s.append(" extends ");
        if (clazz.superclass() == null || clazz.superclass().qualifiedTypeName().equals("java.lang.Object")) {
            if (clazz.isInterface()) s.append("Java.Interface");
            else s.append("Java.Object");
            
        } else {
            String parsed = parseType(clazz.superclassType());
            if (parsed.startsWith("Java")) s.append(parsed);
            else s.append("Java.Object");
        }
        String[] interf = Arrays.stream(clazz.interfaceTypes()).map(AbstractTSParser::parseType).filter(e -> e.startsWith("Java")).toArray(String[]::new);
        if (interf.length > 0) {
            s.append(", ").append(String.join(", ", interf));
        }
        s.append(" {");
        s.append(insertEachLine(genFieldTS(), "\t"));
        s.append("\n");
        s.append(insertEachLine(genMethodTS(false), "\t"));
    
        s.append("\n}");
        
        return cachedTS = s.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassTSParser) {
            return ((ClassTSParser) obj).clazz.equals(clazz);
        }
        return false;
    }
    
    public static void addClass(Type ctype) {
        String cpackage = getPackage(ctype);
        if (cpackage.equals("java.lang")) topLevelPackage.addClass(ctype);
        topLevelPackage.getPackage(cpackage.replace("java.lang.", "")).addClass(ctype);
    }
    
    /**
     * also include outer class
     */
    public static String getPackage(Type clazz) {
        String qual = clazz.qualifiedTypeName();
        String name = clazz.simpleTypeName();
        return qual.substring(0, qual.length() - name.length() - 1);
    }
    
    public static class Package {
        public boolean dirty = true;
        Map<String, ClassTSParser> classes = new LinkedHashMap<>();
        Map<String, Package> children = new LinkedHashMap<>();
        final Package parent;
        final String packageName;
        
        public Package(String packageName, Package parent) {
            this.packageName = packageName;
            this.parent = parent;
        }
        
        public void addClass(Type ctype) {
            if (Arrays.stream(predefinedClasses).anyMatch(e -> e.equals(ctype.qualifiedTypeName()))) return;
            ClassDoc clazz = ctype.asClassDoc();
            String cname = ctype.simpleTypeName();
            String cpackage = ClassTSParser.getPackage(ctype);
            if (!classes.containsKey(cname)) {
                markDirty();
                classes.put(cname, new ClassTSParser(clazz, cname, cpackage));
            }
        }
        
        public void markDirty() {
            if (parent != null) parent.markDirty();
            dirty = true;
        }
        
        public Package getPackage(String packageName) {
            List<String> pkg = new LinkedList<>();
            Collections.addAll(pkg, packageName.split("\\."));
            return getPackage(pkg);
        }
        
        public Package getPackage(List<String> pkg) {
            if (pkg.size() == 0) return this;
            if (!children.containsKey(pkg.get(0))) children.put(pkg.get(0), new Package(pkg.get(0), this));
            return children.get(pkg.remove(0)).getPackage(pkg);
        }
        
        public TSPackage genTypeScript() {
            dirty = false;
            List<String> cls = new LinkedList<>(classes.values()).stream().map(ClassTSParser::genTypeScript).collect(Collectors.toList());
            List<TSPackage> chd = new LinkedList<>(children.values()).stream().map(Package::genTypeScript).collect(Collectors.toList());
            return new TSPackage(packageName, cls, chd);
        }
    }
    public static class TSPackage {
        String pkgName;
        List<String> interfaces;
        List<TSPackage> children;
        
        public TSPackage(String pkgName, List<String> interfaces, List<TSPackage> children) {
            if (pkgName.equals("function")) pkgName = "_function";
            this.pkgName = pkgName;
            this.interfaces = interfaces;
            this.children = children;
        }
        
        public String genTypeScript() {
            if (interfaces.size() == 0 && children.size() == 1) {
                children.get(0).pkgName = pkgName + "." + children.get(0).pkgName;
                return children.get(0).genTypeScript();
            }
            return "export namespace " + pkgName + " {\n" + AbstractTSParser.insertEachLine(String.join("\n", interfaces) + "\n" +
            children.stream().map(e -> e.genTypeScript() + "\n").collect(Collectors.joining()), "\t") + "\n}";
        }
    }
}
