package com.fruitforge.fruitLibs.core.gui;

import com.fruitforge.fruitLibs.api.gui.GUI;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class GUIRegistry {

    private static final Map<UUID, GUI> openGUIs = new ConcurrentHashMap<>();

    private GUIRegistry() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void register(UUID playerId, GUI gui) {
        openGUIs.put(playerId, gui);
    }

    public static void unregister(UUID playerId) {
        openGUIs.remove(playerId);
    }

    public static Optional<GUI> getOpenGUI(UUID playerId) {
        return Optional.ofNullable(openGUIs.get(playerId));
    }

    public static boolean hasOpenGUI(UUID playerId) {
        return openGUIs.containsKey(playerId);
    }

    public static void closeAll() {
        openGUIs.clear();
    }
}