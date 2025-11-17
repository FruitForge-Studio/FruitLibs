package com.fruitforge.fruitLibs.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public final class SkullTextureUtil {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String MOJANG_API = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private SkullTextureUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static CompletableFuture<String> getTextureFromUsername(String username) {
        return getUUIDFromUsername(username)
                .thenCompose(SkullTextureUtil::getTextureFromUUID);
    }

    public static CompletableFuture<String> getTextureFromUUID(String uuid) {
        String cleanUUID = uuid.replace("-", "");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MOJANG_API + cleanUUID))
                .GET()
                .build();

        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("Failed to fetch texture: HTTP " + response.statusCode());
                    }

                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    JsonObject properties = json.getAsJsonArray("properties").get(0).getAsJsonObject();
                    return properties.get("value").getAsString();
                });
    }

    public static CompletableFuture<String> getUUIDFromUsername(String username) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
                .GET()
                .build();

        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("Failed to fetch UUID: HTTP " + response.statusCode());
                    }

                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    return json.get("id").getAsString();
                });
    }

    public static String createTextureURL(String minecraftTextureHash) {
        String url = "http://textures.minecraft.net/texture/" + minecraftTextureHash;
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }
}