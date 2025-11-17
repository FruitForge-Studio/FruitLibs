package com.fruitforge.fruitLibs.api.scheduler;

import java.util.concurrent.CompletableFuture;

public interface Task {

    Task delay(long ticks);

    Task repeat(long ticks);

    Task repeatAsync(long ticks);

    Task iterations(int count);

    Task retry(int maxRetries);

    Task retryWithBackoff(int maxRetries, long initialDelay);

    Task onSuccess(Runnable callback);

    Task onError(ErrorHandler handler);

    Task onComplete(Runnable callback);

    void execute();

    CompletableFuture<Void> executeAsync();

    void cancel();

    boolean isCancelled();

    boolean isRunning();

    int getCurrentIteration();
}