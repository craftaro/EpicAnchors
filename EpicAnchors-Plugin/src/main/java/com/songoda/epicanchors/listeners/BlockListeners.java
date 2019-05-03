package com.songoda.epicanchors.listeners;

import com.songoda.epicanchors.anchor.EAnchor;
import com.songoda.epicanchors.EpicAnchorsPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListeners implements Listener {

    private EpicAnchorsPlugin instance;

    public BlockListeners(EpicAnchorsPlugin instance) {
        this.instance = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!instance.canBuild(player, block.getLocation())
                || event.getBlock().getType() != Material.valueOf(instance.getConfig().getString("Main.Anchor Block Material"))) return;

        ItemStack item = event.getItemInHand();

        if (!item.hasItemMeta()
                || !item.getItemMeta().hasDisplayName()
                || instance.getTicksFromItem(item) == 0) return;

        instance.getAnchorManager().addAnchor(event.getBlock().getLocation(), new EAnchor(event.getBlock().getLocation(), instance.getTicksFromItem(item)));

    }

    @EventHandler
    public void onPortalCreation(EntityCreatePortalEvent e) {
        if (e.getBlocks().size() < 1) return;
        if (instance.getAnchorManager().isAnchor(e.getBlocks().get(0).getLocation())) e.setCancelled(true);
    }
}
