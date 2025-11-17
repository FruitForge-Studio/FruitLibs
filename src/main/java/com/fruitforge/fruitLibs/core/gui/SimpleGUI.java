package com.fruitforge.fruitLibs.core.gui;

import com.fruitforge.fruitLibs.api.gui.GUI;
import com.fruitforge.fruitLibs.api.gui.GUIClickEvent;
import com.fruitforge.fruitLibs.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SimpleGUI implements GUI {

    protected final UUID guiId;
    protected String title;
    protected int size;
    protected Inventory inventory;
    protected final Map<Integer, Consumer<GUIClickEvent>> clickHandlers;
    protected Consumer<Player> closeHandler;

    public SimpleGUI(String title, int rows) {
        this.guiId = UUID.randomUUID();
        this.title = ColorUtil.colorize(title);
        this.size = rows * 9;
        this.clickHandlers = new HashMap<>();
        this.inventory = Bukkit.createInventory(null, size, this.title);
    }

    @Override
    public void open(Player player) {
        GUIRegistry.register(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    @Override
    public void close(Player player) {
        player.closeInventory();
        GUIRegistry.unregister(player.getUniqueId());

        if (closeHandler != null) {
            closeHandler.accept(player);
        }
    }

    @Override
    public void refresh() {
        inventory.clear();
        clickHandlers.clear();
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    @Override
    public void setItem(int slot, ItemStack item, Consumer<GUIClickEvent> onClick) {
        if (slot < 0 || slot >= size) {
            return;
        }

        inventory.setItem(slot, item);

        if (onClick != null) {
            clickHandlers.put(slot, onClick);
        } else {
            clickHandlers.remove(slot);
        }
    }

    @Override
    public void removeItem(int slot) {
        inventory.setItem(slot, null);
        clickHandlers.remove(slot);
    }

    @Override
    public void fill(ItemStack item) {
        for (int i = 0; i < size; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, item);
            }
        }
    }

    @Override
    public void fillBorder(ItemStack item) {
        int rows = size / 9;

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, item);
        }

        for (int i = (rows - 1) * 9; i < size; i++) {
            inventory.setItem(i, item);
        }

        for (int i = 1; i < rows - 1; i++) {
            inventory.setItem(i * 9, item);
            inventory.setItem(i * 9 + 8, item);
        }
    }

    @Override
    public void setTitle(String title) {
        this.title = ColorUtil.colorize(title);

        Inventory newInventory = Bukkit.createInventory(null, size, this.title);
        newInventory.setContents(inventory.getContents());
        this.inventory = newInventory;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public UUID getGuiId() {
        return guiId;
    }

    public Consumer<GUIClickEvent> getClickHandler(int slot) {
        return clickHandlers.get(slot);
    }

    public void setCloseHandler(Consumer<Player> closeHandler) {
        this.closeHandler = closeHandler;
    }
}