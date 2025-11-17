package com.fruitforge.fruitLibs.core.command;

import com.fruitforge.fruitLibs.api.command.Subcommand;

import java.lang.reflect.Method;

public class SubcommandNode {

    private final Subcommand subcommand;
    private final Method method;
    private final Object instance;
    private final String[] path;

    public SubcommandNode(Subcommand subcommand, Method method, Object instance) {
        this.subcommand = subcommand;
        this.method = method;
        this.instance = instance;
        this.path = subcommand.value().split(" ");
        this.method.setAccessible(true);
    }

    public void execute(Object... args) throws Exception {
        method.invoke(instance, args);
    }

    public Subcommand getSubcommand() {
        return subcommand;
    }

    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return instance;
    }

    public String[] getPath() {
        return path;
    }

    public int getPathLength() {
        return path.length;
    }
}