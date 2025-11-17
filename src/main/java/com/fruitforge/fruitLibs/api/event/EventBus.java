package com.fruitforge.fruitLibs.api.event;

import java.util.concurrent.CompletableFuture;

public interface EventBus {

    void register(Object listener);

    void unregister(Object listener);

    void unregisterAll();

    <T extends Event> T post(T event);

    <T extends Event> CompletableFuture<T> postAsync(T event);

    boolean hasListeners(Class<? extends Event> eventClass);

    int getListenerCount(Class<? extends Event> eventClass);

    int getTotalListenerCount();
}