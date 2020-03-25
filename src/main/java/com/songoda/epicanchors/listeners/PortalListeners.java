package com.songoda.epicanchors.listeners;

import com.songoda.epicanchors.EpicAnchors;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

public class PortalListeners implements Listener {

    private EpicAnchors plugin;

    public PortalListeners(EpicAnchors instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onPortalCreation(PortalCreateEvent e) {
        if (e.getBlocks().size() < 1) return;
        if (plugin.getAnchorManager().isAnchor(e.getBlocks().get(0).getLocation())) e.setCancelled(true);
    }
}
