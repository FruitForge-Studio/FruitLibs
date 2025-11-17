package com.fruitforge.fruitLibs.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface GUI {

    void open(Player player);

    void close(Player player);

    void refresh();

    void setItem(int slot, ItemStack item);

    void setItem(int slot, ItemStack item, Consumer<GUIClickEvent> onClick);

    void removeItem(int slot);

    void fill(ItemStack item);

    void fillBorder(ItemStack item);

    void setTitle(String title);

    int getSize();

    String getTitle();
}