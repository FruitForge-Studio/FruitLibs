package com.fruitforge.fruitLibs.api.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface ItemBuilder {

    ItemBuilder type(Material material);

    ItemBuilder amount(int amount);

    ItemBuilder name(String name);

    ItemBuilder lore(String... lines);

    ItemBuilder lore(List<String> lines);

    ItemBuilder addLoreLine(String line);

    ItemBuilder enchant(Enchantment enchantment, int level);

    ItemBuilder enchant(Enchantment enchantment);

    ItemBuilder removeEnchant(Enchantment enchantment);

    ItemBuilder flags(ItemFlag... flags);

    ItemBuilder removeFlags(ItemFlag... flags);

    ItemBuilder unbreakable(boolean unbreakable);

    ItemBuilder customModelData(int data);

    ItemBuilder glow(boolean glow);

    ItemBuilder skull(String base64Texture);

    ItemBuilder skullOwner(String playerName);

    ItemBuilder nbt(String key, Object value);

    ItemBuilder nbt(Map<String, Object> nbtData);

    ItemBuilder durability(int durability);

    ItemBuilder modify(Consumer<ItemStack> modifier);

    ItemStack build();

    ItemBuilder clone();
}