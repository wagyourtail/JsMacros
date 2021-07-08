package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.MethodWrapper;

/**
 * {@link FunctionalInterface} implementation for wrapping methods to match the language spec.
 *
 * An instance of this class is passed to scripts as the {@code consumer} variable.
 *
 * Javascript:
 * language spec requires that only one thread can hold an instance of the language at a time,
 * so this implementation uses a non-preemptive queue for the threads that call the resulting {@link MethodWrapper MethodWrappers}.
 *
 * JEP:
 * language spec requires everything to be on the same thread, on the java end, so all calls to {@link MethodWrapper MethodWrappers}
 * call back to JEP's starting thread and wait for the call to complete. This means that JEP can sometimes have trouble
 * closing properly, so if you use any {@link MethodWrapper MethodWrappers}, be sure to call FConsumer#stop(), to close the process,
 * otherwise it's a memory leak.
 *
 * Jython:
 * no limitations
 *
 * LUA:
 * no limitations
 *
 *
 * @since 1.2.5, re-named from {@code consumer} in 1.3.2
 *
 * @author Wagyourtail
 */
public interface IFWrapper<T> {
    
    /**
     * @since 1.4.0
     * @param c
     * @custom.replaceParams c: (arg0?: A, arg1?: B) =&gt; R
     * @return a new {@link MethodWrapper MethodWrapper}
     */
    <A, B, R> MethodWrapper<A, B, R> methodToJava(T c);
    
    /**
     * @since 1.4.0
     * @param c
     * @custom.replaceParams c: (arg0?: A, arg1?: B) =&gt; R
     * @return a new {@link MethodWrapper MethodWrapper}
     */
    <A, B, R> MethodWrapper<A, B, R> methodToJavaAsync(T c);

    /**
     * Close the current context, more important in JEP as they won't close themselves if you use other functions in
     * this class
     *
     * @since 1.2.2
     *
     */
    void stop();
}
