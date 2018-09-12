package com.songoda.epicanchors.api;

import com.songoda.epicanchors.api.anchor.AnchorManager;
import com.songoda.epicanchors.api.utils.ProtectionPluginHook;
import org.bukkit.inventory.ItemStack;

public interface EpicAnchors {

    void registerProtectionHook(ProtectionPluginHook hook);

    int getTicksFromItem(ItemStack item);

    ItemStack makeAnchorItem(int ticks);

    AnchorManager getAnchorManager();
}
