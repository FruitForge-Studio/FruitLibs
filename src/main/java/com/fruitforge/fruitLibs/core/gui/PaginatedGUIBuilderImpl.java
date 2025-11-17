package com.fruitforge.fruitLibs.core.gui;

import com.fruitforge.fruitLibs.api.gui.PaginatedGUI;
import com.fruitforge.fruitLibs.api.gui.PaginatedGUIBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PaginatedGUIBuilderImpl implements PaginatedGUIBuilder {

    private String title;
    private int rows;
    private List<ItemStack> paginatedItems;
    private int[] itemSlots;
    private ItemStack nextPageItem;
    private ItemStack previousPageItem;
    private int nextPageSlot = -1;
    private int previousPageSlot = -1;
    private Consumer<Integer> pageChangeHandler;
    private ItemStack fillItem;
    private ItemStack borderItem;
    private Consumer<Player> closeHandler;

    public PaginatedGUIBuilderImpl(String title, int rows, ItemStack fillItem,
                                   ItemStack borderItem, Consumer<Player> closeHandler) {
        this.title = title;
        this.rows = rows;
        this.fillItem = fillItem;
        this.borderItem = borderItem;
        this.closeHandler = closeHandler;
    }

    @Override
    public PaginatedGUIBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public PaginatedGUIBuilder rows(int rows) {
        this.rows = Math.max(1, Math.min(6, rows));
        return this;
    }

    @Override
    public PaginatedGUIBuilder items(List<ItemStack> items) {
        this.paginatedItems = new ArrayList<>(items);
        return this;
    }

    @Override
    public PaginatedGUIBuilder nextPageItem(int slot, ItemStack item) {
        this.nextPageSlot = slot;
        this.nextPageItem = item;
        return this;
    }

    @Override
    public PaginatedGUIBuilder previousPageItem(int slot, ItemStack item) {
        this.previousPageSlot = slot;
        this.previousPageItem = item;
        return this;
    }

    @Override
    public PaginatedGUIBuilder itemSlots(int... slots) {
        this.itemSlots = slots;
        return this;
    }

    @Override
    public PaginatedGUIBuilder onPageChange(Consumer<Integer> onPageChange) {
        this.pageChangeHandler = onPageChange;
        return this;
    }

    @Override
    public PaginatedGUI build() {
        if (itemSlots == null || itemSlots.length == 0) {
            int size = rows * 9;
            List<Integer> defaultSlots = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                defaultSlots.add(i);
            }
            itemSlots = defaultSlots.stream().mapToInt(Integer::intValue).toArray();
        }

        SimplePaginatedGUI gui = new SimplePaginatedGUI(title, rows, itemSlots);

        if (borderItem != null) {
            gui.fillBorder(borderItem);
        }

        if (fillItem != null) {
            gui.fill(fillItem);
        }

        if (paginatedItems != null) {
            gui.setItems(paginatedItems);
        }

        if (nextPageItem != null && nextPageSlot != -1) {
            gui.setNextPageItem(nextPageSlot, nextPageItem);
        }

        if (previousPageItem != null && previousPageSlot != -1) {
            gui.setPreviousPageItem(previousPageSlot, previousPageItem);
        }

        if (pageChangeHandler != null) {
            gui.setPageChangeHandler(pageChangeHandler);
        }

        if (closeHandler != null) {
            gui.setCloseHandler(closeHandler);
        }

        return gui;
    }
}