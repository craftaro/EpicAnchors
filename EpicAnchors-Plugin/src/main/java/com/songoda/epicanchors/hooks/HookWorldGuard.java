package com.songoda.epicanchors.hooks;

import com.songoda.epicanchors.api.utils.ProtectionPluginHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public class HookWorldGuard implements ProtectionPluginHook {

    private Method wg6canBuild;
    private Object wg6inst;
    private boolean isWorldGuard7;

    public HookWorldGuard() {
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard");
            this.isWorldGuard7 = true;
        } catch (ClassNotFoundException ex) {
            this.isWorldGuard7 = false;
            try {
                this.wg6inst = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin").getMethod("inst").invoke(null);
                this.wg6canBuild = this.wg6inst.getClass().getMethod("canBuild", Player.class, Location.class);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public JavaPlugin getPlugin() {
        try {
            if (this.isWorldGuard7)
                return com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst();
            return (JavaPlugin) Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin").getMethod("inst").invoke(null);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        if (this.isWorldGuard7) {
            com.sk89q.worldguard.protection.regions.RegionQuery q = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            com.sk89q.worldguard.protection.ApplicableRegionSet ars = q.getApplicableRegions(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(player.getLocation()));
            return ars.testState(com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapPlayer(player), com.sk89q.worldguard.protection.flags.Flags.BUILD);
        } else {
            try {
                return (boolean) this.wg6canBuild.invoke(this.wg6inst, player, location);
            } catch (Exception ex) {
                return true;
            }
        }
    }

}