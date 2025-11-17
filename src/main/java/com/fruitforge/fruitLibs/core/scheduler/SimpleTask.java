package com.fruitforge.fruitLibs.core.scheduler;

import com.fruitforge.fruitLibs.api.scheduler.ErrorHandler;
import com.fruitforge.fruitLibs.api.scheduler.Task;
import com.fruitforge.fruitLibs.core.logging.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleTask implements Task {

    private final JavaPlugin plugin;
    private final LogManager logManager;
    private final Runnable runnable;
    private final boolean async;

    private long delay = 0;
    private long repeat = 0;
    private boolean repeatAsync = false;
    private int maxIterations = -1;
    private int maxRetries = 0;
    private long retryBackoff = 0;

    private Runnable successCallback;
    private ErrorHandler errorHandler;
    private Runnable completeCallback;

    private BukkitTask bukkitTask;
    private final AtomicInteger currentIteration = new AtomicInteger(0);
    private final AtomicInteger retryCount = new AtomicInteger(0);
    private volatile boolean cancelled = false;

    public SimpleTask(JavaPlugin plugin, LogManager logManager, Runnable runnable, boolean async) {
        this.plugin = plugin;
        this.logManager = logManager;
        this.runnable = runnable;
        this.async = async;
    }

    @Override
    public Task delay(long ticks) {
        this.delay = ticks;
        return this;
    }

    @Override
    public Task repeat(long ticks) {
        this.repeat = ticks;
        this.repeatAsync = false;
        return this;
    }

    @Override
    public Task repeatAsync(long ticks) {
        this.repeat = ticks;
        this.repeatAsync = true;
        return this;
    }

    @Override
    public Task iterations(int count) {
        this.maxIterations = count;
        return this;
    }

    @Override
    public Task retry(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    @Override
    public Task retryWithBackoff(int maxRetries, long initialDelay) {
        this.maxRetries = maxRetries;
        this.retryBackoff = initialDelay;
        return this;
    }

    @Override
    public Task onSuccess(Runnable callback) {
        this.successCallback = callback;
        return this;
    }

    @Override
    public Task onError(ErrorHandler handler) {
        this.errorHandler = handler;
        return this;
    }

    @Override
    public Task onComplete(Runnable callback) {
        this.completeCallback = callback;
        return this;
    }

    @Override
    public void execute() {
        if (repeat > 0) {
            executeRepeating();
        } else {
            executeSingle();
        }
    }

    @Override
    public CompletableFuture<Void> executeAsync() {
        return CompletableFuture.runAsync(() -> execute());
    }

    @Override
    public void cancel() {
        cancelled = true;
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isRunning() {
        return bukkitTask != null && !cancelled;
    }

    @Override
    public int getCurrentIteration() {
        return currentIteration.get();
    }

    private void executeSingle() {
        Runnable wrappedTask = createWrappedTask();

        if (delay > 0) {
            if (async) {
                bukkitTask = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, wrappedTask, delay);
            } else {
                bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, wrappedTask, delay);
            }
        } else {
            if (async) {
                bukkitTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, wrappedTask);
            } else {
                bukkitTask = Bukkit.getScheduler().runTask(plugin, wrappedTask);
            }
        }

        TaskRegistry.register(this);
    }

    private void executeRepeating() {
        Runnable wrappedTask = () -> {
            if (cancelled) {
                cancel();
                return;
            }

            if (maxIterations > 0 && currentIteration.get() >= maxIterations) {
                cancel();
                executeComplete();
                return;
            }

            try {
                runnable.run();
                currentIteration.incrementAndGet();

                if (successCallback != null) {
                    successCallback.run();
                }

            } catch (Exception e) {
                handleError(e);
            }
        };

        if (repeatAsync) {
            bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, wrappedTask, delay, repeat);
        } else {
            bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, wrappedTask, delay, repeat);
        }

        TaskRegistry.register(this);
    }

    private Runnable createWrappedTask() {
        return () -> {
            if (cancelled) {
                return;
            }

            try {
                runnable.run();

                if (successCallback != null) {
                    successCallback.run();
                }

                executeComplete();

            } catch (Exception e) {
                handleError(e);
            }
        };
    }

    private void handleError(Exception e) {
        if (retryCount.get() < maxRetries) {
            int attempt = retryCount.incrementAndGet();
            long backoffDelay = retryBackoff > 0 ? retryBackoff * (long) Math.pow(2, attempt - 1) : 0;

            logManager.warning("Task failed (attempt " + attempt + "/" + maxRetries + "), retrying in " + backoffDelay + " ticks...");

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    runnable.run();
                    if (successCallback != null) {
                        successCallback.run();
                    }
                    executeComplete();
                } catch (Exception retryException) {
                    handleError(retryException);
                }
            }, backoffDelay);

        } else {
            if (errorHandler != null) {
                errorHandler.handle(e);
            } else {
                logManager.error("Task execution failed after " + retryCount.get() + " retries", e);
            }
            executeComplete();
        }
    }

    private void executeComplete() {
        if (completeCallback != null) {
            completeCallback.run();
        }
        TaskRegistry.unregister(this);
    }
}