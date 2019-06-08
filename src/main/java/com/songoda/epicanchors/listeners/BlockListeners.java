package com.songoda.epicanchors.listeners;

import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.anchor.Anchor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListeners implements Listener {

    private EpicAnchors instance;

    public BlockListeners(EpicAnchors instance) {
        this.instance = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {

        ItemStack item = event.getItemInHand();

        if (!item.hasItemMeta()
                || !item.getItemMeta().hasDisplayName()
                || instance.getTicksFromItem(item) == 0) return;

        instance.getAnchorManager().addAnchor(event.getBlock().getLocation(), new Anchor(event.getBlock().getLocation(), instance.getTicksFromItem(item)));

    }

    @EventHandler
    public void onPortalCreation(EntityCreatePortalEvent e) {
        if (e.getBlocks().size() < 1) return;
        if (instance.getAnchorManager().isAnchor(e.getBlocks().get(0).getLocation())) e.setCancelled(true);
    }
}
