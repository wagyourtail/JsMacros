package io.noties.prism4j.languages;

import io.noties.prism4j.GrammarUtils;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.annotations.Extend;
import org.jetbrains.annotations.NotNull;

import static io.noties.prism4j.Prism4j.*;
import static java.util.regex.Pattern.compile;

@Extend("javascript")
@SuppressWarnings("unused")
public class Prism_typescript {
    
    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {
        Token class_name = token("class-name");
        
    
        Grammar ts = GrammarUtils.extend(GrammarUtils.require(prism4j, "javascript"), "typescript",
            class_name,
            // From JavaScript Prism keyword list and TypeScript language spec: https://github.com/Microsoft/TypeScript/blob/master/doc/spec.md#221-reserved-words
            token("keyword", pattern(compile("\\b(?:abstract|as|asserts|async|await|break|case|catch|class|const|constructor|continue|debugger|declare|default|delete|do|else|enum|export|extends|finally|for|from|function|get|if|implements|import|in|instanceof|interface|is|keyof|let|module|namespace|new|null|of|package|private|protected|public|readonly|return|require|set|static|super|switch|this|throw|try|type|typeof|undefined|var|void|while|with|yield)\\b"))),
            token("builtin", pattern(compile("\\b(?:string|Function|any|number|boolean|Array|symbol|console|Promise|unknown|never)\\b")))
        );
    
        // doesn't work with TS because TS is too complex
        ts.tokens().remove(GrammarUtils.findToken(ts, "parameter"));
        
        Grammar typeInside = GrammarUtils.extend(ts, "inside-class");
        typeInside.tokens().remove(GrammarUtils.findToken(typeInside, "class-name"));
        
        GrammarUtils.insertBeforeToken(ts, "function",
            token("decorator",
                pattern(compile("@[$\\w\\xA0-\\uFFFF]+"), false, false, null,
                    grammar("inside",
                        token("at", pattern(compile("^@"), false, false, "operator")),
                        token("function", pattern(compile("^[\\s\\S]+")))
                    )
                )
            ),
            token("generic-function", pattern(compile("#?(?!\\s)[_$a-zA-Z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*\\s*<(?:[^<>]|<(?:[^<>]|<[^<>]*>)*>)*>(?=\\s*\\()"), false, true, null,
                grammar("inside",
                    token("function", pattern(compile("^#?(?!\\s)[_$a-zA-Z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*"))),
                    token("generic", pattern(compile("<[\\s\\S]+"), false, false, "class-name", typeInside))
                )
            ))
        );
        
        class_name.patterns().add(pattern(compile("(\\b(?:class|extends|implements|instanceof|interface|new|type)\\s+)(?!keyof\\b)(?!\\s)[_$a-zA-Z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*(?:\\s*<(?:[^<>]|<(?:[^<>]|<[^<>]*>)*>)*>)?"), true, true, null, typeInside));
    
        return ts;
    }
}
