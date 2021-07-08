package xyz.wagyourtail.webdoclet;

import com.sun.javadoc.*;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.XMLBuilder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WebParser {
    
    public static XMLBuilder parseClass(ClassDoc clazz) {
        XMLBuilder builder = new XMLBuilder("main").addStringOption("class", "classDoc");
        XMLBuilder constructors = null;
        XMLBuilder subClasses;
        builder.append(subClasses = new XMLBuilder("div").addStringOption("id", "subClasses"));
        for (ClassDoc otherClazz : Main.classes.stream().sorted(Comparator.comparing(Type::typeName)).collect(Collectors.toList())) {
            if (otherClazz.subclassOf(clazz) && otherClazz.superclass() == clazz || Arrays.stream(otherClazz.interfaces()).anyMatch(e -> e == clazz)) {
                subClasses.append(parseType(clazz, otherClazz), " ");
            }
        }
        XMLBuilder cname;
        builder.append(cname = new XMLBuilder("h2", true, true).addStringOption("class", "classTitle").append(clazz.typeName()));
        
        Type[] ptypes = clazz.typeParameters();
        if (ptypes.length > 0) {
            cname.append("<");
            int i;
            for (i = 0; i < ptypes.length - 1; ++i) {
                cname.append(parseType(clazz, ptypes[i]));
                cname.append(", ");
            }
            cname.append(parseType(clazz, ptypes[i]));
            cname.append(">");
        }
        
        builder.append(createFlags(clazz, false));
        if (clazz.superclassType() != null || clazz.interfaces().length > 0) {
            XMLBuilder ext;
            builder.append(ext = new XMLBuilder("h4", true, true).addStringOption("class", "classExtends"));
            if (clazz.superclassType() != null) {
                ext.append("extends ");
                ext.append(parseType(clazz, clazz.superclassType()));
            }
            if (clazz.interfaces().length > 0) {
                ext.append(" implements ");
                for (Type interf : clazz.interfaceTypes()) {
                    ext.append(parseType(clazz, interf), " ");
                }
            }
        }
        
        Tag[] since;
        XMLBuilder sinceXML;
        if ((since = clazz.tags("@since")).length > 0) {
            sinceXML = createDescription(clazz, since[0].inlineTags());
            sinceXML.addStringOption("class", "classSince since");
        } else {
            sinceXML = new XMLBuilder("p").addStringOption("class", "classSince since");
        }
        builder.append(sinceXML);
        builder.append(createDescription(clazz, clazz.inlineTags()));
        
        boolean flag = true;
        
        if (Arrays.stream(clazz.annotations()).noneMatch(e -> e.annotationType().simpleTypeName().equals("Library"))) {
            for (ConstructorDoc constructor : clazz.constructors()) {
                if (!constructor.isPublic()) continue;
                if (flag) {
                    flag = false;
                    builder.append(new XMLBuilder("h3", true, true).append("Constructors"));
                    builder.append(constructors = new XMLBuilder("div").addStringOption("class", "constructorDoc"));
                }
                constructors.append(parseConstructor(clazz, constructor));
            }
        }
        
        XMLBuilder shorts;
        builder.append(shorts = new XMLBuilder("div").addStringOption("class", "shortFieldMethods"));
        
        XMLBuilder fieldShorts = null;
        XMLBuilder methodShorts = null;
        
        XMLBuilder fields = null;
        flag = true;
        for (FieldDoc field : (clazz.isEnum() ? clazz.enumConstants() : clazz.fields())) {
            if (!field.isPublic()) continue;
            if (flag) {
                flag = false;
                builder.append(new XMLBuilder("h3", true, true).append("Fields"));
                builder.append(fields = new XMLBuilder("div").addStringOption("class", "fieldDoc"));
                shorts.append(fieldShorts = new XMLBuilder("div").addStringOption("class", "fieldShorts").append(new XMLBuilder("h4").append("Fields")));
            }
            fields.append(parseField(clazz, field));
            fieldShorts.append(
                new XMLBuilder("div").addStringOption("class", "shortField shortClassItem").append(
                    new XMLBuilder("a", true, true).addStringOption("href", "#" + memberId(field)).append(field.name()),
                    createFlags(field, true)
                )
            );
        }
        
        XMLBuilder methods = null;
        flag = true;
        for (MethodDoc method : clazz.methods()) {
            if (!method.isPublic()) continue;
            if (flag) {
                flag = false;
                builder.append(new XMLBuilder("h3", true, true).append("Methods"));
                builder.append(methods = new XMLBuilder("div").addStringOption("class", "methodDoc"));
                shorts.append(methodShorts = new XMLBuilder("div").addStringOption("class", "methodShorts").append(new XMLBuilder("h4").append("Methods")));
            }
            methods.append(parseMethod(clazz, method));
            methodShorts.append(
                new XMLBuilder("div").addStringOption("class", "shortMethod shortClassItem").append(
                    new XMLBuilder("a", true, true).addStringOption("href", "#" + memberId(method)).append(method.name() + "(" + Arrays.stream(method.parameters()).map(Parameter::name).collect(Collectors.joining(", ")) + ")"),
                    createFlags(method, true)
                )
            );
        }
        return builder;
    }
    
    public static Pair<String, Boolean> getURL(ClassDoc currentClass, Type type, MemberDoc method) {
        if (type != null) {
            if (type.isPrimitive()) return new Pair<>("", false);
            String pkg = type.asClassDoc().containingPackage().name();
            if (Main.externalPackages.containsKey(pkg)) {
                return new Pair<>(Main.externalPackages.get(pkg) + type.typeName() + ".html", true);
            } else if (pkg.startsWith("com.mojang") || pkg.startsWith("net.minecraft")) {
                return new Pair<>(Main.mappingViewerBaseURL + pkg.replaceAll("\\.", "/") + "/" + type.typeName(), true);
            } else if (Main.internalPackages.contains(pkg)) {
                StringBuilder url = new StringBuilder();
                if (!currentClass.qualifiedTypeName().equals(type.qualifiedTypeName())) {
                    for (String ignored : currentClass.containingPackage().name().split("\\.")) {
                        url.append("../");
                    }
                    url.append(pkg.replaceAll("\\.", "/")).append("/").append(type.typeName()).append(".html");
                } else if (method == null) {
                    url.append("#");
                }
                if (method != null) {
                    url.append("#").append(memberId(method));
                }
                if (url.length() == 0) return new Pair<>("", false);
                return new Pair<>(url.toString(), false);
            } else {
                return new Pair<>("", false);
            }
        } else {
            String url = "";
            if (method != null) {
                url += "#" + memberId(method);
            }
            if (url.isEmpty()) return new Pair<>("", false);
            return new Pair<>(url, false);
        }
    }
    
    public static boolean isGenericType(Type t) {
        return t.asTypeVariable() != null || t.asWildcardType() != null;
    }
    
    public static XMLBuilder parseType(ClassDoc currentClass, Type type) {
        XMLBuilder builder = new XMLBuilder("div", true).addStringOption("class", "typeParameter");
        XMLBuilder typeLink;
        Pair<String, Boolean> url = getURL(currentClass, type, null);
        builder.append(typeLink = new XMLBuilder(isGenericType(type) || type.isPrimitive() ? "p" : "a", true).addStringOption("href", url.getKey()).append(type.typeName()));
        if (url.getValue()) {
            typeLink.addStringOption("target", "_blank");
        }
        if (type.isPrimitive() || isGenericType(type)) typeLink.addStringOption("class", "type primitiveType");
        else if (typeLink.options.get("href").equals("\"\"")) typeLink.addStringOption("class", "type deadType");
        else typeLink.addStringOption("class", "type");
        
        ParameterizedType ptype = type.asParameterizedType();
        if (ptype != null && !ptype.qualifiedTypeName().startsWith("net.minecraft")) {
            builder.append("<");
            Type[] types = ptype.typeArguments();
            int i;
            for (i = 0; i < types.length - 1; ++i) {
                builder.append(parseType(currentClass, types[i]));
                builder.append(", ");
            }
            if (types.length > 0) builder.append(parseType(currentClass, types[i]));
            builder.append(">");
        }
        builder.append(type.dimension());
        return builder;
    }
    
    // TODO: make package @see/@link labels work too
    public static XMLBuilder parseSee(ClassDoc currentClass, SeeTag tag) {
        XMLBuilder builder = new XMLBuilder("a", true).addStringOption("class", "type");
        Pair<String, Boolean> url = getURL(currentClass, tag.referencedClass(), tag.referencedMember());
        builder.addStringOption("href", url.getKey());
        if (url.getValue()) {
            builder.addStringOption("target", "_blank");
        }
        if (builder.options.get("href").equals("\"\"")) builder.addStringOption("class", "type deadType");
        else builder.addStringOption("class", "type");
        
        if (tag.label() != null && !tag.label().isEmpty()) {
            builder.append(tag.label());
        } else if (tag.referencedClass() != null || tag.referencedMember() != null) {
            if (tag.referencedClass() != null) {
                builder.append(tag.referencedClass().typeName());
            }
            if (tag.referencedMember() != null) {
                builder.append("#").append(tag.referencedMember().name());
            }
        } else {
            builder.append(tag.text());
        }
        return builder;
    }
    
    public static XMLBuilder createDescription(ClassDoc currentClass, Tag[] tags) {
        XMLBuilder builder = new XMLBuilder("p", true, true).addStringOption("class", "description");
        for (Tag t : tags) {
            if (t instanceof SeeTag) {
                builder.append(parseSee(currentClass, (SeeTag) t));
            } else if (t.name().equals("@code")) {
                builder.append(new XMLBuilder("code", true).addStringOption("class","inlineCode").append(t.text()));
            } else {
                builder.append(t.text().replaceAll("\n\\s*\n", "<br><br>"));
            }
        }
        return builder;
    }
    
    public static String memberId(MemberDoc methodDoc) {
        StringBuilder stringBuilder = new StringBuilder();
        if (methodDoc instanceof ConstructorDoc) {
            stringBuilder.append("constructor");
        } else {
            stringBuilder.append(methodDoc.name());
        }
        if (methodDoc instanceof ExecutableMemberDoc) {
            for (Parameter param : ((ExecutableMemberDoc) methodDoc).parameters()) {
                stringBuilder.append("-");
                stringBuilder.append(param.type().typeName());
            }
            stringBuilder.append("-");
        }
        return stringBuilder.toString();
    }
    
    public static XMLBuilder createFlags(ProgramElementDoc member, boolean shortFlags) {
        XMLBuilder flags = new XMLBuilder("div").addStringOption("class", shortFlags ? "shortFlags" : "flags");
        if (member.isFinal()) {
            flags.append(
                new XMLBuilder("div", true, true).addStringOption("class", "flag finalFlag").append(shortFlags ? "F" : "Final")
            );
        }
        if (member.isStatic()) {
            flags.append(
                new XMLBuilder("div", true, true).addStringOption("class", "flag staticFlag").append(shortFlags ? "S" : "Static")
            );
        }
        if (member.isInterface()) {
            flags.append(
                new XMLBuilder("div", true, true).addStringOption("class", "flag interfaceFlag").append(shortFlags ? "I" : "Interface")
            );
        }
        if (member.isEnum()) {
            flags.append(
                new XMLBuilder("div", true, true).addStringOption("class", "flag enumFlag").append(shortFlags ? "E" : "Enum")
            );
        }
        if ((member instanceof ClassDoc && !member.isInterface() && ((ClassDoc) member).isAbstract()) || (member instanceof MethodDoc && !member.containingClass().isInterface() && ((MethodDoc) member).isAbstract())) {
            flags.append(
                new XMLBuilder("div", true, true).addStringOption("class", "flag abstractFlag").append(shortFlags ? "A" : "Abstract")
            );
        }
        if (member.tags("deprecated").length > 0 || Arrays.stream(member.annotations()).anyMatch(a -> a.annotationType().simpleTypeName().equals("Deprecated"))) {
            flags.append(
                new XMLBuilder("div", true, true).addStringOption("class", "flag deprecatedFlag").append(shortFlags ? "D" : "Deprecated")
            );
        }
        return flags;
    }
    
    // TODO: inherit javadoc from super-class's interfaces
    public static XMLBuilder parseMethod(ClassDoc currentClass, MethodDoc methodDoc) {
        XMLBuilder method = new XMLBuilder("div").addStringOption("class", "constructor classItem").addStringOption("id", memberId(methodDoc));
        method.append(new XMLBuilder("h4", true).addStringOption("class", "methodTitle classItemTitle").append(
            "." + methodDoc.name() + "(",
            new XMLBuilder("div", true).addStringOption("class", "methodParams").append(Arrays.stream(methodDoc.parameters()).map(Parameter::name).collect(Collectors.joining(", "))),
            ")"
        ));
        method.append(createFlags(methodDoc, false));
        Tag[] since;
        XMLBuilder sinceXML;
        if ((since = methodDoc.tags("@since")).length > 0) {
            sinceXML = createDescription(currentClass, since[0].inlineTags());
            sinceXML.addStringOption("class", "methodSince since");
        } else {
            sinceXML = new XMLBuilder("p").addStringOption("class", "methodSince since");
        }
        method.append(sinceXML);
        MethodDoc superMethod = methodDoc;
        do {
            method.append(new XMLBuilder("div").addStringOption("class", "methodDesc classItemDesc").append(createDescription(currentClass, superMethod.inlineTags())));
        } while ((superMethod = superMethod.overriddenMethod()) != null);
        for (ClassDoc interfaceDoc : currentClass.interfaces()) {
            for (MethodDoc m : interfaceDoc.methods()) {
                if (methodDoc.overrides(m)) {
                    method.append(new XMLBuilder("div").addStringOption("class", "methodDesc classItemDesc").append(createDescription(currentClass, m.inlineTags())));
                    break;
                }
            }
        }
        if (methodDoc.parameters().length > 0) {
            XMLBuilder paramTable;
            method.append(new XMLBuilder("table").addStringOption("class", "paramTable").append(
                new XMLBuilder("thead").append(
                    new XMLBuilder("th", true, true).append("Parameter"),
                    new XMLBuilder("th", true, true).append("Type"),
                    new XMLBuilder("th", true, true).append("Description")
                ),
                paramTable = new XMLBuilder("tbody")
            ));
            Map<String, Tag[]> params = new HashMap<>();
            superMethod = methodDoc;
            do {
                for (ParamTag tag : superMethod.paramTags()) {
                    if (!params.containsKey(tag.parameterName()) && tag.inlineTags().length > 0)
                        params.put(tag.parameterName(), tag.inlineTags());
                }
            } while ((superMethod = superMethod.overriddenMethod()) != null);
            for (ClassDoc interfaceDoc : currentClass.interfaces()) {
                for (MethodDoc m : interfaceDoc.methods()) {
                    if (methodDoc.overrides(m)) {
                        for (ParamTag tag : m.paramTags()) {
                            if (!params.containsKey(tag.parameterName()) && tag.inlineTags().length > 0)
                                params.put(tag.parameterName(), tag.inlineTags());
                        }
                        break;
                    }
                }
            }
            for (Parameter param : methodDoc.parameters()) {
                paramTable.append(new XMLBuilder("tr").append(
                    new XMLBuilder("td", true, true).append(param.name()),
                    new XMLBuilder("td", true, true).append(parseType(currentClass, param.type())),
                    new XMLBuilder("td", true, true).append(createDescription(currentClass, params.getOrDefault(param.name(), new Tag[0])).addStringOption("class", "paramDescription"))
                ));
            }
        }
        XMLBuilder returnVal;
        method.append(returnVal = new XMLBuilder("div").addStringOption("class", "methodReturn classItemType").append(
            new XMLBuilder("h5", true, true).addStringOption("class", "methodReturnTitle classItemTypeTitle").append("Returns: ", parseType(currentClass, methodDoc.returnType()))
        ));
        superMethod = methodDoc;
        do {
            if (superMethod.tags("@return").length > 0) {
                returnVal.append(createDescription(currentClass, superMethod.tags("@return")[0].inlineTags()).addStringOption("class", "methodReturnDesc classItemTypeDesc"));
                break;
            }
        } while ((superMethod = superMethod.overriddenMethod()) != null);
        if (superMethod == null) {
            for (ClassDoc interfaceDoc : currentClass.interfaces()) {
                boolean flag = false;
                for (MethodDoc m : interfaceDoc.methods()) {
                    if (methodDoc.overrides(m)) {
                        if (m.tags("@return").length > 0) {
                            returnVal.append(createDescription(currentClass, m.tags("@return")[0].inlineTags()).addStringOption("class", "methodReturnDesc classItemTypeDesc"));
                            flag = true;
                        }
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }
        }
        return method;
    }
    
    public static XMLBuilder parseConstructor(ClassDoc currentClass, ConstructorDoc constructorDoc) {
        XMLBuilder constructor = new XMLBuilder("div").addStringOption("class", "constructor classItem").addStringOption("id", memberId(constructorDoc));
        constructor.append(new XMLBuilder("h4", true).addStringOption("class", "constructorTitle classItemTitle").append(
            "new " + currentClass.typeName() + "(",
            new XMLBuilder("div", true).addStringOption("class", "constructorParams").append(Arrays.stream(constructorDoc.parameters()).map(Parameter::name).collect(Collectors.joining(", "))),
            ")"
        ));
        constructor.append(createFlags(constructorDoc, false));
        Tag[] since;
        XMLBuilder sinceXML;
        if ((since = constructorDoc.tags("@since")).length > 0) {
            sinceXML = createDescription(currentClass, since[0].inlineTags());
            sinceXML.addStringOption("class", "constructorSince since");
        } else {
            sinceXML = new XMLBuilder("p").addStringOption("class", "constructorSince since");
        }
        constructor.append(sinceXML);
        constructor.append(new XMLBuilder("div").addStringOption("class", "constructorDesc classItemDesc").append(createDescription(currentClass, constructorDoc.inlineTags())));
        if (constructorDoc.parameters().length > 0) {
            XMLBuilder paramTable;
            constructor.append(new XMLBuilder("table").addStringOption("class", "paramTable").append(
                new XMLBuilder("thead").append(
                    new XMLBuilder("th", true, true).append("Parameter"),
                    new XMLBuilder("th", true, true).append("Type"),
                    new XMLBuilder("th", true, true).append("Description")
                ),
                paramTable = new XMLBuilder("tbody")
            ));
            Map<String, Tag[]> params = new HashMap<>();
            for (ParamTag tag : constructorDoc.paramTags()) {
                params.put(tag.parameterName(), tag.inlineTags());
            }
            for (Parameter param : constructorDoc.parameters()) {
                paramTable.append(new XMLBuilder("tr").append(
                    new XMLBuilder("td", true, true).append(param.name()),
                    new XMLBuilder("td", true, true).append(parseType(currentClass, param.type())),
                    new XMLBuilder("td", true, true).append(createDescription(currentClass, params.getOrDefault(param.name(), new Tag[0])).addStringOption("class", "paramDescription"))
                ));
            }
        }
        return constructor;
    }
    
    public static XMLBuilder parseField(ClassDoc currentClass, FieldDoc fieldDoc) {
        XMLBuilder field = new XMLBuilder("div").addStringOption("class", "field classItem").addStringOption("id", memberId(fieldDoc));
        field.append(new XMLBuilder("h4", true).addStringOption("class", "fieldTitle classItemTitle").append(
            "." + fieldDoc.name()
        ));
        field.append(createFlags(fieldDoc, false));
        Tag[] since;
        XMLBuilder sinceXML;
        if ((since = fieldDoc.tags("@since")).length > 0) {
            sinceXML = createDescription(currentClass, since[0].inlineTags());
            sinceXML.addStringOption("class", "fieldSince since");
        } else {
            sinceXML = new XMLBuilder("p").addStringOption("class", "fieldSince since");
        }
        field.append(sinceXML);
        field.append(new XMLBuilder("div").addStringOption("class", "fieldDesc classItemDesc").append(createDescription(currentClass, fieldDoc.inlineTags())));
        field.append(new XMLBuilder("div").addStringOption("class", "methodReturn classItemType").append(
            new XMLBuilder("h5", true, true).addStringOption("class", "fieldTypeTitle classItemTypeTitle").append("Type: ", parseType(currentClass, fieldDoc.type()))
        ));
        return field;
    }
}
