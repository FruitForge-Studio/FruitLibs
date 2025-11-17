package com.fruitforge.fruitLibs.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface GUIClickEvent {

    Player getPlayer();

    int getSlot();

    ItemStack getClickedItem();

    ClickType getClickType();

    boolean isLeftClick();

    boolean isRightClick();

    boolean isShiftClick();

    void setCancelled(boolean cancelled);

    boolean isCancelled();
}