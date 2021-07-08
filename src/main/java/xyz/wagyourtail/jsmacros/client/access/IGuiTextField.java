package xyz.wagyourtail.jsmacros.client.access;

import java.util.function.Consumer;

public interface IGuiTextField {

    boolean isEnabled();
    
    void setOnChange(Consumer<String> onChange);
}
