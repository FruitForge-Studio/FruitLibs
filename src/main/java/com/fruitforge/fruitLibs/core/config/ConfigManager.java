// src/main/java/com/fruitforge/fruitLibs/core/config/ConfigManager.java
package com.fruitforge.fruitLibs.core.config;

import com.fruitforge.fruitLibs.api.config.IConfigManager;
import com.fruitforge.fruitLibs.core.logging.LogManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager implements IConfigManager {

    private final Map<String, FileConfiguration> configs;
    private final ConfigLoader loader;
    private final LogManager logManager;

    public ConfigManager(JavaPlugin plugin, LogManager logManager) {
        this.configs = new ConcurrentHashMap<>();
        this.loader = new ConfigLoader(plugin, logManager);
        this.logManager = logManager;
    }

    @Override
    public boolean loadConfig(String fileName) {
        try {
            FileConfiguration config = loader.load(fileName);
            configs.put(fileName, config);
            logManager.debug("Loaded config: " + fileName);
            return true;
        } catch (Exception e) {
            logManager.error("Failed to load config: " + fileName, e);
            return false;
        }
    }

    @Override
    public boolean reloadConfig(String fileName) {
        configs.remove(fileName);
        return loadConfig(fileName);
    }

    @Override
    public boolean saveConfig(String fileName) {
        FileConfiguration config = configs.get(fileName);
        if (config == null) {
            logManager.error("Cannot save non-loaded config: " + fileName);
            return false;
        }
        return loader.save(fileName, config);
    }

    @Override
    public Optional<FileConfiguration> getConfig(String fileName) {
        return Optional.ofNullable(configs.get(fileName));
    }

    @Override
    public FileConfiguration getOrCreateConfig(String fileName) {
        return configs.computeIfAbsent(fileName, key -> loader.load(key));
    }

    @Override
    public void setDefault(String fileName, String path, Object value) {
        FileConfiguration config = getOrCreateConfig(fileName);
        if (!config.contains(path)) {
            config.set(path, value);
            saveConfig(fileName);
            logManager.debug("Set default value for " + path + " in " + fileName);
        }
    }

    @Override
    public boolean validateVersion(String fileName, String expectedVersion) {
        FileConfiguration config = getOrCreateConfig(fileName);
        String currentVersion = config.getString("version", "unknown");

        if (!currentVersion.equals(expectedVersion)) {
            logManager.warning("Config version mismatch in " + fileName +
                    ": expected " + expectedVersion + ", found " + currentVersion);
            return false;
        }

        return true;
    }
}