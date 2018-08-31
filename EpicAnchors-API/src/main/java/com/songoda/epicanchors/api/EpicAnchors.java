package com.songoda.epicanchors.api;

import com.songoda.epicanchors.api.anchor.AnchorManager;
import com.songoda.epicanchors.api.anchor.Level;
import com.songoda.epicanchors.api.anchor.LevelManager;
import org.bukkit.inventory.ItemStack;

public interface EpicAnchors {
    int getLevelFromItem(ItemStack item);

    ItemStack makeAnchorItem(Level level);

    LevelManager getLevelManager();

    AnchorManager getAnchorManager();
}
