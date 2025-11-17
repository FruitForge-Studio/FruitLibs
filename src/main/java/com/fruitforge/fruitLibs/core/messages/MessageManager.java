// src/main/java/com/fruitforge/fruitLibs/core/messages/MessageManager.java
package com.fruitforge.fruitLibs.core.messages;

import com.fruitforge.fruitLibs.api.messages.IMessageManager;
import com.fruitforge.fruitLibs.core.config.ConfigManager;
import com.fruitforge.fruitLibs.core.logging.LogManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public final class MessageManager implements IMessageManager {

    private static final String MESSAGES_FILE = "messages.yml";

    private final ConfigManager configManager;
    private final LogManager logManager;
    private MessageFormatter formatter;

    public MessageManager(ConfigManager configManager, LogManager logManager) {
        this.configManager = configManager;
        this.logManager = logManager;
        this.formatter = new MessageFormatter(true);

        initialize();
    }

    private void initialize() {
        if (!configManager.loadConfig(MESSAGES_FILE)) {
            logManager.error("Failed to load messages configuration");
        }
    }

    @Override
    public void sendMessage(CommandSender sender, String key) {
        sendMessage(sender, key, null);
    }

    @Override
    public void sendMessage(CommandSender sender, String key, Map<String, String> placeholders) {
        String message = getMessage(key, placeholders);
        if (!message.isEmpty()) {
            sender.sendMessage(message);
        }
    }

    @Override
    public String getMessage(String key) {
        return getMessage(key, null);
    }

    @Override
    public String getMessage(String key, Map<String, String> placeholders) {
        FileConfiguration config = configManager.getOrCreateConfig(MESSAGES_FILE);
        String rawMessage = config.getString(key);

        if (rawMessage == null) {
            logManager.debug("Message key not found: " + key);
            return "";
        }

        return formatter.format(rawMessage, placeholders);
    }

    @Override
    public void reload() {
        configManager.reloadConfig(MESSAGES_FILE);
        logManager.info("Messages reloaded");
    }

    @Override
    public boolean isColorized() {
        return true;
    }

    @Override
    public void setColorized(boolean colorized) {
        this.formatter = new MessageFormatter(colorized);
    }
}