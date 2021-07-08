declare const event: Events.BaseEvent;
declare const file: Java.java.io.File;
declare const context: Java.xyz.wagyourtail.jsmacros.core.language.ContextContainer<any>;

declare namespace Events {
    export interface BaseEvent extends Java.Object {
        getEventName(): string;
    }