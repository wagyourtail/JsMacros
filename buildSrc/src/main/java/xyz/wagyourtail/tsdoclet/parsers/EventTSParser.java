package xyz.wagyourtail.tsdoclet.parsers;

import com.sun.javadoc.*;
import xyz.wagyourtail.tsdoclet.AbstractTSParser;

public class EventTSParser extends AbstractTSParser {
    public final String name;
    final String oldName;
    public EventTSParser(ClassDoc clazz, String name, String oldName) {
        super(clazz);
        this.name = name;
        this.oldName = oldName;
    }
    
    @Override
    public String genTypeScript() {
        StringBuilder s = new StringBuilder("\t");
        Tag[] classtags = clazz.inlineTags();
        if (classtags.length > 0) s.append(AbstractTSParser.genCommentTypeScript(classtags, false, 1)).append("\t");
        s.append("export interface ").append(name).append(" extends BaseEvent {");
        
        s.append(insertEachLine(genFieldTS(), "\t\t"));
        s.append("\n");
        s.append(insertEachLine(genMethodTS(false), "\t\t"));
        
        s.append("\n\t}");
        
        return s.toString();
    }
    
}
