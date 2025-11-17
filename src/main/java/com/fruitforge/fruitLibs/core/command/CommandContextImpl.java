package com.fruitforge.fruitLibs.core.command;

import com.fruitforge.fruitLibs.api.command.CommandContext;
import com.fruitforge.fruitLibs.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandContextImpl implements CommandContext {

    private final CommandSender sender;
    private final String[] args;

    public CommandContextImpl(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    @Override
    public CommandSender getSender() {
        return sender;
    }

    @Override
    public Player getPlayer() {
        return sender instanceof Player ? (Player) sender : null;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public String getArg(int index) {
        return hasArg(index) ? args[index] : null;
    }

    @Override
    public String getArg(int index, String defaultValue) {
        return hasArg(index) ? args[index] : defaultValue;
    }

    @Override
    public int getArgAsInt(int index) {
        return getArgAsInt(index, 0);
    }

    @Override
    public int getArgAsInt(int index, int defaultValue) {
        try {
            return hasArg(index) ? Integer.parseInt(args[index]) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public double getArgAsDouble(int index) {
        return getArgAsDouble(index, 0.0);
    }

    @Override
    public double getArgAsDouble(int index, double defaultValue) {
        try {
            return hasArg(index) ? Double.parseDouble(args[index]) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public boolean getArgAsBoolean(int index) {
        return getArgAsBoolean(index, false);
    }

    @Override
    public boolean getArgAsBoolean(int index, boolean defaultValue) {
        if (!hasArg(index)) {
            return defaultValue;
        }
        String arg = args[index].toLowerCase();
        return arg.equals("true") || arg.equals("yes") || arg.equals("1");
    }

    @Override
    public Player getArgAsPlayer(int index) {
        if (!hasArg(index)) {
            return null;
        }
        return Bukkit.getPlayer(args[index]);
    }

    @Override
    public int getArgCount() {
        return args.length;
    }

    @Override
    public boolean hasArg(int index) {
        return index >= 0 && index < args.length;
    }

    @Override
    public void reply(String message) {
        sender.sendMessage(ColorUtil.colorize(message));
    }

    @Override
    public void replyError(String message) {
        sender.sendMessage(ColorUtil.colorize("&c" + message));
    }

    @Override
    public void replySuccess(String message) {
        sender.sendMessage(ColorUtil.colorize("&a" + message));
    }
}