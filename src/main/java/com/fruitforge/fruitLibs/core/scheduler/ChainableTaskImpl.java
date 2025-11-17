package com.fruitforge.fruitLibs.core.scheduler;

import com.fruitforge.fruitLibs.api.scheduler.ChainableTask;
import com.fruitforge.fruitLibs.api.scheduler.ErrorHandler;
import com.fruitforge.fruitLibs.core.logging.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChainableTaskImpl<T> implements ChainableTask<T> {

    private final JavaPlugin plugin;
    private final LogManager logManager;
    private CompletableFuture<T> future;
    private long delayTicks = 0;
    private ErrorHandler errorHandler;

    public ChainableTaskImpl(JavaPlugin plugin, LogManager logManager, Supplier<T> supplier) {
        this.plugin = plugin;
        this.logManager = logManager;
        this.future = CompletableFuture.supplyAsync(supplier);
    }

    private ChainableTaskImpl(JavaPlugin plugin, LogManager logManager, CompletableFuture<T> future) {
        this.plugin = plugin;
        this.logManager = logManager;
        this.future = future;
    }

    @Override
    public <U> ChainableTask<U> thenAsync(Function<T, U> function) {
        CompletableFuture<U> nextFuture = future.thenApplyAsync(function);
        return new ChainableTaskImpl<>(plugin, logManager, nextFuture);
    }

    @Override
    public <U> ChainableTask<U> thenSync(Function<T, U> function) {
        CompletableFuture<U> nextFuture = future.thenApplyAsync(result -> {
            CompletableFuture<U> syncResult = new CompletableFuture<>();

            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    U value = function.apply(result);
                    syncResult.complete(value);
                } catch (Exception e) {
                    syncResult.completeExceptionally(e);
                }
            });

            return syncResult.join();
        });

        return new ChainableTaskImpl<>(plugin, logManager, nextFuture);
    }

    @Override
    public ChainableTask<T> thenAcceptAsync(Consumer<T> consumer) {
        future = future.thenApplyAsync(result -> {
            consumer.accept(result);
            return result;
        });
        return this;
    }

    @Override
    public ChainableTask<T> thenAcceptSync(Consumer<T> consumer) {
        future = future.thenApplyAsync(result -> {
            Bukkit.getScheduler().runTask(plugin, () -> consumer.accept(result));
            return result;
        });
        return this;
    }

    @Override
    public ChainableTask<T> delay(long ticks) {
        this.delayTicks = ticks;
        return this;
    }

    @Override
    public ChainableTask<T> onError(ErrorHandler handler) {
        this.errorHandler = handler;
        return this;
    }

    @Override
    public void execute() {
        if (delayTicks > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> executeFuture(), delayTicks);
        } else {
            executeFuture();
        }
    }

    private void executeFuture() {
        future.exceptionally(throwable -> {
            if (errorHandler != null) {
                errorHandler.handle(throwable);
            } else {
                logManager.error("Chainable task execution failed", throwable);
            }
            return null;
        });
    }
}