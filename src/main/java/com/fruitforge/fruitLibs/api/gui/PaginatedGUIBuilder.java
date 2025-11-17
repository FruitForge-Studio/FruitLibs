package com.fruitforge.fruitLibs.api.gui;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public interface PaginatedGUIBuilder {

    PaginatedGUIBuilder title(String title);

    PaginatedGUIBuilder rows(int rows);

    PaginatedGUIBuilder items(List<ItemStack> items);

    PaginatedGUIBuilder nextPageItem(int slot, ItemStack item);

    PaginatedGUIBuilder previousPageItem(int slot, ItemStack item);

    PaginatedGUIBuilder itemSlots(int... slots);

    PaginatedGUIBuilder onPageChange(Consumer<Integer> onPageChange);

    PaginatedGUI build();
}