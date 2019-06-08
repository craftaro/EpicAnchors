package com.songoda.epicanchors.anchor;

import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.gui.GUIOverview;
import com.songoda.epicanchors.utils.ServerVersion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Anchor {

    private Location location;
    private int ticksLeft;

    public Anchor(Location location, int ticksLeft) {
        this.location = location;
        this.ticksLeft = ticksLeft;
    }

    public void overview(Player player) {
        new GUIOverview(EpicAnchors.getInstance(), this, player);
    }

    public void addTime(String type, Player player) {
        EpicAnchors instance = EpicAnchors.getInstance();

        if (type.equals("ECO")) {
            if (instance.getServer().getPluginManager().getPlugin("Vault") != null) {
                RegisteredServiceProvider<Economy> rsp = instance.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                net.milkbowl.vault.economy.Economy econ = rsp.getProvider();
                double cost = instance.getConfig().getDouble("Main.Economy Cost");
                if (econ.has(player, cost)) {
                    econ.withdrawPlayer(player, cost);
                } else {
                    player.sendMessage(instance.getLocale().getMessage("event.upgrade.cannotafford"));
                    return;
                }
            } else {
                player.sendMessage("Vault is not installed.");
                return;
            }
        } else if (type.equals("XP")) {
            int cost = instance.getConfig().getInt("Main.XP Cost");
            if (player.getLevel() >= cost || player.getGameMode() == GameMode.CREATIVE) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    player.setLevel(player.getLevel() - cost);
                }
            } else {
                player.sendMessage(instance.getLocale().getMessage("event.upgrade.cannotafford"));
                return;
            }
        }

        ticksLeft = ticksLeft + 20 * 60 * 30;
        Sound sound = EpicAnchors.getInstance().isServerVersionAtLeast(ServerVersion.V1_9) ? Sound.ENTITY_PLAYER_LEVELUP : Sound.valueOf("LEVEL_UP");
        player.playSound(player.getLocation(), sound, 0.6F, 15.0F);

        if (EpicAnchors.getInstance().isServerVersionAtLeast(ServerVersion.V1_9))
            player.getWorld().spawnParticle(Particle.SPELL_WITCH, getLocation().add(.5, .5, .5), 100, .5, .5, .5);
    }

    public void bust() {
        EpicAnchors plugin = EpicAnchors.getInstance();

        if (plugin.getConfig().getBoolean("Main.Allow Anchor Breaking")) {
            ItemStack item = plugin.makAnchorItem(getTicksLeft());
            getLocation().getWorld().dropItemNaturally(getLocation(), item);
        }
        location.getBlock().setType(Material.AIR);

        if (plugin.isServerVersionAtLeast(ServerVersion.V1_9))
            location.getWorld().spawnParticle(Particle.LAVA, location.clone().add(.5, .5, .5), 5, 0, 0, 0, 5);

        location.getWorld().playSound(location, plugin.isServerVersionAtLeast(ServerVersion.V1_9)
                ? Sound.ENTITY_GENERIC_EXPLODE : Sound.valueOf("EXPLODE"), 10, 10);

        if (plugin.getHologram() != null)
            plugin.getHologram().remove(this);
        plugin.getAnchorManager().removeAnchor(location);
    }

    public Location getLocation() {
        return location.clone();
    }


    public int getX() {
        return location.getBlockX();
    }


    public int getY() {
        return location.getBlockY();
    }


    public int getZ() {
        return location.getBlockZ();
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
}
