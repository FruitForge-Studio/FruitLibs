// src/main/java/com/fruitforge/fruitLibs/core/logging/LogManager.java
package com.fruitforge.fruitLibs.core.logging;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class LogManager {

    private final JavaPlugin plugin;
    private boolean debugMode;

    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String GRAY = "\u001B[90m";
    private static final String MAGENTA = "\u001B[35m";

    private static final String SPIGOTMC = "\u001B[38;2;222;147;2m";
    private static final String POLYMART = "\u001B[38;2;0;134;136m";
    private static final String BUILTBYBIT = "\u001B[38;2;45;135;195m";
    private static final String MODRINTH = "\u001B[38;2;30;195;55m";

    public LogManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.debugMode = false;
    }

    public void info(String message) {
        plugin.getLogger().log(Level.INFO, CYAN + "[INFO] " + RESET + message);
    }

    public void success(String message) {
        plugin.getLogger().log(Level.INFO, GREEN + "[SUCCESS] " + RESET + message);
    }

    public void warning(String message) {
        plugin.getLogger().log(Level.WARNING, YELLOW + "[WARNING] " + RESET + message);
    }

    public void error(String message) {
        plugin.getLogger().log(Level.SEVERE, RED + "[ERROR] " + RESET + message);
    }

    public void error(String message, Throwable throwable) {
        plugin.getLogger().log(Level.SEVERE, RED + "[ERROR] " + RESET + message, throwable);
    }

    public void debug(String message) {
        if (debugMode) {
            plugin.getLogger().log(Level.INFO, GRAY + "[DEBUG] " + RESET + message);
        }
    }

    public void spigotMC(String message) {
        plugin.getLogger().log(Level.INFO, SPIGOTMC + "[SPIGOTMC] " + RESET + message);
    }

    public void polymart(String message) {
        plugin.getLogger().log(Level.INFO, POLYMART + "[POLYMART] " + RESET + message);
    }

    public void builtByBit(String message) {
        plugin.getLogger().log(Level.INFO, BUILTBYBIT + "[BUILTBYBIT] " + RESET + message);
    }

    public void modrinth(String message) {
        plugin.getLogger().log(Level.INFO, MODRINTH + "[MODRINTH] " + RESET + message);
    }

    public void custom(String prefix, String rgbColor, String message) {
        String[] rgb = rgbColor.split(",");
        if (rgb.length == 3) {
            String ansiColor = String.format("\u001B[38;2;%s;%s;%sm",
                    rgb[0].trim(), rgb[1].trim(), rgb[2].trim());
            plugin.getLogger().log(Level.INFO, ansiColor + "[" + prefix + "] " + RESET + message);
        } else {
            plugin.getLogger().log(Level.INFO, MAGENTA + "[" + prefix + "] " + RESET + message);
        }
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isDebugMode() {
        return debugMode;
    }
}