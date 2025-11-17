package com.fruitforge.fruitLibs.api.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface CommandContext {

    CommandSender getSender();

    Player getPlayer();

    String[] getArgs();

    String getArg(int index);

    String getArg(int index, String defaultValue);

    int getArgAsInt(int index);

    int getArgAsInt(int index, int defaultValue);

    double getArgAsDouble(int index);

    double getArgAsDouble(int index, double defaultValue);

    boolean getArgAsBoolean(int index);

    boolean getArgAsBoolean(int index, boolean defaultValue);

    Player getArgAsPlayer(int index);

    int getArgCount();

    boolean hasArg(int index);

    void reply(String message);

    void replyError(String message);

    void replySuccess(String message);
}