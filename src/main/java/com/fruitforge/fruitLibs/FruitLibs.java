package com.fruitforge.fruitLibs;

import com.fruitforge.fruitLibs.api.config.IConfigManager;
import com.fruitforge.fruitLibs.api.database.Database;
import com.fruitforge.fruitLibs.api.event.EventBus;
import com.fruitforge.fruitLibs.api.gui.GUIBuilder;
import com.fruitforge.fruitLibs.api.item.ItemBuilder;
import com.fruitforge.fruitLibs.api.item.ItemSerializer;
import com.fruitforge.fruitLibs.api.messages.IMessageManager;
import com.fruitforge.fruitLibs.api.scheduler.SchedulerBuilder;
import com.fruitforge.fruitLibs.core.command.CommandRegistry;
import com.fruitforge.fruitLibs.core.config.ConfigManager;
import com.fruitforge.fruitLibs.core.database.DatabaseConfig;
import com.fruitforge.fruitLibs.core.database.HikariDatabase;
import com.fruitforge.fruitLibs.core.database.MigrationManager;
import com.fruitforge.fruitLibs.core.event.SimpleEventBus;
import com.fruitforge.fruitLibs.core.gui.GUIBuilderImpl;
import com.fruitforge.fruitLibs.core.gui.GUIListener;
import com.fruitforge.fruitLibs.core.gui.GUIRegistry;
import com.fruitforge.fruitLibs.core.item.SimpleItemBuilder;
import com.fruitforge.fruitLibs.core.item.SimpleItemSerializer;
import com.fruitforge.fruitLibs.core.logging.LogManager;
import com.fruitforge.fruitLibs.core.messages.MessageManager;
import com.fruitforge.fruitLibs.core.scheduler.SchedulerBuilderImpl;
import com.fruitforge.fruitLibs.core.scheduler.TaskRegistry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import revxrsal.zapper.ZapperJavaPlugin;

public final class FruitLibs extends ZapperJavaPlugin {

    private static FruitLibs instance;

    private LogManager logManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private ItemSerializer itemSerializer;
    private CommandRegistry commandRegistry;
    private SchedulerBuilder schedulerBuilder;
    private EventBus eventBus;

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
        if (eventBus != null) {
            eventBus.unregisterAll();
        }
        TaskRegistry.cancelAll();
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

        itemSerializer = new SimpleItemSerializer();
        logManager.debug("ItemSerializer initialized");

        commandRegistry = new CommandRegistry(this, logManager);
        logManager.debug("CommandRegistry initialized");

        schedulerBuilder = new SchedulerBuilderImpl(this, logManager);
        logManager.debug("SchedulerBuilder initialized");

        eventBus = new SimpleEventBus(logManager);
        logManager.debug("EventBus initialized");
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

    public ItemSerializer getItemSerializer() {
        return itemSerializer;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public static GUIBuilder createGUI() {
        return new GUIBuilderImpl();
    }

    public static ItemBuilder createItem(Material material) {
        return new SimpleItemBuilder(material);
    }

    public static ItemBuilder createItem(ItemStack item) {
        return new SimpleItemBuilder(item);
    }

    public static Database createDatabase(DatabaseConfig config) {
        return new HikariDatabase(config, getInstance().getLogManager());
    }

    public static MigrationManager createMigrationManager(Database database) {
        return new MigrationManager(database, getInstance().getLogManager());
    }

    public static SchedulerBuilder scheduler() {
        return getInstance().schedulerBuilder;
    }

    public static EventBus eventBus() {
        return getInstance().eventBus;
    }
}