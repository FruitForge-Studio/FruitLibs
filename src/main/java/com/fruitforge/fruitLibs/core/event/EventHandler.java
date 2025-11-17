package com.fruitforge.fruitLibs.core.event;

import com.fruitforge.fruitLibs.api.event.Event;
import com.fruitforge.fruitLibs.api.event.EventListener;
import com.fruitforge.fruitLibs.api.event.EventPriority;

import java.lang.reflect.Method;

class EventHandler implements Comparable<EventHandler> {

    private final Object listener;
    private final Method method;
    private final Class<? extends Event> eventType;
    private final EventPriority priority;
    private final boolean ignoreCancelled;

    public EventHandler(Object listener, Method method, Class<? extends Event> eventType, EventListener annotation) {
        this.listener = listener;
        this.method = method;
        this.eventType = eventType;
        this.priority = annotation.priority();
        this.ignoreCancelled = annotation.ignoreCancelled();
        this.method.setAccessible(true);
    }

    public void invoke(Event event) throws Exception {
        if (event.isCancelled() && !ignoreCancelled) {
            return;
        }
        method.invoke(listener, event);
    }

    public Object getListener() {
        return listener;
    }

    public Method getMethod() {
        return method;
    }

    public Class<? extends Event> getEventType() {
        return eventType;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public boolean isIgnoreCancelled() {
        return ignoreCancelled;
    }

    @Override
    public int compareTo(EventHandler other) {
        return Integer.compare(this.priority.getSlot(), other.priority.getSlot());
    }
}