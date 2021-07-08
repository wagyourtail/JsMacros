package xyz.wagyourtail.tsdoclet.parsers;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;
import xyz.wagyourtail.tsdoclet.AbstractTSParser;

public class LibraryTSParser extends AbstractTSParser {
    final String libName;
    
    public LibraryTSParser(ClassDoc clazz, String libName) {
        super (clazz);
        this.libName = libName;
    }
    
    @Override
    public String genTypeScript() {
        StringBuilder s = new StringBuilder();
        Tag[] classtags = clazz.inlineTags();
        if (classtags.length > 0) s.append(AbstractTSParser.genCommentTypeScript(classtags, false, 0));
        s.append("declare namespace ").append(libName).append(" {\n");
        
        s.append(insertEachLine(genMethodTS(true), "\t"));
        s.append("\n}");
        return s.toString();
    }
    
}
