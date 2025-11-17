// src/main/java/com/fruitforge/fruitLibs/core/config/ConfigLoader.java
package com.fruitforge.fruitLibs.core.config;

import com.fruitforge.fruitLibs.core.logging.LogManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

final class ConfigLoader {

    private final JavaPlugin plugin;
    private final LogManager logManager;

    ConfigLoader(JavaPlugin plugin, LogManager logManager) {
        this.plugin = plugin;
        this.logManager = logManager;
    }

    FileConfiguration load(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            createDefaultConfig(fileName, file);
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    boolean save(String fileName, FileConfiguration config) {
        File file = new File(plugin.getDataFolder(), fileName);

        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            logManager.error("Failed to save config: " + fileName, e);
            return false;
        }
    }

    private void createDefaultConfig(String fileName, File file) {
        try {
            file.getParentFile().mkdirs();

            InputStream resource = plugin.getResource(fileName);
            if (resource != null) {
                FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(resource, StandardCharsets.UTF_8)
                );
                defaultConfig.save(file);
                logManager.info("Created default config: " + fileName);
            } else {
                file.createNewFile();
                logManager.warning("No default config found in resources: " + fileName);
            }
        } catch (IOException e) {
            logManager.error("Failed to create config file: " + fileName, e);
        }
    }
}