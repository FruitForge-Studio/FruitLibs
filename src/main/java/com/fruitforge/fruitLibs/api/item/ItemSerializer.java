package com.fruitforge.fruitLibs.api.item;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface ItemSerializer {

    Map<String, Object> serialize(ItemStack item);

    ItemStack deserialize(Map<String, Object> data);

    String toBase64(ItemStack item);

    ItemStack fromBase64(String base64);

    String toJson(ItemStack item);

    ItemStack fromJson(String json);
}