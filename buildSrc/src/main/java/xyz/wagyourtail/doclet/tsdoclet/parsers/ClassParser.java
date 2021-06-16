package xyz.wagyourtail.doclet.tsdoclet.parsers;

import xyz.wagyourtail.StringHelpers;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClassParser extends AbstractParser {
    public ClassParser(TypeElement type) {
        super(type);
    }

    private String getClassName() {
        StringBuilder s = new StringBuilder(type.getSimpleName());
        Element type = this.type.getEnclosingElement();
        while (type.getKind() == ElementKind.INTERFACE || type.getKind() == ElementKind.CLASS) {
            s.insert(0, type.getSimpleName() + "$");
            type = type.getEnclosingElement();
        }
        List<? extends TypeParameterElement> params = this.type.getTypeParameters();
        if (params != null && !params.isEmpty()) {
            s.append("<");
            for (TypeParameterElement param : params) {
                s.append(transformType(param.asType())).append(", ");
            }
            s.setLength(s.length() - 2);
            s.append(">");
        }
        return s.toString();
    }

    private String buildExtends() {
        StringBuilder s = new StringBuilder();
        String sup = transformType(type.getSuperclass());
        if (sup.equals("void")) {
            if (type.getKind().isInterface()) {
                s.append("Java.Interface");
            } else {
                s.append("Java.Object");
            }
        } else if (!sup.equals("/* minecraft classes, as any, because obfuscation: */ any")) {
            s.append(sup);
        } else {
            s.append("/* supressed minecraft class */ Java.Object");
        }

        List<? extends TypeMirror> iface = type.getInterfaces();
        if (iface != null && !iface.isEmpty()) {
            for (TypeMirror ifa : iface) {
                sup = transformType(ifa);
                if (sup.equals("/* minecraft classes, as any, because obfuscation: */ any")) {
                    s.append(", ").append("/* supressed minecraft class */ Java.Interface");
                } else {
                    s.append(", ").append(sup);
                }
            }
        }
        return s.toString();
    }

    @Override
    public String genTSInterface() {
        Set<Element> fields = new LinkedHashSet<>();
        Set<Element> methods = new LinkedHashSet<>();

        for (Element el : type.getEnclosedElements()) {
            if (el.getModifiers().contains(Modifier.PUBLIC)) {
                switch (el.getKind()) {
                    case FIELD, ENUM_CONSTANT -> fields.add(el);
                    case METHOD -> methods.add(el);
                    default -> {}
                }
            }
        }

        //TODO: extends
        return "export interface " + getClassName() + " extends " + buildExtends() + " {\n" +
            StringHelpers.tabIn(genFields(fields)) +
            "\n\n" +
            StringHelpers.tabIn(genMethods(methods)) +
            "\n}";
    }

}
