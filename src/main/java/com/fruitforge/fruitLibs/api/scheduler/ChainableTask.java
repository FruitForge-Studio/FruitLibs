package com.fruitforge.fruitLibs.api.scheduler;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ChainableTask<T> {

    <U> ChainableTask<U> thenAsync(Function<T, U> function);

    <U> ChainableTask<U> thenSync(Function<T, U> function);

    ChainableTask<T> thenAcceptAsync(Consumer<T> consumer);

    ChainableTask<T> thenAcceptSync(Consumer<T> consumer);

    ChainableTask<T> delay(long ticks);

    ChainableTask<T> onError(ErrorHandler handler);

    void execute();
}