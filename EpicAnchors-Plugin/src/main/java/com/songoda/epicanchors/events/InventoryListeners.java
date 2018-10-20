package com.songoda.epicanchors.events;

import com.songoda.epicanchors.EpicAnchorsPlugin;
import com.songoda.epicanchors.anchor.EAnchor;
import com.songoda.epicanchors.api.anchor.Anchor;
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

        Player player = (Player) event.getWhoClicked();
        if (!instance.getMenuHandler().isPlayerInMenu(player)) return;

        Anchor anchor = instance.getMenuHandler().getAnchor(player);

        event.setCancelled(true);

        if (event.getSlot() == 15 && instance.getConfig().getBoolean("Main.Add Time With XP")) {
            if (!event.getCurrentItem().getItemMeta().getDisplayName().equals("§l")) {
                ((EAnchor) anchor).addTime("XP", player);
            }
        } else if (event.getSlot() == 11 && instance.getConfig().getBoolean("Main.Add Time With Economy")) {
            if (!event.getCurrentItem().getItemMeta().getDisplayName().equals("§l")) {
                ((EAnchor) anchor).addTime("ECO", player);
            }
        }
    }

    @EventHandler()
    public void onInventoryClose(InventoryCloseEvent event) {
        instance.getMenuHandler().removePlayer((Player) event.getPlayer());
    }

}
