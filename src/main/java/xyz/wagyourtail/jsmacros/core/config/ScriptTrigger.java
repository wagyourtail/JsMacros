package xyz.wagyourtail.jsmacros.core.config;

public class ScriptTrigger {
    public TriggerType triggerType;
    public String event;
    public String scriptFile;
    public boolean enabled;
    
    public ScriptTrigger(TriggerType triggerType, String event, String scriptFile, boolean enabled) {
        this.triggerType = triggerType;
        this.event = event;
        this.scriptFile = scriptFile;
    }
    
    public boolean equals(ScriptTrigger macro) {
        return triggerType == macro.triggerType && event.equalsIgnoreCase(macro.event) && scriptFile.equals(macro.scriptFile);
    }
    
    public String toString() {
        return String.format("RawMacro:{\"type\": \"%s\", \"eventkey\": \"%s\", \"scriptFile\": \"%s\", \"enabled\": %b}", triggerType.toString(), event, scriptFile, enabled);
    }
    
    public static ScriptTrigger copy(ScriptTrigger m) {
        return new ScriptTrigger(m.triggerType, m.event, m.scriptFile, m.enabled);
    }
    
    public ScriptTrigger copy() {
        return copy(this);
    }
    
    /**
     * @since 1.0.0 [citation needed]
     * @author Wagyourtail
     */
    public enum TriggerType {
        KEY_FALLING,
        KEY_RISING,
        KEY_BOTH,
        EVENT
    }
    
    /**
     * @since 1.2.7
     * @return
     */
    public TriggerType getTriggerType() {
        return triggerType;
    }
    
    /**
     * @since 1.2.7
     * @return
     */
    public String getEvent() {
        return event;
    }
    
    /**
     * @since 1.2.7
     * @return
     */
    public String getScriptFile() {
        return scriptFile;
    }
    
    /**
     * @since 1.2.7
     * @return
     */
    public boolean getEnabled() {
        return enabled;
    }
}