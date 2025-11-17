package com.fruitforge.fruitLibs.core.gui;

import com.fruitforge.fruitLibs.api.gui.GUIClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIClickEventImpl implements GUIClickEvent {

    private final InventoryClickEvent event;

    public GUIClickEventImpl(InventoryClickEvent event) {
        this.event = event;
    }

    @Override
    public Player getPlayer() {
        return (Player) event.getWhoClicked();
    }

    @Override
    public int getSlot() {
        return event.getSlot();
    }

    @Override
    public ItemStack getClickedItem() {
        return event.getCurrentItem();
    }

    @Override
    public ClickType getClickType() {
        return event.getClick();
    }

    @Override
    public boolean isLeftClick() {
        return event.getClick().isLeftClick();
    }

    @Override
    public boolean isRightClick() {
        return event.getClick().isRightClick();
    }

    @Override
    public boolean isShiftClick() {
        return event.getClick().isShiftClick();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }
}