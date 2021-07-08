package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerAddress;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.helpers.OptionsHelper;
import xyz.wagyourtail.jsmacros.client.config.Profile;
import xyz.wagyourtail.jsmacros.client.tick.TickSync;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

/**
*
* Functions that interact with minecraft that don't fit into their own module.
*
 * An instance of this class is passed to scripts as the {@code Client} variable.
 * @author Wagyourtail
 * @since 1.2.9
 */
@Library("Client")
@SuppressWarnings("unused")
public class FClient extends BaseLibrary {
    private static final Minecraft mc = Minecraft.getMinecraft();
    /**
     * Don't touch this plz xd.
     */
    public static TickSync tickSynchronizer = new TickSync();
    
    /**
    *
    * @since 1.0.0 (was in the {@code jsmacros} library until 1.2.9)
     * @return the raw minecraft client class, it may be useful to use <a target="_blank" href="https://wagyourtail.xyz/Projects/Minecraft%20Mappings%20Viewer/App">Minecraft Mappings Viewer</a> for this.
     */
    public Minecraft getMinecraft() {
        return mc;
    }

    /**
     * Run your task on the main minecraft thread
     * @param runnable task to run
     * @since 1.4.0
     */
    public void runOnMainThread(MethodWrapper<Object, Object, Object> runnable) {
        mc.addScheduledTask(runnable);
    }

    /**
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.OptionsHelper
     *
     * @since 1.1.7 (was in the {@code jsmacros} library until 1.2.9)
     *
     * @return an {@link xyz.wagyourtail.jsmacros.client.api.helpers.OptionsHelper OptionsHelper} for the game options.
     */
    public OptionsHelper getGameOptions() {
        return new OptionsHelper(mc.gameSettings);
    }
    
    /**
     * @return the current minecraft version as a {@link java.lang.String String}.
     *
     * @since 1.1.2 (was in the {@code jsmacros} library until 1.2.9)
     */
    public String mcVersion() {
        return mc.getVersion();
    }
    
    /**
     * @since 1.2.0 (was in the {@code jsmacros} library until 1.2.9)
     *
     * @return the fps debug string from minecraft.
     *
     */
    public String getFPS() {
        return mc.debug;
    }
    
    /**
     * @since 1.2.3 (was in the {@code jsmacros} library until 1.2.9)
     *
     * @see #connect(String, int)
     *
     * @param ip
     */
    public void connect(String ip) {
        ServerAddress a = ServerAddress.fromString(ip);
        connect(a.getIP(), a.getPort());
    }
    
    /**
     * Connect to a server
     *
     * @since 1.2.3 (was in the {@code jsmacros} library until 1.2.9)
     *
     * @param ip
     * @param port
     */
    public void connect(String ip, int port) {
        mc.addScheduledTask(() -> {
            if (mc.theWorld != null) mc.theWorld.sendQuittingDisconnectingPacket();
            mc.loadWorld(null);
            mc.displayGuiScreen(new GuiConnecting(null, mc, ip, port));
        });
    }
    
    /**
     * @since 1.2.3 (was in the {@code jsmacros} library until 1.2.9)
     *
     * @see #disconnect(MethodWrapper)
     */
    public void disconnect() {
        disconnect(null);
    }
    
    /**
     * Disconnect from a server with callback.
     *
     * @since 1.2.3 (was in the {@code jsmacros} library until 1.2.9)
     *
     * {@code callback} defaults to {@code null}
     *
     * @param callback calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link java.lang.Boolean Boolean}&gt;
     */
    public void disconnect(MethodWrapper<Boolean, Object, Object> callback) {
        mc.addScheduledTask(() -> {
            boolean isWorld = mc.theWorld != null;
            if (isWorld) mc.theWorld.sendQuittingDisconnectingPacket();
            if (callback != null) callback.accept(isWorld);
        });
    }
    
    /**
     * @since 1.2.4
     *
     * @see #waitTick(int)
     *
     * @throws InterruptedException
     */
    public void waitTick() throws InterruptedException {
        if (mc.isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread())) {
            throw new IllegalThreadStateException("Attempted to wait on a thread that is currently joined!");
        }
        tickSynchronizer.waitTick();
    }
    
    /**
     * waits the specified number of client ticks.
     * don't use this on an event that the main thread waits on (joins)... that'll cause circular waiting.
     * @since 1.2.6
     *
     * @param i
     * @throws InterruptedException
     */
    public void waitTick(int i) throws InterruptedException {
        if (mc.isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread())) {
            throw new IllegalThreadStateException("Attempted to wait on a thread that is currently joined!");
        }
        while (--i >= 0) {
            tickSynchronizer.waitTick();
        }
    }
}
