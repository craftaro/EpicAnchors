package com.songoda.epicanchors.handlers;

import com.songoda.epicanchors.EpicAnchorsPlugin;
import com.songoda.epicanchors.anchor.EAnchor;
import com.songoda.epicanchors.api.anchor.Anchor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuHandler {

    private Map<UUID, Location> playersInMenu = new HashMap<>();

    private EpicAnchorsPlugin instance;

    public MenuHandler(EpicAnchorsPlugin instance) {
        this.instance = instance;
    }

    public void updateMenu() {
        if (playersInMenu.size() == 0) return;
        for (Map.Entry<UUID, Location> entry : playersInMenu.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            ((EAnchor)instance.getAnchorManager().getAnchor(entry.getValue())).overview(player);
        }
    }

    public Anchor getAnchor(Player player) {
        return instance.getAnchorManager().getAnchor(playersInMenu.get(player.getUniqueId()));
    }

    public void addPlayer(Player player, Location location) {
        playersInMenu.put(player.getUniqueId(), location);
    }

    public void removePlayer(Player player) {
        playersInMenu.remove(player.getUniqueId());
    }

    public boolean isPlayerInMenu(Player player) {
        return playersInMenu.containsKey(player.getUniqueId());
    }

    public void removeAnchor(Location location) {
        for (Map.Entry<UUID, Location> entry : playersInMenu.entrySet()) {
            if (entry.getValue() != location) return;
            Player player = Bukkit.getPlayer(entry.getKey());
            playersInMenu.remove(player.getUniqueId());
            player.closeInventory();
        }
    }
}
