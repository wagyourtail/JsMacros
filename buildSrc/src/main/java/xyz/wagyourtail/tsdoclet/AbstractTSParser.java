package xyz.wagyourtail.tsdoclet;

import com.sun.javadoc.*;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.tsdoclet.parsers.ClassTSParser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractTSParser {
    public final ClassDoc clazz;

    public AbstractTSParser(ClassDoc clazz) {
        this.clazz = clazz;
    }

    public abstract String genTypeScript();

    public static String fixGTLT(String s) {
        return s.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
    }

    public String genFieldTS() {
        StringBuilder s = new StringBuilder();
        for (FieldDoc field : clazz.fields()) {
            if (!field.isPublic()) continue;
            Tag[] tags = field.inlineTags();
            s.append("\n");
            s.append(AbstractTSParser.genCommentTypeScript(tags, field.isStatic(),1));
            if (field.isFinal()) s.append("readonly ");
            s.append(field.name());
            s.append(": ").append(AbstractTSParser.parseType(field.type()));
        }
        s.append("\n");
        return s.toString();
    }

    public String genMethodTS(boolean export) {
        StringBuilder s = new StringBuilder();
        Map<String, List<MethodDoc>> methods = new LinkedHashMap<>();
        for (MethodDoc method : clazz.methods()) {
            if (!method.isPublic()) continue;
            if (methods.containsKey(method.name())) {
                methods.get(method.name()).add(method);
            } else {
                List<MethodDoc> overloads = new LinkedList<>();
                overloads.add(method);
                methods.put(method.name(), overloads);
            }
        }
        for (Map.Entry<String, List<MethodDoc>> entry : methods.entrySet()) {
        
            Tag[] tags = entry.getValue().stream().flatMap(e -> Arrays.stream(e.inlineTags())).toArray(Tag[]::new);
        
            s.append(AbstractTSParser.genCommentTypeScript(tags, entry.getValue().get(0).isStatic(), 0));
            if (export) s.append("export function ");
            s.append(entry.getKey());
            
            Optional<MethodDoc> met = entry.getValue().stream().filter(e -> e.typeParameters().length > 0).findFirst();
            if (met.isPresent()) {
                TypeVariable[] types = met.get().typeParameters();
                s.append("<").append(Arrays.stream(types).map(AbstractTSParser::parseType).collect(Collectors.joining(", "))).append(">");
            }
            s.append(genMethodTypeScript(entry.getValue()));
            s.append("\n");
        }
        return s.toString();
    }

    public static String parseType(Type type) {
        ParameterizedType ptype = type.asParameterizedType();
        if (ptype != null && !ptype.qualifiedTypeName().startsWith("net.minecraft")) {
            List<String> types = Arrays.stream(ptype.typeArguments()).map(AbstractTSParser::parseType).collect(Collectors.toList());
            return arrayize(transformType(ptype) + "<" + String.join(", ", types) + ">", ptype.dimension());
        }
        return arrayize(transformType(type), type.dimension());
    }
    
    private static String arrayize(String input, String dimensions) {
        StringBuilder inputBuilder = new StringBuilder(input);
        for (int i = 0; i < dimensions.length() / 2; ++i) {
            inputBuilder = new StringBuilder("Java.Array<" + inputBuilder + ">");
        }
        input = inputBuilder.toString();
        return input;
    }
    
    private static String transformType(Type type) {
        if (type.asTypeVariable() != null) {
            //if (type.typeName().equals("?")) return "unknown";
            return type.asTypeVariable().typeName();
        } else if (type.qualifiedTypeName().startsWith("net.minecraft")) {
            return "/* minecraft classes, as any, because obfuscation: */ any";
        } else if (type.asWildcardType() != null) {
            return "any";
        } else if (Main.events.stream().flatMap(e -> Stream.of(e.clazz.qualifiedTypeName())).anyMatch(e -> e.equals(type.qualifiedTypeName()))) {
            return "Events." + Main.events.stream().filter(e -> e.clazz.qualifiedTypeName().equals(type.qualifiedTypeName())).findFirst().get().name;
        } else if (type.qualifiedTypeName().equals("xyz.wagyourtail.jsmacros.core.event.BaseEvent")){
            return "Events.BaseEvent";
        } else if (type.qualifiedTypeName().startsWith("java.lang") || type.isPrimitive()) {
            switch (type.typeName()) {
                case "String":
                case "CharSequence":
                    return "string";
                case "int":
                case "Integer":
                case "float":
                case "Float":
                case "Long":
                case "long":
                case "short":
                case "Short":
                case "char":
                case "Character":
                case "byte":
                case "Byte":
                case "Double":
                case "double":
                    return "number";
                case "Object":
                    return "any";
                case "Boolean":
                case "boolean":
                    return "boolean";
                case "void":
                    return "void";
                default:
                    ClassTSParser.addClass(type);
                    return "Java." + type.qualifiedTypeName().replace("java.lang.", "");
            }
        }
        ClassTSParser.addClass(type);
        return "Java." + type.qualifiedTypeName().replace(".function.", "._function.");
    }
    
    public static String genMethodTypeScript(List<MethodDoc> methods) {
        MethodDoc firstDoc = methods.get(0);
        StringBuilder s = new StringBuilder("(");
        boolean customParamsFlag = true;
        
        for (Tag ann : firstDoc.tags()) {
            if (ann.kind().equals("@custom.replaceParams")) {
                s.append(fixGTLT(ann.text()));
                customParamsFlag = false;
                break;
            }
        }
        
        if (customParamsFlag) {
            List<Pair<String, List<Parameter>>> params = new ArrayList<>();
            for (MethodDoc method : methods) {
                Parameter[] mparams = method.parameters();
                if (mparams.length > params.size()) {
                    for (int i = 0; i < params.size(); ++i) {
                        List<Parameter> p = params.get(i).getValue();
                        params.set(i, new Pair<>(mparams[i].name(), p));
                    }
                }
                for (int i = 0; i < mparams.length; ++i) {
                    if (params.size() == i) {
                        params.add(new Pair<>(mparams[i].name(), new LinkedList<>()));
                    }
                    params.get(i).getValue().add(mparams[i]);
                }
            }
            List<String> paramvals = new LinkedList<>();
            for (Pair<String, List<Parameter>> pairs : params) {
                Set<String> types = new LinkedHashSet<>();
                for (Parameter p : pairs.getValue()) {
                    types.add(parseType(p.type()));
                }
                paramvals.add(pairs.getKey() + ((pairs.getValue().size() < methods.size()) ? "?: " : ": ") + String.join(" | ", types));
            }
            s.append(String.join(", ", paramvals));
        }
        
        s.append("):").append(parseType(methods.get(0).returnType()));
        
        s.append(";");
        return s.toString();
    }
    
    public static String insertEachLine(String input, String insert) {
        String[] inp = input.split("\n", -1);
        return insert + String.join("\n"+ insert, inp);
    }
    
    public static String genCommentTypeScript(Tag[] inline, boolean isStatic, int tabLevel) {
        if (inline.length == 0 && !isStatic) return "";
        StringBuilder comment = new StringBuilder(" ");
        for (Tag t : inline) {
            if (t.name().equals("@link")) {
                String[] stuff = t.text().split("\\s+");
                comment.append(fixGTLT(stuff[stuff.length - 1]));
            } else if (t.name().equals("@code")) {
                comment.append("`").append(fixGTLT(t.text())).append("`");
            } else {
                comment.append(fixGTLT(t.text()));
            }
        }
        if (isStatic) comment.append(inline.length > 0 ? "\n\nstatic" : "static");
        StringBuilder delim = new StringBuilder();
        for (int i = 0; i < tabLevel; ++i) {
            delim.append("\t");
        }
        comment = new StringBuilder(insertEachLine(comment.toString(), delim + " *"));
        return "\n" + delim + "/**\n" + comment + "\n" + delim + " */" + "\n";
    }
}
