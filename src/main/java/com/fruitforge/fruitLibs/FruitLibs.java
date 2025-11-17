package com.fruitforge.fruitLibs;

import com.fruitforge.fruitLibs.api.config.IConfigManager;
import com.fruitforge.fruitLibs.api.gui.GUIBuilder;
import com.fruitforge.fruitLibs.api.messages.IMessageManager;
import com.fruitforge.fruitLibs.core.config.ConfigManager;
import com.fruitforge.fruitLibs.core.gui.GUIBuilderImpl;
import com.fruitforge.fruitLibs.core.gui.GUIListener;
import com.fruitforge.fruitLibs.core.gui.GUIRegistry;
import com.fruitforge.fruitLibs.core.logging.LogManager;
import com.fruitforge.fruitLibs.core.messages.MessageManager;
import revxrsal.zapper.ZapperJavaPlugin;

public final class FruitLibs extends ZapperJavaPlugin {

    private static FruitLibs instance;

    private LogManager logManager;
    private ConfigManager configManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        instance = this;

        long startTime = System.currentTimeMillis();

        initializeManagers();
        registerListeners();

        long endTime = System.currentTimeMillis();
        logManager.success("FruitLibs v" + getDescription().getVersion() +
                " loaded in " + (endTime - startTime) + "ms");
    }

    @Override
    public void onDisable() {
        GUIRegistry.closeAll();
        logManager.info("FruitLibs disabled.");
        instance = null;
    }

    private void initializeManagers() {
        logManager = new LogManager(this);
        logManager.info("Initializing FruitLibs...");

        configManager = new ConfigManager(this, logManager);
        logManager.debug("ConfigManager initialized");

        messageManager = new MessageManager(configManager, logManager);
        logManager.debug("MessageManager initialized");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        logManager.debug("GUIListener registered");
    }

    public static FruitLibs getInstance() {
        return instance;
    }

    public IConfigManager getConfigManager() {
        return configManager;
    }

    public IMessageManager getMessageManager() {
        return messageManager;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public static GUIBuilder createGUI() {
        return new GUIBuilderImpl();
    }
}