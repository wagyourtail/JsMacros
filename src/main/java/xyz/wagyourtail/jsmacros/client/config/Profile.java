package xyz.wagyourtail.jsmacros.client.config;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.CustomClickEvent;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.client.api.event.impl.*;
import xyz.wagyourtail.jsmacros.client.api.library.impl.*;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;
import xyz.wagyourtail.jsmacros.client.gui.screens.MacroScreen;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.BaseProfile;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.language.BaseWrappedException;
import xyz.wagyourtail.jsmacros.core.language.ContextContainer;
import xyz.wagyourtail.jsmacros.core.library.impl.FJsMacros;

public class Profile extends BaseProfile {
    
    public Profile(Core runner) {
        super(runner, JsMacros.LOGGER);
    }
    
    @Override
    protected boolean loadProfile(String profileName) {
        boolean val = super.loadProfile(profileName);
        final MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen instanceof MacroScreen) {
            mc.execute(() -> ((MacroScreen) mc.currentScreen).reload());
        }
        return val;
    }
    
    @Override
    public void triggerEventJoin(BaseEvent event) {
        boolean joinedMain = MinecraftClient.getInstance().isOnThread() || joinedThreadStack.contains(Thread.currentThread());
        triggerEventJoinNoAnything(event);
    
        for (IEventListener macro : runner.eventRegistry.getListeners("ANYTHING")) {
            runJoinedEventListener(event, joinedMain, macro);
        }
    }
    
    @Override
    public void triggerEventJoinNoAnything(BaseEvent event) {
        boolean joinedMain = MinecraftClient.getInstance().isOnThread() || joinedThreadStack.contains(Thread.currentThread());
        if (event instanceof EventCustom) {
            for (IEventListener macro : runner.eventRegistry.getListeners(((EventCustom) event).eventName)) {
                runJoinedEventListener(event, joinedMain, macro);
            }
        } else {
            for (IEventListener macro : runner.eventRegistry.getListeners(event.getEventName())) {
                runJoinedEventListener(event, joinedMain, macro);
            }
        }
    }
    
    private void runJoinedEventListener(BaseEvent event, boolean joinedMain, IEventListener macroListener) {
        if (macroListener instanceof FJsMacros.ScriptEventListener && ((FJsMacros.ScriptEventListener) macroListener).getCreator() == Thread.currentThread() && ((FJsMacros.ScriptEventListener) macroListener).getWrapper().preventSameThreadJoin()) {
            throw new IllegalThreadStateException("Cannot join " + macroListener.toString() + " on same thread as it's creation.");
        }
        ContextContainer<?> t = macroListener.trigger(event);
        if (t == null) return;
        try {
            if (joinedMain) {
                joinedThreadStack.add(t.getLockThread());
            }
            ContextLockWatchdog.startWatchdog(t, Thread.currentThread(), macroListener, Core.instance.config.getOptions(CoreConfigV2.class).maxLockTime);
            t.awaitLock(() -> {
                joinedThreadStack.remove(t.getLockThread());
            });
        } catch (InterruptedException ignored) {
            joinedThreadStack.remove(t.getLockThread());
        }
    }
    
    @Override
    public void logError(Throwable ex) {
        ex.printStackTrace();
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.inGameHud != null) {
            BaseWrappedException<?> e;
            try {
                e = runner.wrapException(ex);
            } catch (Throwable t) {
                t.printStackTrace();
                mc.execute(() -> ((IChatHud) mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(new TranslatableText("jsmacros.errorerror").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED))));
                return;
            }
            Text text = compileError(e);
            mc.execute(() -> {
                try {
                    ((IChatHud) mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text);
                } catch (Throwable t) {
                    ((IChatHud) mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(new TranslatableText("jsmacros.errorerror").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
                    t.printStackTrace();
                }
            });
        }
    }
    
    private Text compileError(BaseWrappedException<?> ex) {
        if (ex == null) return null;
        BaseWrappedException<?> head = ex;
        LiteralText text = new LiteralText("");
        do {
            String message = head.message;
            MutableText line = new LiteralText(message).setStyle(Style.EMPTY.withColor(Formatting.RED));
            if (head.location != null) {
                Style locationStyle = Style.EMPTY.withColor(Formatting.GOLD);
                if (head.location instanceof BaseWrappedException.GuestLocation) {
                    BaseWrappedException.GuestLocation loc = (BaseWrappedException.GuestLocation) head.location;
                    locationStyle = locationStyle.withHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("jsmacros.clicktoview"))
                    ).withClickEvent(new CustomClickEvent(() -> {
                        if (loc.startIndex > -1) {
                            EditorScreen.openAndScrollToIndex(loc.file, loc.startIndex, loc.endIndex);
                        } else if (loc.line > -1) {
                            EditorScreen.openAndScrollToLine(loc.file, loc.line, loc.column, -1);
                        } else {
                            EditorScreen.openAndScrollToIndex(loc.file, 0, 0);
                        }
                    }));
                }
                line.append(new LiteralText(" (" + head.location.toString() + ")").setStyle(locationStyle));
            }
            if ((head = head.next) != null) line.append("\n");
            text.append(line);
        } while (head != null);
        return text;
    }
    
    @Override
    public void initRegistries() {
        super.initRegistries();
    
        TickBasedEvents.init();
        runner.eventRegistry.addEvent(EventAirChange.class);
        runner.eventRegistry.addEvent(EventArmorChange.class);
        runner.eventRegistry.addEvent(EventBlockUpdate.class);
        runner.eventRegistry.addEvent(EventBossbar.class);
        runner.eventRegistry.addEvent(EventChunkLoad.class);
        runner.eventRegistry.addEvent(EventChunkUnload.class);
        runner.eventRegistry.addEvent(EventDamage.class);
        runner.eventRegistry.addEvent(EventDeath.class);
        runner.eventRegistry.addEvent(EventDimensionChange.class);
        runner.eventRegistry.addEvent(EventDisconnect.class);
        runner.eventRegistry.addEvent(EventEXPChange.class);
        runner.eventRegistry.addEvent(EventFallFlying.class);
        runner.eventRegistry.addEvent(EventHeldItemChange.class);
        runner.eventRegistry.addEvent(EventHungerChange.class);
        runner.eventRegistry.addEvent(EventItemDamage.class);
        runner.eventRegistry.addEvent(EventItemPickup.class);
        runner.eventRegistry.addEvent(EventJoinedTick.class);
        runner.eventRegistry.addEvent(EventJoinServer.class);
        runner.eventRegistry.addEvent(EventKey.class);
        runner.eventRegistry.addEvent(EventOpenScreen.class);
        runner.eventRegistry.addEvent(EventPlayerJoin.class);
        runner.eventRegistry.addEvent(EventPlayerLeave.class);
        runner.eventRegistry.addEvent(EventRecvMessage.class);
        runner.eventRegistry.addEvent(EventRiding.class);
        runner.eventRegistry.addEvent(EventSendMessage.class);
        runner.eventRegistry.addEvent(EventSignEdit.class);
        runner.eventRegistry.addEvent(EventSound.class);
        runner.eventRegistry.addEvent(EventTick.class);
        runner.eventRegistry.addEvent(EventTitle.class);

        runner.libraryRegistry.addLibrary(FChat.class);
        runner.libraryRegistry.addLibrary(FHud.class);
        runner.libraryRegistry.addLibrary(FClient.class);
        runner.libraryRegistry.addLibrary(FKeyBind.class);
        runner.libraryRegistry.addLibrary(FPlayer.class);
        runner.libraryRegistry.addLibrary(FTime.class);
        runner.libraryRegistry.addLibrary(FWorld.class);
    }
}