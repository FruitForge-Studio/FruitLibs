package com.fruitforge.fruitLibs.api.gui;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface PaginatedGUI extends GUI {

    void setItems(List<ItemStack> items);

    void addItem(ItemStack item);

    void nextPage();

    void previousPage();

    void setPage(int page);

    int getCurrentPage();

    int getTotalPages();

    boolean hasNextPage();

    boolean hasPreviousPage();

    void setNextPageItem(int slot, ItemStack item);

    void setPreviousPageItem(int slot, ItemStack item);
}