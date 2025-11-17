package com.fruitforge.fruitLibs.api.scheduler;

public interface SchedulerBuilder {

    Task sync(Runnable runnable);

    Task async(Runnable runnable);

    <T> ChainableTask<T> supplyAsync(java.util.function.Supplier<T> supplier);

    CronTask cron(String expression, Runnable runnable);
}