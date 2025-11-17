package com.fruitforge.fruitLibs.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface GUIBuilder {

    GUIBuilder title(String title);

    GUIBuilder rows(int rows);

    GUIBuilder item(int slot, ItemStack item);

    GUIBuilder item(int slot, ItemStack item, Consumer<GUIClickEvent> onClick);

    GUIBuilder fill(ItemStack item);

    GUIBuilder fillBorder(ItemStack item);

    GUIBuilder onClose(Consumer<Player> onClose);

    GUI build();

    PaginatedGUIBuilder paginated();
}