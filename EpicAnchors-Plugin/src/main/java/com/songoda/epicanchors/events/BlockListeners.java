package com.songoda.epicanchors.events;

import com.songoda.epicanchors.anchor.EAnchor;
import com.songoda.epicanchors.EpicAnchorsPlugin;
import com.songoda.epicanchors.api.anchor.Anchor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListeners implements Listener {

    private EpicAnchorsPlugin instance;

    public BlockListeners(EpicAnchorsPlugin instance) {
        this.instance = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.getBlock().getType() != Material.valueOf(instance.getConfig().getString("Main.Anchor Block Material"))) return;

        ItemStack item = event.getItemInHand();

        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        if (instance.getTicksFromItem(item) == 0) return;

        instance.getAnchorManager().addAnchor(event.getBlock().getLocation(), new EAnchor(event.getBlock().getLocation(), instance.getTicksFromItem(item)));

    }
}
