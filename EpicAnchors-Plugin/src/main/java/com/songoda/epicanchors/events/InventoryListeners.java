package com.songoda.epicanchors.events;

import com.songoda.epicanchors.EpicAnchorsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListeners implements Listener {

    EpicAnchorsPlugin instance;

    public InventoryListeners(EpicAnchorsPlugin instance) {
        this.instance = instance;
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() == null || event.getRawSlot() >= event.getView().getTopInventory().getSize()) return;

        Player player = (Player)event.getWhoClicked();
        if (!instance.getMenuHandler().isPlayerInMenu(player)) return;

        event.setCancelled(true);
    }

    @EventHandler()
    public void onInventoryClose(InventoryCloseEvent event) {
        instance.getMenuHandler().removePlayer((Player)event.getPlayer());
    }

}
