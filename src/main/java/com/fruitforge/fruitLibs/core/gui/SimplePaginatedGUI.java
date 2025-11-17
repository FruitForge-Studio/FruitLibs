package com.fruitforge.fruitLibs.core.gui;

import com.fruitforge.fruitLibs.api.gui.PaginatedGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimplePaginatedGUI extends SimpleGUI implements PaginatedGUI {

    private final List<ItemStack> items;
    private final List<Integer> itemSlots;
    private int currentPage;
    private int nextPageSlot;
    private int previousPageSlot;
    private ItemStack nextPageItem;
    private ItemStack previousPageItem;
    private Consumer<Integer> pageChangeHandler;

    public SimplePaginatedGUI(String title, int rows, int[] itemSlots) {
        super(title, rows);
        this.items = new ArrayList<>();
        this.itemSlots = new ArrayList<>();

        for (int slot : itemSlots) {
            this.itemSlots.add(slot);
        }

        this.currentPage = 0;
        this.nextPageSlot = size - 1;
        this.previousPageSlot = size - 9;
    }

    @Override
    public void setItems(List<ItemStack> items) {
        this.items.clear();
        this.items.addAll(items);
        updatePage();
    }

    @Override
    public void addItem(ItemStack item) {
        this.items.add(item);
        updatePage();
    }

    @Override
    public void nextPage() {
        if (hasNextPage()) {
            currentPage++;
            updatePage();

            if (pageChangeHandler != null) {
                pageChangeHandler.accept(currentPage);
            }
        }
    }

    @Override
    public void previousPage() {
        if (hasPreviousPage()) {
            currentPage--;
            updatePage();

            if (pageChangeHandler != null) {
                pageChangeHandler.accept(currentPage);
            }
        }
    }

    @Override
    public void setPage(int page) {
        if (page >= 0 && page < getTotalPages()) {
            currentPage = page;
            updatePage();

            if (pageChangeHandler != null) {
                pageChangeHandler.accept(currentPage);
            }
        }
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public int getTotalPages() {
        return (int) Math.ceil((double) items.size() / itemSlots.size());
    }

    @Override
    public boolean hasNextPage() {
        return currentPage < getTotalPages() - 1;
    }

    @Override
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    @Override
    public void setNextPageItem(int slot, ItemStack item) {
        this.nextPageSlot = slot;
        this.nextPageItem = item;
        updateNavigationItems();
    }

    @Override
    public void setPreviousPageItem(int slot, ItemStack item) {
        this.previousPageSlot = slot;
        this.previousPageItem = item;
        updateNavigationItems();
    }

    private void updatePage() {
        for (int slot : itemSlots) {
            inventory.setItem(slot, null);
            clickHandlers.remove(slot);
        }

        int startIndex = currentPage * itemSlots.size();
        int endIndex = Math.min(startIndex + itemSlots.size(), items.size());

        for (int i = startIndex; i < endIndex; i++) {
            int slotIndex = i - startIndex;
            if (slotIndex < itemSlots.size()) {
                int slot = itemSlots.get(slotIndex);
                inventory.setItem(slot, items.get(i));
            }
        }

        updateNavigationItems();
    }

    private void updateNavigationItems() {
        if (hasPreviousPage() && previousPageItem != null) {
            setItem(previousPageSlot, previousPageItem, event -> previousPage());
        } else {
            removeItem(previousPageSlot);
        }

        if (hasNextPage() && nextPageItem != null) {
            setItem(nextPageSlot, nextPageItem, event -> nextPage());
        } else {
            removeItem(nextPageSlot);
        }
    }

    @Override
    public void open(Player player) {
        updatePage();
        super.open(player);
    }

    public void setPageChangeHandler(Consumer<Integer> pageChangeHandler) {
        this.pageChangeHandler = pageChangeHandler;
    }
}