package com.songoda.epicanchors.anchor;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.epicanchors.EpicAnchors;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Anchor {

    private Location location;
    private int ticksLeft;
    private boolean isInfinite;

    private final int chunkX;
    private final int chunkZ;

    public Anchor(Location location, int ticksLeft) {
        this.location = location;
        this.chunkX = location.getBlockX() >> 4;
        this.chunkZ = location.getBlockZ() >> 4;
        this.ticksLeft = ticksLeft;
        this.isInfinite = (ticksLeft == -99);
    }

    public void addTime(String type, Player player) {
        EpicAnchors instance = EpicAnchors.getInstance();

        if (type.equals("ECO")) {
            if (!EconomyManager.isEnabled()) return;
            double cost = instance.getConfig().getDouble("Main.Economy Cost");
            if (EconomyManager.hasBalance(player, cost)) {
                EconomyManager.withdrawBalance(player, cost);
            } else {
                instance.getLocale().getMessage("event.upgrade.cannotafford").sendPrefixedMessage(player);
                return;
            }
        } else if (type.equals("XP")) {
            int cost = instance.getConfig().getInt("Main.XP Cost");
            if (player.getLevel() >= cost || player.getGameMode() == GameMode.CREATIVE) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    player.setLevel(player.getLevel() - cost);
                }
            } else {
                instance.getLocale().getMessage("event.upgrade.cannotafford").sendPrefixedMessage(player);
                return;
            }
        }

        ticksLeft = ticksLeft + 20 * 60 * 30;
        Sound sound = ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9) ? Sound.ENTITY_PLAYER_LEVELUP : Sound.valueOf("LEVEL_UP");
        player.playSound(player.getLocation(), sound, 0.6F, 15.0F);

        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9))
            player.getWorld().spawnParticle(Particle.SPELL_WITCH, getLocation().add(.5, .5, .5), 100, .5, .5, .5);
    }

    public void bust() {
        EpicAnchors plugin = EpicAnchors.getInstance();

        if (plugin.getConfig().getBoolean("Main.Allow Anchor Breaking")) {
            ItemStack item = plugin.makeAnchorItem(getTicksLeft());
            getLocation().getWorld().dropItemNaturally(getLocation(), item);
        }
        plugin.clearHologram(this);
        location.getBlock().setType(Material.AIR);

        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9))
            location.getWorld().spawnParticle(Particle.LAVA, location.clone().add(.5, .5, .5), 5, 0, 0, 0, 5);

        location.getWorld().playSound(location, ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)
                ? Sound.ENTITY_GENERIC_EXPLODE : Sound.valueOf("EXPLODE"), 10, 10);

        plugin.getAnchorManager().removeAnchor(location);
    }

    public Location getLocation() {
        return location.clone();
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public World getWorld() {
        return location.getWorld();
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public void setTicksLeft(int ticksLeft) {
        this.ticksLeft = ticksLeft;
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public void setInfinite(boolean infinite) {
        isInfinite = infinite;
    }
}
