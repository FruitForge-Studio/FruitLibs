package com.fruitforge.fruitLibs.api.scheduler;

@FunctionalInterface
public interface ErrorHandler {
    void handle(Throwable throwable);
}