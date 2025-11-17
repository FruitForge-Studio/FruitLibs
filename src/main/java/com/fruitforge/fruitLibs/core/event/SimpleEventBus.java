package com.fruitforge.fruitLibs.core.event;

import com.fruitforge.fruitLibs.api.event.*;
import com.fruitforge.fruitLibs.api.event.EventListener;
import com.fruitforge.fruitLibs.core.logging.LogManager;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleEventBus implements EventBus {

    private final LogManager logManager;
    private final Map<Class<? extends Event>, List<EventHandler>> handlersByEvent;
    private final Map<Object, List<EventHandler>> handlersByListener;

    public SimpleEventBus(LogManager logManager) {
        this.logManager = logManager;
        this.handlersByEvent = new ConcurrentHashMap<>();
        this.handlersByListener = new ConcurrentHashMap<>();
    }

    @Override
    public void register(Object listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        if (handlersByListener.containsKey(listener)) {
            logManager.warning("Listener already registered: " + listener.getClass().getName());
            return;
        }

        List<EventHandler> handlers = new ArrayList<>();

        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventListener.class)) {
                continue;
            }

            if (method.getParameterCount() != 1) {
                logManager.error("Event listener method must have exactly one parameter: " +
                        method.getName() + " in " + listener.getClass().getName());
                continue;
            }

            Class<?> paramType = method.getParameterTypes()[0];

            if (!Event.class.isAssignableFrom(paramType)) {
                logManager.error("Event listener parameter must extend Event: " +
                        method.getName() + " in " + listener.getClass().getName());
                continue;
            }

            @SuppressWarnings("unchecked")
            Class<? extends Event> eventType = (Class<? extends Event>) paramType;
            EventListener annotation = method.getAnnotation(EventListener.class);

            EventHandler handler = new EventHandler(listener, method, eventType, annotation);
            handlers.add(handler);

            handlersByEvent
                    .computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                    .add(handler);
        }

        if (handlers.isEmpty()) {
            logManager.warning("No event listener methods found in: " + listener.getClass().getName());
            return;
        }

        handlersByEvent.values().forEach(Collections::sort);

        handlersByListener.put(listener, handlers);
        logManager.debug("Registered event listener: " + listener.getClass().getName() +
                " (" + handlers.size() + " handlers)");
    }

    @Override
    public void unregister(Object listener) {
        List<EventHandler> handlers = handlersByListener.remove(listener);

        if (handlers == null) {
            return;
        }

        for (EventHandler handler : handlers) {
            List<EventHandler> eventHandlers = handlersByEvent.get(handler.getEventType());
            if (eventHandlers != null) {
                eventHandlers.remove(handler);
                if (eventHandlers.isEmpty()) {
                    handlersByEvent.remove(handler.getEventType());
                }
            }
        }

        logManager.debug("Unregistered event listener: " + listener.getClass().getName());
    }

    @Override
    public void unregisterAll() {
        handlersByEvent.clear();
        handlersByListener.clear();
        logManager.debug("Unregistered all event listeners");
    }

    @Override
    public <T extends Event> T post(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        List<EventHandler> handlers = getHandlersForEvent(event.getClass());

        if (handlers.isEmpty() && !(event instanceof DeadEvent)) {
            post(new DeadEvent(event));
            return event;
        }

        for (EventHandler handler : handlers) {
            try {
                handler.invoke(event);
            } catch (Exception e) {
                logManager.error("Error dispatching event " + event.getClass().getSimpleName() +
                        " to " + handler.getListener().getClass().getName(), e);
            }
        }

        return event;
    }

    @Override
    public <T extends Event> CompletableFuture<T> postAsync(T event) {
        return CompletableFuture.supplyAsync(() -> post(event));
    }

    @Override
    public boolean hasListeners(Class<? extends Event> eventClass) {
        return !getHandlersForEvent(eventClass).isEmpty();
    }

    @Override
    public int getListenerCount(Class<? extends Event> eventClass) {
        return getHandlersForEvent(eventClass).size();
    }

    @Override
    public int getTotalListenerCount() {
        return handlersByListener.size();
    }

    private List<EventHandler> getHandlersForEvent(Class<? extends Event> eventClass) {
        List<EventHandler> handlers = new ArrayList<>();

        for (Map.Entry<Class<? extends Event>, List<EventHandler>> entry : handlersByEvent.entrySet()) {
            if (entry.getKey().isAssignableFrom(eventClass)) {
                handlers.addAll(entry.getValue());
            }
        }

        Collections.sort(handlers);
        return handlers;
    }
}