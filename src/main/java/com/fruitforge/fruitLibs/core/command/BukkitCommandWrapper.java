package com.fruitforge.fruitLibs.core.command;

import com.fruitforge.fruitLibs.api.command.CommandContext;
import com.fruitforge.fruitLibs.core.logging.LogManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BukkitCommandWrapper implements CommandExecutor, TabCompleter {

    private final CommandNode commandNode;
    private final LogManager logManager;

    public BukkitCommandWrapper(CommandNode commandNode, LogManager logManager) {
        this.commandNode = commandNode;
        this.logManager = logManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        com.fruitforge.fruitLibs.api.command.Command cmdAnnotation = commandNode.getCommand();

        if (cmdAnnotation.playerOnly() && !(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by players.");
            return true;
        }

        if (!cmdAnnotation.permission().isEmpty() && !sender.hasPermission(cmdAnnotation.permission())) {
            sender.sendMessage("§cYou don't have permission to execute this command.");
            return true;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        SubcommandNode subcommand = commandNode.getSubcommand(args).orElse(null);

        if (subcommand == null) {
            sender.sendMessage("§cUnknown subcommand. Use /" + command.getName() + " help");
            return true;
        }

        if (subcommand.getSubcommand().playerOnly() && !(sender instanceof Player)) {
            sender.sendMessage("§cThis subcommand can only be executed by players.");
            return true;
        }

        if (!subcommand.getSubcommand().permission().isEmpty() &&
                !sender.hasPermission(subcommand.getSubcommand().permission())) {
            sender.sendMessage("§cYou don't have permission to execute this subcommand.");
            return true;
        }

        String[] subArgs = Arrays.copyOfRange(args, subcommand.getPathLength(), args.length);
        CommandContext context = new CommandContextImpl(sender, subArgs);

        try {
            subcommand.execute(context);
        } catch (Exception e) {
            sender.sendMessage("§cAn error occurred while executing the command.");
            logManager.error("Error executing command: " + command.getName(), e);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        List<String> suggestions = new ArrayList<>();
        String currentArg = args[args.length - 1].toLowerCase();

        for (SubcommandNode subcommand : commandNode.getSubcommands()) {
            String[] path = subcommand.getPath();

            if (args.length <= path.length) {
                String pathPart = path[args.length - 1];

                if (pathPart.toLowerCase().startsWith(currentArg)) {
                    if (!subcommand.getSubcommand().permission().isEmpty() &&
                            !sender.hasPermission(subcommand.getSubcommand().permission())) {
                        continue;
                    }
                    suggestions.add(pathPart);
                }
            }
        }

        return suggestions.stream().distinct().collect(Collectors.toList());
    }

    private void showHelp(CommandSender sender) {
        com.fruitforge.fruitLibs.api.command.Command cmd = commandNode.getCommand();

        sender.sendMessage("§8§m                                           ");
        sender.sendMessage("§6" + cmd.name() + " §7- §e" + cmd.description());
        sender.sendMessage("");
        sender.sendMessage("§7Subcommands:");

        for (SubcommandNode subcommand : commandNode.getSubcommands()) {
            if (!subcommand.getSubcommand().permission().isEmpty() &&
                    !sender.hasPermission(subcommand.getSubcommand().permission())) {
                continue;
            }

            String path = String.join(" ", subcommand.getPath());
            sender.sendMessage("  §e/" + cmd.name() + " " + path + " §8- §7" +
                    subcommand.getSubcommand().description());
        }

        sender.sendMessage("§8§m                                           ");
    }
}