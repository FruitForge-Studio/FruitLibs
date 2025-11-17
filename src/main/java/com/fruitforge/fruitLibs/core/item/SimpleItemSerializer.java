package com.fruitforge.fruitLibs.core.item;

import com.fruitforge.fruitLibs.api.item.ItemSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SimpleItemSerializer implements ItemSerializer {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();

    @Override
    public Map<String, Object> serialize(ItemStack item) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", item.getType().name());
        data.put("amount", item.getAmount());
        data.put("base64", toBase64(item));
        return data;
    }

    @Override
    public ItemStack deserialize(Map<String, Object> data) {
        String base64 = (String) data.get("base64");
        if (base64 != null) {
            return fromBase64(base64);
        }
        return null;
    }

    @Override
    public String toBase64(ItemStack item) {
        if (item == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeObject(item);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize ItemStack", e);
        }
    }

    @Override
    public ItemStack fromBase64(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return null;
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            return (ItemStack) dataInput.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize ItemStack", e);
        }
    }

    @Override
    public String toJson(ItemStack item) {
        return GSON.toJson(serialize(item));
    }

    @Override
    public ItemStack fromJson(String json) {
        Map<String, Object> data = GSON.fromJson(json, MAP_TYPE);
        return deserialize(data);
    }
}