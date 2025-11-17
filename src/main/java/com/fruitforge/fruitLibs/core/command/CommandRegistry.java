package com.fruitforge.fruitLibs.core.command;

import com.fruitforge.fruitLibs.api.command.Command;
import com.fruitforge.fruitLibs.api.command.Subcommand;
import com.fruitforge.fruitLibs.core.logging.LogManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CommandRegistry {

    private final JavaPlugin plugin;
    private final LogManager logManager;
    private final Map<String, CommandNode> commands;

    public CommandRegistry(JavaPlugin plugin, LogManager logManager) {
        this.plugin = plugin;
        this.logManager = logManager;
        this.commands = new ConcurrentHashMap<>();
    }

    public void register(Object commandInstance) {
        Class<?> clazz = commandInstance.getClass();

        if (!clazz.isAnnotationPresent(Command.class)) {
            logManager.error("Class " + clazz.getName() + " is not annotated with @Command");
            return;
        }

        Command commandAnnotation = clazz.getAnnotation(Command.class);
        CommandNode rootNode = new CommandNode(commandAnnotation, commandInstance);

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Subcommand.class)) {
                Subcommand subcommandAnnotation = method.getAnnotation(Subcommand.class);
                SubcommandNode subNode = new SubcommandNode(subcommandAnnotation, method, commandInstance);
                rootNode.addSubcommand(subNode);
            }
        }

        commands.put(commandAnnotation.name().toLowerCase(), rootNode);

        for (String alias : commandAnnotation.aliases()) {
            commands.put(alias.toLowerCase(), rootNode);
        }

        BukkitCommandWrapper wrapper = new BukkitCommandWrapper(rootNode, logManager);

        org.bukkit.command.PluginCommand bukkitCommand = plugin.getCommand(commandAnnotation.name());
        if (bukkitCommand != null) {
            bukkitCommand.setExecutor(wrapper);
            bukkitCommand.setTabCompleter(wrapper);
            logManager.debug("Registered command: " + commandAnnotation.name());
        } else {
            logManager.error("Command not found in plugin.yml: " + commandAnnotation.name());
        }
    }

    public void unregister(String commandName) {
        commands.remove(commandName.toLowerCase());
    }

    public Optional<CommandNode> getCommand(String name) {
        return Optional.ofNullable(commands.get(name.toLowerCase()));
    }

    public Collection<CommandNode> getAllCommands() {
        return new HashSet<>(commands.values());
    }
}