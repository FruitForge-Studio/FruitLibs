// src/main/java/com/fruitforge/fruitLibs/api/messages/IMessageManager.java
package com.fruitforge.fruitLibs.api.messages;

import org.bukkit.command.CommandSender;

import java.util.Map;

public interface IMessageManager {

    void sendMessage(CommandSender sender, String key);

    void sendMessage(CommandSender sender, String key, Map<String, String> placeholders);

    String getMessage(String key);

    String getMessage(String key, Map<String, String> placeholders);

    void reload();

    boolean isColorized();

    void setColorized(boolean colorized);
}