package com.fruitforge.fruitLibs.core.gui;

import com.fruitforge.fruitLibs.api.gui.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GUIBuilderImpl implements GUIBuilder {

    private String title = "GUI";
    private int rows = 3;
    private final List<ItemPlacement> items = new ArrayList<>();
    private ItemStack fillItem;
    private ItemStack borderItem;
    private Consumer<Player> closeHandler;

    @Override
    public GUIBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public GUIBuilder rows(int rows) {
        this.rows = Math.max(1, Math.min(6, rows));
        return this;
    }

    @Override
    public GUIBuilder item(int slot, ItemStack item) {
        return item(slot, item, null);
    }

    @Override
    public GUIBuilder item(int slot, ItemStack item, Consumer<GUIClickEvent> onClick) {
        items.add(new ItemPlacement(slot, item, onClick));
        return this;
    }

    @Override
    public GUIBuilder fill(ItemStack item) {
        this.fillItem = item;
        return this;
    }

    @Override
    public GUIBuilder fillBorder(ItemStack item) {
        this.borderItem = item;
        return this;
    }

    @Override
    public GUIBuilder onClose(Consumer<Player> onClose) {
        this.closeHandler = onClose;
        return this;
    }

    @Override
    public GUI build() {
        SimpleGUI gui = new SimpleGUI(title, rows);

        if (borderItem != null) {
            gui.fillBorder(borderItem);
        }

        if (fillItem != null) {
            gui.fill(fillItem);
        }

        for (ItemPlacement placement : items) {
            gui.setItem(placement.slot, placement.item, placement.onClick);
        }

        if (closeHandler != null) {
            gui.setCloseHandler(closeHandler);
        }

        return gui;
    }

    @Override
    public PaginatedGUIBuilder paginated() {
        return new PaginatedGUIBuilderImpl(title, rows, fillItem, borderItem, closeHandler);
    }

    private static class ItemPlacement {
        final int slot;
        final ItemStack item;
        final Consumer<GUIClickEvent> onClick;

        ItemPlacement(int slot, ItemStack item, Consumer<GUIClickEvent> onClick) {
            this.slot = slot;
            this.item = item;
            this.onClick = onClick;
        }
    }
}