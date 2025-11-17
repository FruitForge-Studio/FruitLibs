package com.fruitforge.fruitLibs.api.scheduler;

public interface CronTask {

    void start();

    void stop();

    boolean isRunning();

    String getExpression();

    long getNextExecutionTime();
}