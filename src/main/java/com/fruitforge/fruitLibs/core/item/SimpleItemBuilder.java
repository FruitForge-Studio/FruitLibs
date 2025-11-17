package com.fruitforge.fruitLibs.core.item;

import com.fruitforge.fruitLibs.api.item.ItemBuilder;
import com.fruitforge.fruitLibs.util.ColorUtil;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SimpleItemBuilder implements ItemBuilder {

    private ItemStack item;
    private ItemMeta meta;

    public SimpleItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public SimpleItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    @Override
    public ItemBuilder type(Material material) {
        item.setType(material);
        meta = item.getItemMeta();
        return this;
    }

    @Override
    public ItemBuilder amount(int amount) {
        item.setAmount(Math.max(1, Math.min(64, amount)));
        return this;
    }

    @Override
    public ItemBuilder name(String name) {
        if (meta != null) {
            meta.setDisplayName(ColorUtil.colorize(name));
        }
        return this;
    }

    @Override
    public ItemBuilder lore(String... lines) {
        return lore(Arrays.asList(lines));
    }

    @Override
    public ItemBuilder lore(List<String> lines) {
        if (meta != null) {
            List<String> colorizedLore = lines.stream()
                    .map(ColorUtil::colorize)
                    .collect(Collectors.toList());
            meta.setLore(colorizedLore);
        }
        return this;
    }

    @Override
    public ItemBuilder addLoreLine(String line) {
        if (meta != null) {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add(ColorUtil.colorize(line));
            meta.setLore(lore);
        }
        return this;
    }

    @Override
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        if (meta != null) {
            meta.addEnchant(enchantment, level, true);
        }
        return this;
    }

    @Override
    public ItemBuilder enchant(Enchantment enchantment) {
        return enchant(enchantment, 1);
    }

    @Override
    public ItemBuilder removeEnchant(Enchantment enchantment) {
        if (meta != null) {
            meta.removeEnchant(enchantment);
        }
        return this;
    }

    @Override
    public ItemBuilder flags(ItemFlag... flags) {
        if (meta != null) {
            meta.addItemFlags(flags);
        }
        return this;
    }

    @Override
    public ItemBuilder removeFlags(ItemFlag... flags) {
        if (meta != null) {
            meta.removeItemFlags(flags);
        }
        return this;
    }

    @Override
    public ItemBuilder unbreakable(boolean unbreakable) {
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
        }
        return this;
    }

    @Override
    public ItemBuilder customModelData(int data) {
        if (meta != null) {
            meta.setCustomModelData(data);
        }
        return this;
    }

    @Override
    public ItemBuilder glow(boolean glow) {
        if (glow) {
            enchant(Enchantment.UNBREAKING, 1);
            flags(ItemFlag.HIDE_ENCHANTS);
        } else {
            removeEnchant(Enchantment.UNBREAKING);
            removeFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    @Override
    public ItemBuilder skull(String base64Texture) {
        if (item.getType() != Material.PLAYER_HEAD) {
            type(Material.PLAYER_HEAD);
        }

        NBT.modify(item, nbt -> {
            ReadWriteNBT skullOwner = nbt.getOrCreateCompound("SkullOwner");
            skullOwner.setUUID("Id", UUID.randomUUID());

            ReadWriteNBT properties = skullOwner.getOrCreateCompound("Properties");
            ReadWriteNBT textures = properties.getCompoundList("textures").addCompound();
            textures.setString("Value", base64Texture);
        });

        return this;
    }

    @Override
    public ItemBuilder skullOwner(String playerName) {
        if (item.getType() != Material.PLAYER_HEAD) {
            type(Material.PLAYER_HEAD);
        }

        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwner(playerName);
            this.meta = skullMeta;
        }

        return this;
    }

    @Override
    public ItemBuilder nbt(String key, Object value) {
        NBT.modify(item, nbt -> {
            if (value instanceof String) {
                nbt.setString(key, (String) value);
            } else if (value instanceof Integer) {
                nbt.setInteger(key, (Integer) value);
            } else if (value instanceof Double) {
                nbt.setDouble(key, (Double) value);
            } else if (value instanceof Boolean) {
                nbt.setBoolean(key, (Boolean) value);
            } else if (value instanceof Long) {
                nbt.setLong(key, (Long) value);
            } else {
                nbt.setString(key, value.toString());
            }
        });
        return this;
    }

    @Override
    public ItemBuilder nbt(Map<String, Object> nbtData) {
        nbtData.forEach(this::nbt);
        return this;
    }

    @Override
    public ItemBuilder durability(int durability) {
        if (item.getType().getMaxDurability() > 0) {
            NBT.modify(item, nbt -> {
                nbt.setInteger("Damage", durability);
            });
        }
        return this;
    }

    @Override
    public ItemBuilder modify(Consumer<ItemStack> modifier) {
        item.setItemMeta(meta);
        modifier.accept(item);
        this.meta = item.getItemMeta();
        return this;
    }

    @Override
    public ItemStack build() {
        item.setItemMeta(meta);
        return item.clone();
    }

    @Override
    public ItemBuilder clone() {
        return new SimpleItemBuilder(build());
    }
}