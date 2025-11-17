package com.fruitforge.fruitLibs.core.command;

import com.fruitforge.fruitLibs.api.command.Command;

import java.util.*;

public class CommandNode {

    private final Command command;
    private final Object instance;
    private final Map<String, SubcommandNode> subcommands;

    public CommandNode(Command command, Object instance) {
        this.command = command;
        this.instance = instance;
        this.subcommands = new HashMap<>();
    }

    public void addSubcommand(SubcommandNode subcommand) {
        String[] path = subcommand.getPath();
        subcommands.put(String.join(" ", path).toLowerCase(), subcommand);
    }

    public Optional<SubcommandNode> getSubcommand(String[] args) {
        for (int i = args.length; i > 0; i--) {
            String path = String.join(" ", Arrays.copyOfRange(args, 0, i)).toLowerCase();
            SubcommandNode node = subcommands.get(path);
            if (node != null) {
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    public Collection<SubcommandNode> getSubcommands() {
        return subcommands.values();
    }

    public Command getCommand() {
        return command;
    }

    public Object getInstance() {
        return instance;
    }
}