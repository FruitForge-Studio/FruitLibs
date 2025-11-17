package com.fruitforge.fruitLibs.core.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.Consumer;

public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        GUIRegistry.getOpenGUI(player.getUniqueId()).ifPresent(gui -> {
            event.setCancelled(true);

            if (gui instanceof SimpleGUI simpleGUI) {
                Consumer<com.fruitforge.fruitLibs.api.gui.GUIClickEvent> handler =
                        simpleGUI.getClickHandler(event.getSlot());

                if (handler != null) {
                    GUIClickEventImpl guiEvent = new GUIClickEventImpl(event);
                    handler.accept(guiEvent);
                }
            }
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        GUIRegistry.unregister(player.getUniqueId());
    }
}