package com.fruitforge.fruitLibs.core.event.events;

import com.fruitforge.fruitLibs.api.event.Event;
import org.bukkit.entity.Player;

public class PlayerCoinsChangeEvent extends Event {

    private final Player player;
    private final int oldCoins;
    private int newCoins;

    public PlayerCoinsChangeEvent(Player player, int oldCoins, int newCoins) {
        this.player = player;
        this.oldCoins = oldCoins;
        this.newCoins = newCoins;
    }

    public Player getPlayer() {
        return player;
    }

    public int getOldCoins() {
        return oldCoins;
    }

    public int getNewCoins() {
        return newCoins;
    }

    public void setNewCoins(int newCoins) {
        this.newCoins = newCoins;
    }

    public int getDifference() {
        return newCoins - oldCoins;
    }
}