package com.songoda.epicanchors.listeners;

import com.songoda.core.compatibility.CompatibleSounds;
import com.songoda.core.compatibility.LegacyMaterials;
import com.songoda.core.compatibility.ParticleHandler;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.utils.ItemUtils;
import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.anchor.Anchor;
import com.songoda.epicanchors.gui.GUIOverview;
import com.songoda.epicanchors.utils.Methods;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            anchor.bust();
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (instance.getConfig().getMaterial("Main.Anchor Block Material").matches(item)) {
            event.setCancelled(true);
            if (instance.getTicksFromItem(item) == 0) return;

            anchor.setTicksLeft(anchor.getTicksLeft() + instance.getTicksFromItem(item));

            if (player.getGameMode() != GameMode.CREATIVE)
                ItemUtils.takeActiveItem(player);

            player.playSound(player.getLocation(), CompatibleSounds.ENTITY_PLAYER_LEVELUP.getSound(), 0.6F, 15.0F);

            ParticleHandler.spawnParticles(ParticleHandler.ParticleType.SPELL_WITCH, anchor.getLocation().add(.5, .5, .5), 100, .5, .5, .5);

            return;
        }

        instance.getGuiManager().showGUI(player, new GUIOverview(EpicAnchors.getInstance(), anchor, player));

    }

}
