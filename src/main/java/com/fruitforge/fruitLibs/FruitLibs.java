package com.fruitforge.fruitLibs;

import revxrsal.zapper.ZapperJavaPlugin;

public final class FruitLibs extends ZapperJavaPlugin {

    private static FruitLibs instance;

    @Override
    public void onEnable() {
        instance = this;

        long startTime = System.currentTimeMillis();
        getLogger().info("Initializing FruitLibs v" + getDescription().getVersion());

        long endTime = System.currentTimeMillis();
        getLogger().info("FruitLibs loaded successfully in " + (endTime - startTime) + "ms");
    }

    @Override
    public void onDisable() {
        getLogger().info("FruitLibs disabled.");
        instance = null;
    }

    public static FruitLibs getInstance() {
        return instance;
    }
}