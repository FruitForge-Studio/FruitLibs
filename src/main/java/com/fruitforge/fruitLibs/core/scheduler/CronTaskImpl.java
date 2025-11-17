package com.fruitforge.fruitLibs.core.scheduler;

import com.fruitforge.fruitLibs.api.scheduler.CronTask;
import com.fruitforge.fruitLibs.core.logging.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class CronTaskImpl implements CronTask {

    private final JavaPlugin plugin;
    private final LogManager logManager;
    private final String expression;
    private final Runnable runnable;

    private BukkitTask bukkitTask;
    private long nextExecutionTime;

    public CronTaskImpl(JavaPlugin plugin, LogManager logManager, String expression, Runnable runnable) {
        this.plugin = plugin;
        this.logManager = logManager;
        this.expression = expression;
        this.runnable = runnable;
    }

    @Override
    public void start() {
        long interval = parseCronExpression(expression);

        if (interval <= 0) {
            logManager.error("Invalid cron expression: " + expression);
            return;
        }

        this.nextExecutionTime = System.currentTimeMillis() + (interval * 50);

        bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                runnable.run();
                this.nextExecutionTime = System.currentTimeMillis() + (interval * 50);
            } catch (Exception e) {
                logManager.error("Cron task execution failed", e);
            }
        }, interval, interval);

        logManager.debug("Started cron task: " + expression);
    }

    @Override
    public void stop() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
            logManager.debug("Stopped cron task: " + expression);
        }
    }

    @Override
    public boolean isRunning() {
        return bukkitTask != null && !bukkitTask.isCancelled();
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public long getNextExecutionTime() {
        return nextExecutionTime;
    }

    private long parseCronExpression(String expression) {
        expression = expression.toLowerCase().trim();

        return switch (expression) {
            case "every second" -> 20L;
            case "every 5 seconds" -> 100L;
            case "every 10 seconds" -> 200L;
            case "every 30 seconds" -> 600L;
            case "every minute" -> 1200L;
            case "every 5 minutes" -> 6000L;
            case "every 10 minutes" -> 12000L;
            case "every 30 minutes" -> 36000L;
            case "every hour" -> 72000L;
            default -> {
                if (expression.startsWith("every ") && expression.endsWith(" ticks")) {
                    try {
                        String ticksStr = expression.replace("every ", "").replace(" ticks", "");
                        yield Long.parseLong(ticksStr);
                    } catch (NumberFormatException e) {
                        yield -1L;
                    }
                }
                yield -1L;
            }
        };
    }
}