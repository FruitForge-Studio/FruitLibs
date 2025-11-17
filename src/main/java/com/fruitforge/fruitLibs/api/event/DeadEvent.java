package com.fruitforge.fruitLibs.api.event;

public class DeadEvent extends Event {

    private final Event sourceEvent;

    public DeadEvent(Event sourceEvent) {
        this.sourceEvent = sourceEvent;
    }

    public Event getSourceEvent() {
        return sourceEvent;
    }

    public Class<? extends Event> getSourceEventClass() {
        return sourceEvent.getClass();
    }
}