package com.fruitforge.fruitLibs.core.scheduler;

import com.fruitforge.fruitLibs.api.scheduler.ChainableTask;
import com.fruitforge.fruitLibs.api.scheduler.CronTask;
import com.fruitforge.fruitLibs.api.scheduler.SchedulerBuilder;
import com.fruitforge.fruitLibs.api.scheduler.Task;
import com.fruitforge.fruitLibs.core.logging.LogManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;

public class SchedulerBuilderImpl implements SchedulerBuilder {

    private final JavaPlugin plugin;
    private final LogManager logManager;

    public SchedulerBuilderImpl(JavaPlugin plugin, LogManager logManager) {
        this.plugin = plugin;
        this.logManager = logManager;
    }

    @Override
    public Task sync(Runnable runnable) {
        return new SimpleTask(plugin, logManager, runnable, false);
    }

    @Override
    public Task async(Runnable runnable) {
        return new SimpleTask(plugin, logManager, runnable, true);
    }

    @Override
    public <T> ChainableTask<T> supplyAsync(Supplier<T> supplier) {
        return new ChainableTaskImpl<>(plugin, logManager, supplier);
    }

    @Override
    public CronTask cron(String expression, Runnable runnable) {
        return new CronTaskImpl(plugin, logManager, expression, runnable);
    }
}