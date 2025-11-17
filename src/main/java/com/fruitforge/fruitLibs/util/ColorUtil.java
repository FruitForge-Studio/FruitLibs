// src/main/java/com/fruitforge/fruitLibs/util/ColorUtil.java
package com.fruitforge.fruitLibs.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.legacySection();

    private ColorUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String colorize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        text = translateHexCodes(text);
        text = translateMiniMessage(text);
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String strip(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return ChatColor.stripColor(text);
    }

    private static String translateHexCodes(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 32);

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            String replacement = net.md_5.bungee.api.ChatColor.of("#" + hexCode).toString();
            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String translateMiniMessage(String text) {
        try {
            Component component = MINI_MESSAGE.deserialize(text);
            return LEGACY_SERIALIZER.serialize(component);
        } catch (Exception e) {
            return text;
        }
    }
}