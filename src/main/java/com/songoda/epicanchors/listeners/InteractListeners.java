package com.songoda.epicanchors.listeners;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleParticleHandler;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.utils.ItemUtils;
import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.anchor.Anchor;
import com.songoda.epicanchors.gui.GUIOverview;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListeners implements Listener {

    private final EpicAnchors instance;

    public InteractListeners(EpicAnchors instance) {
        this.instance = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        Anchor anchor = instance.getAnchorManager().getAnchor(event.getClickedBlock().getLocation());

        if (anchor == null) return;
        event.setCancelled(true);

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            anchor.bust();
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (instance.getConfig().getMaterial("Main.Anchor Block Material", CompatibleMaterial.AIR).matches(item)) {
            if (instance.getTicksFromItem(item) == 0) return;

            anchor.setTicksLeft(anchor.getTicksLeft() + instance.getTicksFromItem(item));

            if (player.getGameMode() != GameMode.CREATIVE)
                ItemUtils.takeActiveItem(player);

            player.playSound(player.getLocation(), CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 0.6F, 15.0F);

            CompatibleParticleHandler.spawnParticles(CompatibleParticleHandler.ParticleType.SPELL_WITCH, anchor.getLocation().add(.5, .5, .5), 100, .5, .5, .5);

        } else {
            instance.getGuiManager().showGUI(player, new GUIOverview(EpicAnchors.getInstance(), anchor, player));
        }
    }

}
