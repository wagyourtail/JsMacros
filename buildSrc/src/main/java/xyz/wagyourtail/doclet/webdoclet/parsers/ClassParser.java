package xyz.wagyourtail.doclet.webdoclet.parsers;

import com.sun.source.doctree.*;
import com.sun.source.util.DocTreePath;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.XMLBuilder;
import xyz.wagyourtail.doclet.webdoclet.Main;
import xyz.wagyourtail.doclet.webdoclet.options.Links;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ClassParser {
    private final String group;
    private final String alias;
    public TypeElement type;

    public ClassParser(TypeElement type, String group, String alias) {
        this.type = type;
        this.group = group;
        this.alias = alias;
    }

    /**
     * @return class name with $ for inner class
     */
    private static String getClassName(TypeElement type) {
        StringBuilder s = new StringBuilder(type.getSimpleName());
        Element t2 = type.getEnclosingElement();
        while (t2.getKind() == ElementKind.INTERFACE || t2.getKind() == ElementKind.CLASS) {
            s.insert(0, t2.getSimpleName() + "$");
            t2 = t2.getEnclosingElement();
        }
        return s.toString();
    }

    /**
     * @return package name with . separators
     */
    private static String getPackage(TypeElement type) {
        Element t2 = type;
        while (t2.getKind() != ElementKind.PACKAGE) t2 = t2.getEnclosingElement();

        return ((PackageElement) t2).getQualifiedName().toString();
    }

    public String getPathPart() {
        return getPackage(type).replaceAll("\\.", "/") + "/" + getClassName(type).replaceAll("\\$", ".");
    }

    /**
     * nothing much
     * @return up dir string
     */
    private String getUpDir(int extra) {
        StringBuilder s = new StringBuilder();
        for (String ignored : getPackage(type).split("\\.")) {
            s.append("../");
        }
        s.append("../".repeat(Math.max(0, extra)));
        return s.toString();
    }

    /**
     * spec
     * C\\t<searchname>\\t<linkname>\\t<?group>\\t<?alias>
     * F\\t<fieldname>\\t<fieldlink>
     * M\\t<methodname>\\t<methodlink>
     */
    public String genSearchData() {
        StringBuilder s = new StringBuilder();
        String cname = getClassName(type).replaceAll("\\$", ".");
        s.append("C\t").append(cname).append("\t")
            .append(getPathPart());
        if (group != null) s.append("\t").append(group);
        if (alias != null) s.append("\t").append(alias);
        s.append("\n");
        for (Element el : type.getEnclosedElements()) {
            switch (el.getKind()) {
                case ENUM_CONSTANT, FIELD -> s.append("F\t").append(cname).append("#").append(memberName(el))
                    .append("\t").append(getPathPart()).append("#").append(memberId(el)).append("\n");
                case METHOD -> s.append("M\t").append(cname).append("#").append(memberName(el))
                    .append("\t").append(getPathPart()).append("#").append(memberId(el)).append("\n");
                default -> {}
            }
        }
        return s.toString();
    }

    public String genXML() {
        return "<!DOCTYPE html>\n" + new XMLBuilder("html").append(
            new XMLBuilder("head").append(
                new XMLBuilder("link", true, true).addStringOption("rel", "stylesheet").addStringOption("href", getUpDir(1) + "classContent.css")
            ),
            new XMLBuilder("body").append(
                new XMLBuilder("header").append(
                    new XMLBuilder("a").addStringOption("href", getUpDir(1)).append(
                        "<----- Return to main JsMacros docs page."
                    )
                )),
            parseClass()
        );
    }

    private XMLBuilder parseClass() {
        XMLBuilder builder = new XMLBuilder("main").setClass("classDoc");
        XMLBuilder subClasses;
        builder.append(subClasses = new XMLBuilder("div").setID("subClasses"));
        for (Element subClass : Main.elements.stream().filter(e -> (e.getKind().isClass() || e.getKind().isInterface()) && ((TypeElement) e).getSuperclass().equals(type.asType())).collect(Collectors.toList())) {
            subClasses.append(parseType(subClass.asType()), " ");
        }
        XMLBuilder cname;
        builder.append(cname = new XMLBuilder("h2", true, true).setClass("classTitle").append(getClassName(type)));

        List<? extends TypeParameterElement> params = type.getTypeParameters();
        if (params != null && !params.isEmpty()) {
            cname.append("<");
            for (TypeParameterElement param : params) {
                cname.append(parseType(param.asType()), ", ");
            }
            cname.pop();
            cname.append(">");
        }

        builder.append(createFlags(type, false));
        TypeMirror sup = type.getSuperclass();
        List<? extends TypeMirror> ifaces = type.getInterfaces();
        if (sup != null || (ifaces != null && !ifaces.isEmpty())) {
            XMLBuilder ext;
            builder.append(ext = new XMLBuilder("h4", true, true).addStringOption("class", "classExtends"));
            if (sup != null) {
                ext.append("extends ", parseType(sup));
            }
            if (ifaces != null && !ifaces.isEmpty()) {
                ext.append(" implements ");
                for (TypeMirror iface : ifaces) {
                    ext.append(parseType(iface), " ");
                }
            }
        }

        builder.append(getSince(type));
        builder.append(getDescription(type));


        AtomicBoolean firstFlag = new AtomicBoolean(true);
        AtomicReference<XMLBuilder> constructors = new AtomicReference<>();
        //CONSTRUCTORS
        if (!group.equals("Library")) {
            type.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.CONSTRUCTOR).forEach(el -> {
                if (!el.getModifiers().contains(Modifier.PUBLIC)) return;
                if (firstFlag.get()) {
                    firstFlag.set(false);
                    builder.append(new XMLBuilder("h3", true, true).append("Constructors"));
                    XMLBuilder con = new XMLBuilder("div").setClass("constructorDoc");
                    builder.append(con);
                    constructors.set(con);
                }
                constructors.get().append(parseConstructor((ExecutableElement) el));
            });
        }

        XMLBuilder shorts;
        builder.append(shorts = new XMLBuilder("div").setClass("shortFieldMethods"));

        AtomicReference<XMLBuilder> fieldShorts = new AtomicReference<>();
        AtomicReference<XMLBuilder> fields = new AtomicReference<>();

        firstFlag.set(true);
        type.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.FIELD || e.getKind() == ElementKind.ENUM_CONSTANT).forEach(el -> {
            if (!el.getModifiers().contains(Modifier.PUBLIC)) return;
            if (firstFlag.get()) {
                firstFlag.set(false);

                builder.append(new XMLBuilder("h3", true, true).append("Fields"));

                XMLBuilder f = new XMLBuilder("div").setClass("fieldDoc");
                builder.append(f);
                XMLBuilder fs = new XMLBuilder("div").setClass("fieldShorts").append(new XMLBuilder("h4").append("Fields"));
                shorts.append(fs);

                fields.set(f);
                fieldShorts.set(fs);
            }

            fields.get().append(parseField(el));
            fieldShorts.get().append(
                new XMLBuilder("div").setClass("shortField shortClassItem").append(
                    new XMLBuilder("a", true, true).addStringOption("href", getURL(el).getKey()).append(memberName(el)),
                    createFlags(el, true)
                )
            );
        });


        AtomicReference<XMLBuilder> methodShorts = new AtomicReference<>();
        AtomicReference<XMLBuilder> methods = new AtomicReference<>();

        firstFlag.set(true);
        type.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.METHOD).forEach(el -> {
            if (!el.getModifiers().contains(Modifier.PUBLIC)) return;
            if (firstFlag.get()) {
                firstFlag.set(false);

                builder.append(new XMLBuilder("h3", true, true).append("Methods"));

                XMLBuilder m = new XMLBuilder("div").setClass("methodDoc");
                builder.append(m);
                XMLBuilder ms = new XMLBuilder("div").setClass("methodShorts").append(new XMLBuilder("h4").append("Methods"));
                shorts.append(ms);

                methods.set(m);
                methodShorts.set(ms);
            }
            methods.get().append(parseMethod((ExecutableElement) el));
            methodShorts.get().append(
                new XMLBuilder("div").setClass("shortMethod shortClassItem").append(
                    new XMLBuilder("a", true, true).addStringOption("href", getURL(el).getKey()).append(memberName(el)),
                    createFlags(el, true)
                )
            );
        });

        return builder;
    }

    private XMLBuilder parseConstructor(ExecutableElement element) {
        XMLBuilder constructor = new XMLBuilder("div").setClass("constructor classItem").setID(memberId(element));
        constructor.append(new XMLBuilder("h4").setClass("constructorTitle classItemTitle").append(
            "new ", getClassName((TypeElement) element.getEnclosingElement()), "(",
                createTitleParams(element).setClass("constructorParams"),
            ")"
        ));
        constructor.append(createFlags(element, false));
        constructor.append(getSince(element));

        constructor.append(new XMLBuilder("div").setClass("constructorDesc classItemDesc")
            .append(getDescription(element)));

        XMLBuilder paramTable = createParamTable(element);
        if (paramTable != null) constructor.append(paramTable);

        return constructor;
    }

    private XMLBuilder parseMethod(ExecutableElement element) {
        XMLBuilder method = new XMLBuilder("div").setClass("method classItem").setID(memberId(element));
        //TODO: type params
        method.append(new XMLBuilder("h4", true).setClass("methodTitle classItemTitle").append(
            ".", element.getSimpleName(), "(",
                createTitleParams(element).setClass("methodParams"),
            ")"
        ));
        method.append(createFlags(element, false));
        method.append(getSince(element));

        method.append(new XMLBuilder("div").setClass("methodDesc classItemDesc").append(getDescription(element)));

        XMLBuilder paramTable = createParamTable(element);
        if (paramTable != null) method.append(paramTable);

        method.append(new XMLBuilder("div").setClass("methodReturn classItemType").append(
            new XMLBuilder("h5", true, true).setClass("methodReturnTitle classItemTypeTitle").append(
                "Returns: ", parseType(element.getReturnType())
            ),
            getReturnDescription(element).setClass("methodReturnDesc classItemTypeDesc")
        ));

        return method;
    }

    private XMLBuilder getReturnDescription(ExecutableElement element) {
        DocCommentTree dct = Main.treeUtils.getDocCommentTree(element);
        if (dct == null) return new XMLBuilder("p");
        ReturnTree t = (ReturnTree) dct.getBlockTags().stream().filter(e -> e.getKind() == DocTree.Kind.RETURN).findFirst().orElse(null);
        if (t == null) return new XMLBuilder("p");
        return createDescription(element, t.getDescription());
    }

    private XMLBuilder createTitleParams(ExecutableElement element) {
        XMLBuilder builder = new XMLBuilder("div", true);
        boolean flag = false;
        for (VariableElement parameter : element.getParameters()) {
            flag = true;
            builder.append(parameter.getSimpleName(), ", ");
        }
        if (flag) builder.pop();
        return builder;
    }

    private XMLBuilder createParamTable(ExecutableElement element) {
        List<? extends VariableElement> params = element.getParameters();
        if (params == null || params.isEmpty()) return null;
        XMLBuilder body;
        XMLBuilder table = new XMLBuilder("table").setClass("paramTable").append(
            new XMLBuilder("thead").append(
                new XMLBuilder("th", true, true).append("Parameter"),
                new XMLBuilder("th", true, true).append("Type"),
                new XMLBuilder("th", true, true).append("Description")
            ),
            body = new XMLBuilder("tbody")
        );
        Map<String, XMLBuilder> paramDescMap = getParamDescriptions(element);
        for (VariableElement param : params) {
            body.append(new XMLBuilder("tr").append(
                new XMLBuilder("td", true, true).append(param.getSimpleName()),
                new XMLBuilder("td", true, true).append(parseType(param.asType())),
                new XMLBuilder("td", true, true).append(paramDescMap.get(param.getSimpleName().toString()))
            ));
        }
        return table;
    }

    private XMLBuilder parseField(Element element) {
        XMLBuilder field = new XMLBuilder("div").setClass("field classItem").setID(memberId(element));
        field.append(new XMLBuilder("h4", true).setClass("classItemTitle").append(
            ".", memberName(element)
        ));
        field.append(createFlags(element, false));
        field.append(getSince(element));

        field.append(new XMLBuilder("div").setClass("fieldDesc classItemDesc").append(getDescription(element)));

        field.append(new XMLBuilder("div").setClass("fieldReturn classItemType").append(
            new XMLBuilder("h5", true, true).setClass("fieldTypeTitle classItemTypeTitle").append(
                "Type: ", parseType(element.asType())
            )
        ));

        return field;
    }

    public Map<String, XMLBuilder> getParamDescriptions(ExecutableElement element) {
        Map<String, XMLBuilder> paramMap = new HashMap<>();
        DocCommentTree comment = Main.treeUtils.getDocCommentTree(element);
        if (comment == null) return paramMap;
        comment.getBlockTags().stream().filter(e -> e.getKind() == DocTree.Kind.PARAM).forEach(e -> paramMap.put(((ParamTree) e).getName().getName().toString(), createDescription(element, ((ParamTree) e).getDescription())));
        return paramMap;
    }

    private XMLBuilder getSince(Element element) {
        DocCommentTree tree = Main.treeUtils.getDocCommentTree(element);
        SinceTree since = tree == null ? null : (SinceTree) tree.getBlockTags().stream().filter(e -> e.getKind().equals(DocTree.Kind.SINCE)).findFirst().orElse(null);
        if (since == null) {
            switch (element.getKind()) {
                case ENUM, CLASS, INTERFACE, ANNOTATION_TYPE -> {
                    return new XMLBuilder("p").setClass("classSince since");
                }
                case ENUM_CONSTANT, FIELD -> {
                    return new XMLBuilder("p").setClass("fieldSince since");
                }
                case METHOD -> {
                    return new XMLBuilder("p").setClass("methodSince since");
                }
                case CONSTRUCTOR -> {
                    return new XMLBuilder("p").setClass("constructorSince since");
                }
                default -> throw new UnsupportedOperationException(element.getKind().toString());
            }
        } else {
            XMLBuilder s = createDescription(element, since.getBody());
            switch (element.getKind()) {
                case ENUM, CLASS, INTERFACE, ANNOTATION_TYPE -> {
                    return s.setClass("classSince since");
                }
                case ENUM_CONSTANT, FIELD -> {
                    return s.setClass("fieldSince since");
                }
                case METHOD -> {
                    return s.setClass("methodSince since");
                }
                case CONSTRUCTOR -> {
                    return s.setClass("constructorSince since");
                }
                default -> throw new UnsupportedOperationException(element.getKind().toString());
            }
        }
    }

    private XMLBuilder getDescription(Element element) {
        DocCommentTree tree = Main.treeUtils.getDocCommentTree(element);

        return createDescription(element, tree == null ? List.of() : tree.getFullBody());
    }

    private XMLBuilder createDescription(Element el, List<? extends DocTree> inlinedoc) {
        XMLBuilder s = new XMLBuilder("p", true, true).setClass("description");
        for (DocTree docTree : inlinedoc) {
            switch (docTree.getKind()) {
                case LINK, LINK_PLAIN -> {
                    Element ele = Main.treeUtils.getElement(new DocTreePath(new DocTreePath(Main.treeUtils.getPath(el), Main.treeUtils.getDocCommentTree(el)), ((LinkTree) docTree).getReference()));
                    if (ele != null) {
                        XMLBuilder link;
                        Pair<String, Boolean> url = getURL(ele);

                        s.append(link = new XMLBuilder("a", true).addStringOption("href", url.getKey()));

                        if (List.of(ElementKind.INTERFACE, ElementKind.CLASS, ElementKind.ANNOTATION_TYPE, ElementKind.ENUM).contains(ele.getKind())) {
                            link.append(getClassName((TypeElement) ele));
                        } else {
                            link.append(getClassName((TypeElement) ele.getEnclosingElement()), "#", ele.toString());
                        }

                        if (url.getValue()) {
                            link.addStringOption("target", "_blank");
                        }
                        if (link.options.get("href").equals("\"\"")) link.setClass("type deadType");
                        else link.setClass("type");

                    } else {
                        s.append(((LinkTree) docTree).getReference().getSignature());
                    }
                }
                case CODE -> s.append(new XMLBuilder("code", true).setClass("inlineCode").append(((LiteralTree)docTree).getBody()));
                default -> s.append(docTree);
            }
        }
        return s;
    }

    private XMLBuilder parseType(TypeMirror type) {
        XMLBuilder builder = new XMLBuilder("div", true).setClass("typeParameter");
        XMLBuilder typeLink;
        switch (type.getKind()) {
            case BOOLEAN, BYTE, SHORT, INT, LONG, CHAR, FLOAT, DOUBLE, VOID, NONE -> {
                //isPrimitive
                builder.append(typeLink = new XMLBuilder("p", true).append(type));
                typeLink.setClass("type primitiveType");
            }
            case ARRAY -> {
                return parseType(((ArrayType) type).getComponentType()).append("[]");
            }
            case DECLARED -> {
                Pair<String, Boolean> url = getURL(((DeclaredType) type).asElement());
                builder.append(typeLink = new XMLBuilder("a", true).addStringOption("href", url.getKey()).append(getClassName((TypeElement) ((DeclaredType) type).asElement())));

                if (url.getValue()) {
                    typeLink.addStringOption("target", "_blank");
                }
                if (typeLink.options.get("href").equals("\"\"")) typeLink.setClass("type deadType");
                else typeLink.setClass("type");

                List<? extends TypeParameterElement> params = ((TypeElement) ((DeclaredType) type).asElement()).getTypeParameters();
                if (params != null && !params.isEmpty()) {
                    builder.append("<");
                    for (TypeParameterElement param : params) {
                        builder.append(parseType(param.asType()), ", ");
                    }
                    builder.pop();
                    builder.append(">");
                }
            }
            case TYPEVAR -> {
                builder.append(typeLink = new XMLBuilder("p", true));
                typeLink.setClass("type primitiveType");
                typeLink.append(((TypeVariable)type).asElement().getSimpleName());
            }
            case WILDCARD -> {
                builder.append(typeLink = new XMLBuilder("p", true));
                typeLink.setClass("type primitiveType");
                typeLink.append("?");
            }
        }
        return builder;
    }

    private Pair<String, Boolean> getURL(Element type) {
        if (type.asType().getKind().isPrimitive()) return new Pair<>("", false);
        Element clazz = type;
        while (!(clazz instanceof TypeElement)) {
            clazz = clazz.getEnclosingElement();
        }
        if (!clazz.equals(this.type)) {
            String pkg = getPackage((TypeElement) clazz);
            if (Main.internalClasses.containsKey(clazz)) {
                StringBuilder s = new StringBuilder(getUpDir(0));
                s.append(Main.internalClasses.get(clazz).getPathPart()).append(".html");
                if (type != clazz) {
                    s.append("#").append(memberId(type));
                }
                return new Pair<>(s.toString(), false);
            } else if (Links.externalPackages.containsKey(pkg)) {
                return new Pair<>(Links.externalPackages.get(pkg) + getClassName((TypeElement) clazz) + ".html", true);
            } else if (pkg.startsWith("com.mojang") || pkg.startsWith("net.minecraft")) {
                return new Pair<>(Main.mappingViewerURL + pkg.replaceAll("\\.", "/") + "/" + getClassName((TypeElement) clazz), true);
            } else {
                return new Pair<>("", false);
            }
        } else {
            StringBuilder s = new StringBuilder();
            s.append("#");
            if (type != clazz) {
                s.append(memberId(type));
            }
            return new Pair<>(s.toString(), false);
        }
    }

    private static String memberId(Element member) {
        StringBuilder s = new StringBuilder();
        switch (member.getKind()) {
            case ENUM_CONSTANT, FIELD -> s.append(member.getSimpleName());
            case CONSTRUCTOR, METHOD -> {
                if (member.getKind() == ElementKind.METHOD) s.append(member.getSimpleName());
                else s.append("constructor");
                for (VariableElement parameter : ((ExecutableElement) member).getParameters()) {
                    s.append("-").append(getTypeMirrorName(parameter.asType()));
                }
                s.append("-");
            }
            case TYPE_PARAMETER -> {}
            default -> throw new UnsupportedOperationException(String.valueOf(member.getKind()));
        }

        return s.toString();
    }

    private static String memberName(Element member) {
        StringBuilder s = new StringBuilder();
        switch (member.getKind()) {
            case ENUM_CONSTANT, FIELD -> s.append(member.getSimpleName());
            case METHOD -> {
                s.append(member.getSimpleName()).append("(");
                for (VariableElement parameter : ((ExecutableElement) member).getParameters()) {
                    s.append(parameter.getSimpleName()).append(", ");
                }
                s.setLength(s.length() - 2);
                s.append(")");
            }
            default -> throw new UnsupportedOperationException(String.valueOf(member.getKind()));
        }
        return s.toString();
    }

    private static String getTypeMirrorName(TypeMirror type) {
        switch (type.getKind()) {
            case BOOLEAN -> {
                return "boolean";
            }
            case BYTE -> {
                return "byte";
            }
            case SHORT -> {
                return "short";
            }
            case INT -> {
                return "int";
            }
            case LONG -> {
                return "long";
            }
            case CHAR -> {
                return "char";
            }
            case FLOAT -> {
                return "float";
            }
            case DOUBLE -> {
                return "double";
            }
            case VOID, NONE -> {
                return "void";
            }
            case NULL -> {
                return "null";
            }
            case ARRAY -> {
                return getTypeMirrorName(((ArrayType) type).getComponentType()) + "[]";
            }
            case DECLARED -> {
                return getClassName((TypeElement) ((DeclaredType) type).asElement());
            }
            case TYPEVAR -> {
                return ((TypeVariable) type).asElement().getSimpleName().toString();
            }
            case WILDCARD -> {
                return "?";
            }
            default -> throw new UnsupportedOperationException(String.valueOf(type.getKind()));
        }
    }

    private static XMLBuilder createFlags(Element member, boolean shortFlags) {
        XMLBuilder flags = new XMLBuilder("div").setClass(shortFlags ? "shortFlags" : "flags");
        for (Modifier modifier : member.getModifiers()) {
            switch (modifier) {
                case ABSTRACT -> {
                    if (member.getKind() != ElementKind.INTERFACE && member.getEnclosingElement().getKind() != ElementKind.INTERFACE) {
                        flags.append(
                            new XMLBuilder("div", true, true).setClass("flag abstractFlag").append(shortFlags ? "A" : "Abstract")
                        );
                    }
                }
                case STATIC -> flags.append(
                    new XMLBuilder("div", true, true).addStringOption("class", "flag staticFlag").append(shortFlags ? "S" : "Static")
                );
                case FINAL -> flags.append(
                    new XMLBuilder("div", true, true).addStringOption("class", "flag finalFlag").append(shortFlags ? "F" : "Final")
                );
                default -> {}
            }
        }
        if (member.getKind() == ElementKind.ENUM || member.getKind() == ElementKind.ENUM_CONSTANT) {
            flags.append(
                new XMLBuilder("div", true, true).addStringOption("class", "flag enumFlag").append(shortFlags ? "E" : "Enum")
            );
        }
        if (member.getKind() == ElementKind.INTERFACE) {
            flags.append(
                new XMLBuilder("div", true, true).addStringOption("class", "flag interfaceFlag").append(shortFlags ? "I" : "Interface")
            );
        }
        if (member.getAnnotation(Deprecated.class) != null) {
            flags.append(
                new XMLBuilder("div", true, true).addStringOption("class", "flag deprecatedFlag").append(shortFlags ? "D" : "Deprecated")
            );
        }
        return flags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassParser that)) return false;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

}
