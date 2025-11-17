package com.fruitforge.fruitLibs.core.scheduler;

import com.fruitforge.fruitLibs.api.scheduler.Task;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class TaskRegistry {

    private static final Set<Task> activeTasks = ConcurrentHashMap.newKeySet();

    private TaskRegistry() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void register(Task task) {
        activeTasks.add(task);
    }

    public static void unregister(Task task) {
        activeTasks.remove(task);
    }

    public static void cancelAll() {
        activeTasks.forEach(Task::cancel);
        activeTasks.clear();
    }

    public static int getActiveTaskCount() {
        return activeTasks.size();
    }

    public static Set<Task> getActiveTasks() {
        return Set.copyOf(activeTasks);
    }
}