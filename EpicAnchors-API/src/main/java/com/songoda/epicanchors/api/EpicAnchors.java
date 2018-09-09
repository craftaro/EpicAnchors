package com.songoda.epicanchors.api;

import com.songoda.epicanchors.api.anchor.AnchorManager;
import org.bukkit.inventory.ItemStack;

public interface EpicAnchors {

    int getTicksFromItem(ItemStack item);

    ItemStack makeAnchorItem(int ticks);

    AnchorManager getAnchorManager();
}
