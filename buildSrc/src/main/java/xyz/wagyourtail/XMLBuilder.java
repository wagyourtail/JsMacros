package xyz.wagyourtail;

import java.util.*;

public class XMLBuilder {
    public final Map<String, String> options = new HashMap<>();
    public final List<Object> children = new LinkedList<>();
    public final String type;
    public boolean inline;
    public boolean startNewLine;
    
    public XMLBuilder(String type) {
        this.type = type;
        this.inline = false;
        this.startNewLine = true;
    }
    
    public XMLBuilder(String type, boolean inline) {
        this.type = type;
        this.inline = inline;
        this.startNewLine = !inline;
    }
    
    public XMLBuilder(String type, boolean inline, boolean startNewLine) {
        this.type = type;
        this.inline = inline;
        this.startNewLine = startNewLine;
    }
    
    
    public XMLBuilder addOption(String key, String option) {
        options.put(key, option);
        return this;
    }
    
    public XMLBuilder addKeyOption(String option) {
        options.put(option, null);
        return this;
    }

    public XMLBuilder setID(String id) {
        return addStringOption("id", id);
    }

    public XMLBuilder setClass(String clazz) {
        return addStringOption("class", clazz);
    }

    public XMLBuilder addStringOption(String key, String option) {
        options.put(key, "\"" + option + "\"");
        return this;
    }
    
    public XMLBuilder append(Object ...children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    public void pop(int index) {
        children.remove(index);
    }

    public void pop() {
        children.remove(children.size() - 1);
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder("<").append(type);
        for (Map.Entry<String, String> option : options.entrySet()) {
            builder.append(" ").append(option.getKey());
            if (option.getValue() != null) builder.append("=").append(option.getValue());
        }
        builder.append(">");
        if (children.isEmpty()) {
            builder.append("</").append(type).append(">");
        } else {
            boolean inline = this.inline;
            for (Object rawChild : children) {
                if (rawChild instanceof XMLBuilder) {
                    XMLBuilder child = (XMLBuilder) rawChild;
                    builder.append((inline || child.inline) && !child.startNewLine ? "" : "\n    ").append(tabIn(child.toString(), child.inline));
                    inline = child.inline;
                } else if (rawChild != null) {
                    builder.append(inline ? "" : "\n    ").append(tabIn(rawChild.toString(), inline));
                    inline = this.inline;
                }
            }
            builder.append(this.inline ? "" : "\n").append("</").append(type).append(">");
        }
        return builder.toString();
    }
    
    private String tabIn(String string, boolean inline) {
        if (inline) return string;
        return string.replaceAll("\n", "\n    ");
    }
}
