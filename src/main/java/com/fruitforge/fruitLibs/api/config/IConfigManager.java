// src/main/java/com/fruitforge/fruitLibs/api/config/IConfigManager.java
package com.fruitforge.fruitLibs.api.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Optional;

public interface IConfigManager {

    boolean loadConfig(String fileName);

    boolean reloadConfig(String fileName);

    boolean saveConfig(String fileName);

    Optional<FileConfiguration> getConfig(String fileName);

    FileConfiguration getOrCreateConfig(String fileName);

    void setDefault(String fileName, String path, Object value);

    boolean validateVersion(String fileName, String expectedVersion);
}