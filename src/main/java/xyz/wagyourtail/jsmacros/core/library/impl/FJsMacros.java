package xyz.wagyourtail.jsmacros.core.library.impl;

import com.google.common.collect.ImmutableList;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import net.minecraft.util.Util;
import org.graalvm.polyglot.Context;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.BaseProfile;
import xyz.wagyourtail.jsmacros.core.config.ConfigManager;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.BaseEventRegistry;
import xyz.wagyourtail.jsmacros.core.event.BaseListener;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.ContextContainer;
import xyz.wagyourtail.jsmacros.core.language.ScriptContext;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Functions that interact directly with JsMacros or Events.
 * 
 * An instance of this class is passed to scripts as the {@code JsMacros} variable.
 * 
 * @author Wagyourtail
 */
 @Library("JsMacros")
 @SuppressWarnings("unused")
public class FJsMacros extends BaseLibrary {
    
    /**
     * @return the JsMacros profile class.
     */
    public BaseProfile getProfile() {
        return Core.instance.profile;
    }

    /**
     * @return the JsMacros config management class.
     */
    public ConfigManager getConfig() {
        return Core.instance.config;
    }
    
    

    /**
     * @return a {@link Map} of the current running threads.
     * 
     * @since 1.0.5
     * 
     */
     @Deprecated
    public Map<ScriptTrigger, Set<Object>> getRunningThreads() {
        throw new RuntimeException("Deprecated");
    }

    /**
     * @return list of non-garbage-collected ScriptContext's
     * @since 1.4.0
     */
    public List<ScriptContext<?>> getOpenContexts() {
        return ImmutableList.copyOf(Core.instance.contexts.keySet());
    }

    /**
     * 
     * @see FJsMacros#runScript(String, String, MethodWrapper)  
     * 
     * @since 1.1.5
     * 
     * @param file
     * @return
     */
    public ContextContainer<?> runScript(String file) {
        return runScript(file, (MethodWrapper<Throwable, Object, Object>) null);
    }

    /**
     * Run a script with optional callback of error.
     * 
     * @since 1.1.5
     * 
     * @param file relative to the macro folder.
     * @param callback defaults to {@code null}
     * @return the {@link ContextContainer} the script is running on.
     */
    public ContextContainer<?> runScript(String file, MethodWrapper<Throwable, Object, Object> callback) {
        if (callback != null) {
            return Core.instance.exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "", file, true), null, () -> callback.accept(null), callback);
        } else {
            return Core.instance.exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "", file, true), null);
        }
    }
    
    /**
     * @see FJsMacros#runScript(String, String, MethodWrapper)
     * 
     * @since 1.2.4
     * 
     * @param language
     * @param script
     * @return
     */
    public ContextContainer<?> runScript(String language, String script) {
        return runScript(language, script, null);
    }
    
    /**
     * Runs a string as a script.
     * 
     * @since 1.2.4
     * 
     * @param language
     * @param script
     * @param callback calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link String}&gt;
     * @return the {@link ContextContainer} the script is running on.
     */
    public ContextContainer<?> runScript(String language, String script, MethodWrapper<Throwable, Object, Object> callback) {
        BaseLanguage<?> lang = Core.instance.defaultLang;
        for (BaseLanguage<?> l : Core.instance.languages) {
            if (language.equals(l.extension.replaceAll("\\.", " ").trim().replaceAll(" ", "."))) {
                lang = l;
                break;
            }
        }
        return lang.trigger(script, callback == null ? null : () -> callback.accept(null), callback);
    }
    
    /**
     * Opens a file with the default system program.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the macro folder.
     */
    public void open(String path) {
        JsMacros.openFile(new File(Core.instance.config.macroFolder, path));
    }
    
    /**
     * Creates a listener for an event, this function can be more efficient that running a script file when used properly.
     * 
     * @see IEventListener
     * 
     * @since 1.2.7
     * @param event
     * @param callback calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link BaseEvent}, {@link ContextContainer}&gt;
     * @return
     */
    public IEventListener on(String event, MethodWrapper<BaseEvent, ContextContainer<?>, Object> callback) {
        if (callback == null) return null;
        if (!Core.instance.eventRegistry.events.contains(event)) {
            throw new IllegalArgumentException(String.format("Event \"%s\" not found, if it's a custom event register it with 'event.registerEvent()' first.", event));
        }
        Thread th = Thread.currentThread();
        ScriptContext<?> ctx = Core.instance.threadContext.get(th);
        IEventListener listener = new ScriptEventListener() {
            
            @Override
            public ContextContainer<?> trigger(BaseEvent event) {
                ContextContainer<?> p = new ContextContainer<>(ctx);
                Thread t = new Thread(() -> {
                    Thread.currentThread().setName(this.toString());
                    
                    try {
                        callback.accept(event, p);
                    } catch (Exception e) {
                        Core.instance.eventRegistry.removeListener(this);
                        Core.instance.profile.logError(e);
                    } finally {
                        p.releaseLock();
                    }
                });
                Thread ot = callback.overrideThread();
                p.setLockThread(ot == null ? t : ot);
                t.start();
                return p;
            }
    
            @Override
            public Thread getCreator() {
                return th;
            }
    
            @Override
            public MethodWrapper<?, ?, ?> getWrapper() {
                return callback;
            }
    
            @Override
            public String toString() {
                return String.format("ScriptEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", th.getName(), event);
            }
        };
        Core.instance.eventRegistry.addListener(event, listener);
        return listener;
    }
        
    /**
     * Creates a single-run listener for an event, this function can be more efficient that running a script file when used properly.
     * 
     * @see IEventListener
     * 
     * @since 1.2.7
     * 
     * @param event
     * @param callback calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link BaseEvent}, {@link ContextContainer}&gt;
     * @return the listener.
     */
    public IEventListener once(String event, MethodWrapper<BaseEvent, ContextContainer<?>, Object> callback) {
        if (callback == null) return null;
        if (!Core.instance.eventRegistry.events.contains(event)) {
            throw new IllegalArgumentException(String.format("Event \"%s\" not found, if it's a custom event register it with 'event.registerEvent()' first.", event));
        }
        Thread th = Thread.currentThread();
        ScriptContext<?> ctx = Core.instance.threadContext.get(th);
        IEventListener listener = new ScriptEventListener() {
            @Override
            public ContextContainer<?> trigger(BaseEvent event) {
                Core.instance.eventRegistry.removeListener(this);
                ContextContainer<?> p = new ContextContainer<>(ctx);
                Thread t = new Thread(() -> {
                    Thread.currentThread().setName(this.toString());
                    try {
                        callback.accept(event, p);
                    } catch (Exception e) {
                        Core.instance.profile.logError(e);
                    } finally {
                        p.releaseLock();
                    }
                });
                Thread ot = callback.overrideThread();
                p.setLockThread(ot == null ? t : ot);
                t.start();
                return p;
            }
    
            @Override
            public Thread getCreator() {
                return th;
            }
    
            @Override
            public MethodWrapper<?, ?, ?> getWrapper() {
                return callback;
            }
    
            @Override
            public String toString() {
                return String.format("OnceScriptEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", th.getName(), event);
            }
            
        };
        Core.instance.eventRegistry.addListener(event, listener);
        return listener;
    }
    
    /**
     * @see FJsMacros#off(String, IEventListener)
     * 
     * @since 1.2.3
     * 
     * @param listener
     * @return
     */
    public boolean off(IEventListener listener) {
        return Core.instance.eventRegistry.removeListener(listener);
    }
    
    /**
     * Removes a {@link IEventListener IEventListener} from an event.
     * 
     * @see IEventListener
     * 
     * @since 1.2.3
     * 
     * @param event
     * @param listener
     * @return
     */
    public boolean off(String event, IEventListener listener) {
        return Core.instance.eventRegistry.removeListener(event, listener);
    }

    /**
     * @param event event to wait for
     * @since 1.5.0
     * @return a event and a new context if the event you're waiting for was joined, to leave it early.
     *
     * @throws InterruptedException
     */
    public EventAndContext waitForEvent(String event) throws InterruptedException {
        return waitForEvent(event, null, null);
    }

    /**
     *
     * @param event
     * @return
     * @throws InterruptedException
     */
    public EventAndContext waitForEvent(String event,  MethodWrapper<BaseEvent, Object, Boolean> filter) throws InterruptedException {
        return waitForEvent(event, filter, null);
    }

    /**
     * @param event event to wait for
     * @param filter filter the event until it has the proper values or whatever.
     * @param runBeforeWaiting runs as a {@link Runnable}, run before waiting, this is a thread-safety thing to prevent "interrupts" from going in between this and things like releaseLock and deferCurrentTask
     * @since 1.5.0
     * @return a event and a new context if the event you're waiting for was joined, to leave it early.
     *
     * @throws InterruptedException
     */
    public EventAndContext waitForEvent(String event, MethodWrapper<BaseEvent, Object, Boolean> filter, MethodWrapper<Object, Object, Object> runBeforeWaiting) throws InterruptedException {
        if (!Core.instance.eventRegistry.events.contains(event)) {
            throw new IllegalArgumentException(String.format("Event \"%s\" not found, if it's a custom event register it with 'event.registerEvent()' first.", event));
        }

        //get curent thread establish the lock to use for waiting blah blah blah
        Thread th = Thread.currentThread();
        Semaphore lock = new Semaphore(0);

        // event return values
        final Exception[] e = {null};
        final BaseEvent[] ev = {null};

        // get the current script context from the thread
        ScriptContext<?> ctx = Core.instance.threadContext.get(th);

        // get the current context container.
        ContextContainer<?> cc = Core.instance.eventContexts.get(Thread.currentThread());

        // create a new context container so we can actually release joined events
        ContextContainer<?> ctxCont = new ContextContainer<>(ctx);

        // create the listener
        IEventListener listener = new ScriptEventListener() {
            @Override
            public ContextContainer<?> trigger(BaseEvent event) {
                try {
                    // check the filter
                    boolean test = filter == null || filter.test(event);

                    if (test) {
                        // remove the listener here, since it this isn't async.
                        Core.instance.eventRegistry.removeListener(this);

                        // release the lock
                        lock.release();

                        ev[0] = event;
                        return ctxCont;
                    }
                } catch (Exception ex) {
                    // release the lock and set error
                    lock.release();
                    e[0] = ex;
                }
                return null;
            }

            @Override
            public Thread getCreator() {
                return th;
            }

            @Override
            public MethodWrapper<?, ?, ?> getWrapper() {
                return null;
            }

            @Override
            public String toString() {
                return String.format("WaitForEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", th.getName(), event);
            }
        };
        // register the listener
        Core.instance.eventRegistry.addListener(event, listener);

        // run before, this is a thread-safety thing to prevent "interrupts" from going in between this and things like deferCurrentTask
        if (runBeforeWaiting != null) runBeforeWaiting.run();

        // make sure the current context isn't still locked.
        if (cc != null && cc.isLocked()) {
            Core.instance.eventRegistry.removeListener(listener);
            throw new IllegalThreadStateException("must explicitly release context (un-joins joined events) using 'context.releaseLock()' if you want to wait for a new event on this script's main thread!");
        }
        // set the new contextContainer's lock
        ctxCont.setLockThread(th);
        Core.instance.eventContexts.put(th, ctxCont);

        // waits for event
        try {
            lock.acquire();
        } finally {
            Core.instance.eventRegistry.removeListener(listener);
        }
        if (e[0] != null) {
            throw new RuntimeException("Error thrown in filter", e[0]);
        }

        // returns new context and event value to the user so they can release joined stuff early
        return new EventAndContext(ev[0], ctxCont);
    }
    
    /**
     * 
     * @since 1.2.3
     * 
     * @param event
     * @return a list of script-added listeners.
     */
    public List<IEventListener> listeners(String event) {
        List<IEventListener> listeners = new ArrayList<>();
        for (IEventListener l : Core.instance.eventRegistry.getListeners(event)) {
            if (!(l instanceof BaseListener)) listeners.add(l);
        }
        return listeners;
    }
    
    /**
    * create a custom event object that can trigger a event. It's recommended to use 
    * {@link EventCustom#registerEvent()} to set up the event to be visible in the GUI.
    * 
    * @see BaseEventRegistry#addEvent(String)
    * 
     * @param eventName name of the event. please don't use an existing one... your scripts might not like that.
     *
     * @since 1.2.8
     *
     * @return
     */
    public EventCustom createCustomEvent(String eventName) {
        return new EventCustom(eventName);
    }
    
    public interface ScriptEventListener extends IEventListener {
        Thread getCreator();
        
        MethodWrapper<?,?,?> getWrapper();
    }

    public static class EventAndContext {
        public final BaseEvent event;
        public final ContextContainer<?> context;

        public EventAndContext(BaseEvent event, ContextContainer<?> context) {
            this.event = event;
            this.context = context;
        }

        public String toString() {
            return String.format("EventAndContext:{\"event\": %s, \"context\": %s}", event.toString(), context.toString());
        }
    }
}
