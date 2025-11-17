// src/main/java/com/fruitforge/fruitLibs/core/messages/MessageFormatter.java
package com.fruitforge.fruitLibs.core.messages;

import com.fruitforge.fruitLibs.util.ColorUtil;

import java.util.Map;

final class MessageFormatter {

    private final boolean colorized;

    MessageFormatter(boolean colorized) {
        this.colorized = colorized;
    }

    String format(String message, Map<String, String> placeholders) {
        if (message == null) {
            return "";
        }

        if (placeholders != null && !placeholders.isEmpty()) {
            message = replacePlaceholders(message, placeholders);
        }

        return colorized ? ColorUtil.colorize(message) : message;
    }

    private String replacePlaceholders(String message, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            message = message.replace(placeholder, entry.getValue());
        }
        return message;
    }
}