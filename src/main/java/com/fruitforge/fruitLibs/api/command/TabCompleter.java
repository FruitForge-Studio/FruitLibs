package com.fruitforge.fruitLibs.api.command;

import org.bukkit.command.CommandSender;

import java.util.List;

@FunctionalInterface
public interface TabCompleter {

    List<String> complete(CommandSender sender, String[] args);
}