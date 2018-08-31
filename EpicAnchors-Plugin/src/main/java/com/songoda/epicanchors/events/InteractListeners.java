package com.songoda.epicanchors.events;

import com.songoda.epicanchors.anchor.EAnchor;
import com.songoda.epicanchors.anchor.ELevel;
import com.songoda.epicanchors.api.anchor.Anchor;
import com.songoda.epicanchors.api.anchor.Level;
import com.songoda.epicanchors.utils.Methods;
import com.songoda.epicanchors.EpicAnchorsPlugin;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListeners implements Listener {

    private EpicAnchorsPlugin instance;

    public InteractListeners(EpicAnchorsPlugin instance) {
        this.instance = instance;
    }


    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (instance.getAnchorManager().getAnchor(e.getClickedBlock().getLocation()) == null) return;

        Anchor anchor = instance.getAnchorManager().getAnchor(e.getClickedBlock().getLocation());

        Player player = e.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item.getType() == Material.valueOf(instance.getConfig().getString("Main.Anchor Block Material"))) {
            if (instance.getLevelFromItem(item) == 0) return;

            Level level = instance.getLevelManager().getLevel(instance.getLevelFromItem(item));

            anchor.setTicksLeft(anchor.getTicksLeft() + level.getTicks());

            if (player.getGameMode() != GameMode.CREATIVE)
                Methods.takeItem(player, 1);

            player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 2F, 25.0F);

            player.getWorld().playEffect(anchor.getLocation().add(.5,.5,.5), org.bukkit.Effect.valueOf("WITCH_MAGIC"), 1, 0);

            e.setCancelled(true);

            return;
        }

        ((EAnchor)anchor).overview(player);

    }

}
