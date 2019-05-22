package com.songoda.epicanchors.anchor;

import com.songoda.epicanchors.EpicAnchorsPlugin;
import com.songoda.epicanchors.api.anchor.Anchor;
import com.songoda.epicanchors.gui.GUIOverview;
import com.songoda.epicanchors.utils.ServerVersion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EAnchor implements Anchor {

    private Location location;
    private int ticksLeft;

    public EAnchor(Location location, int ticksLeft) {
        this.location = location;
        this.ticksLeft = ticksLeft;
    }

    public void overview(Player player) {
        new GUIOverview(EpicAnchorsPlugin.getInstance(), this, player);
    }

    public void addTime(String type, Player player) {
        EpicAnchorsPlugin instance = EpicAnchorsPlugin.getInstance();

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
        Sound sound = EpicAnchorsPlugin.getInstance().isServerVersionAtLeast(ServerVersion.V1_8) ? Sound.ENTITY_PLAYER_LEVELUP : Sound.valueOf("LEVEL_UP");
        player.playSound(player.getLocation(), sound, 0.6F, 15.0F);

        if (EpicAnchorsPlugin.getInstance().isServerVersionAtLeast(ServerVersion.V1_8))
            player.getWorld().spawnParticle(Particle.SPELL_WITCH, getLocation().add(.5, .5, .5), 100, .5, .5, .5);
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public int getTicksLeft() {
        return ticksLeft;
    }

    @Override
    public void setTicksLeft(int ticksLeft) {
        this.ticksLeft = ticksLeft;
    }
}
