package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.concurrent.Semaphore;

/**
 * Functions for interacting with chat.
 * 
 * An instance of this class is passed to scripts as the {@code Chat} variable.
 * 
 * @author Wagyourtail
 */
 @Library("Chat")
 @SuppressWarnings("unused")
public class FChat extends BaseLibrary {
    private static final Minecraft mc = Minecraft.getMinecraft();
    /**
     * Log to player chat.
     * 
     * @since 1.1.3
     * 
     * @param message
     */
    public void log(Object message) throws InterruptedException {
        log(message, false);
    }
    
    /**
     * @param message
     * @param await should wait for message to actually be sent to chat to continue.
     *
     * @throws InterruptedException
     */
    public void log(Object message, boolean await) throws InterruptedException {
        boolean joinedMain = mc.isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread());
        if (joinedMain) {
            if (message instanceof TextHelper) {
                logInternal((TextHelper)message);
            } else if (message != null) {
                logInternal(message.toString());
            }
        } else {
            final Semaphore semaphore = new Semaphore(await ? 0 : 1);
            mc.addScheduledTask(() -> {
                if (message instanceof TextHelper) {
                    logInternal((TextHelper) message);
                } else if (message != null) {
                    logInternal(message.toString());
                }
                semaphore.release();
            });
            semaphore.acquire();
        }
    }
    
    private static void logInternal(String message) {
        if (message != null) {
            ChatComponentText text = new ChatComponentText(message);
            ((IChatHud)mc.ingameGUI.getChatGUI()).jsmacros_addMessageBypass(text);
        }
    }
    
    private static void logInternal(TextHelper text) {
        ((IChatHud)mc.ingameGUI.getChatGUI()).jsmacros_addMessageBypass(text.getRaw());
    }
    
    /**
     * Say to server as player.
     * 
     * @since 1.0.0
     * 
     * @param message
     */
     public void say(String message) throws InterruptedException {
        say(message, false);
     }
    
    /**
    * Say to server as player.
    *
     * @param message
     * @param await
     * @since 1.3.1
     *
     * @throws InterruptedException
     */
    public void say(String message, boolean await) throws InterruptedException {
        boolean joinedMain = mc.isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread());
        if (message == null) return;
        if (joinedMain) {
            assert mc.thePlayer != null;
            mc.thePlayer.sendChatMessage(message);
        } else {
            final Semaphore semaphore = new Semaphore(await ? 0 : 1);
            mc.addScheduledTask(() -> {
                assert mc.thePlayer != null;
                mc.thePlayer.sendChatMessage(message);
                semaphore.release();
            });
            semaphore.acquire();
        }
    }
    
    /**
     * Display a Title to the player.
     * 
     * @since 1.2.1
     * 
     * @param title
     * @param subtitle
     * @param fadeIn
     * @param remain
     * @param fadeOut
     */
    public void title(Object title, Object subtitle, int fadeIn, int remain, int fadeOut) {
        String titlee = null;
        String subtitlee = null;
        if (title instanceof TextHelper) titlee = ((TextHelper) title).getRaw().getFormattedText();
        else if (title != null) titlee = title.toString();
        if (subtitle instanceof TextHelper) subtitlee = ((TextHelper) subtitle).getRaw().getFormattedText();
        else if (subtitle != null) subtitlee = subtitle.toString();
        if (title != null)
            mc.ingameGUI.displayTitle(titlee, null, fadeIn, remain, fadeOut);
        if (subtitle != null)
            mc.ingameGUI.displayTitle(null, subtitlee, fadeIn, remain, fadeOut);
        if (title == null && subtitle == null)
            mc.ingameGUI.displayTitle(null, null, fadeIn, remain, fadeOut);
    }
    
    /**
     * Display the smaller title that's above the actionbar.
     * 
     * @since 1.2.1
     * 
     * @param text
     * @param tinted
     */
    public void actionbar(Object text, boolean tinted) {
        assert mc.ingameGUI != null;
        IChatComponent textt = null;
        if (text instanceof TextHelper) textt = ((TextHelper) text).getRaw();
        else if (text != null) textt = new ChatComponentText(text.toString());
        mc.ingameGUI.setRecordPlaying(textt, tinted);
    }
    
    /**
     * Display a toast.
     * 
     * @since 1.2.5
     * 
     * @param title
     * @param desc
     */
    public void toast(Object title, Object desc) {
        //TODO:
//        ToastManager t = mc.getToastManager();
//        if (t != null) {
//            Text titlee = (title instanceof TextHelper) ? ((TextHelper) title).getRaw() : title != null ? new LiteralText(title.toString()) : null;
//            Text descc = (desc instanceof TextHelper) ? ((TextHelper) desc).getRaw() : desc != null ? new LiteralText(desc.toString()) : null;
//            if (titlee != null) t.add(new SystemToast(null, titlee, descc));
//        }
    }
    
    /**
     * Creates a {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper} for use where you need one and not a string.
     * 
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper
     * @since 1.1.3
     * 
     * @param content
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper}
     */
    public TextHelper createTextHelperFromString(String content) {
        return new TextHelper(new ChatComponentText(content));
    }
    
    /**
     * Create a  {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper} for use where you need one and not a string.
     * 
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper
     * @since 1.1.3
     * 
     * @param json
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper}
     */
    public TextHelper createTextHelperFromJSON(String json) {
        return new TextHelper(json);
    }
    
    /**
     * @see TextBuilder
     * @since 1.3.0
     * @return a new builder
     */
    public TextBuilder createTextBuilder() {
        return new TextBuilder();
    }

    /**
     * @param name name of command
     * @since 1.4.2
     * @return
     */
    public CommandBuilder createCommandBuilder(String name) {
        return new CommandBuilder(name);
    }
}
