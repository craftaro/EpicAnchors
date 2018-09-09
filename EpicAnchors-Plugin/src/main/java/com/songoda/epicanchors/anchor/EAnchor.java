package com.songoda.epicanchors.anchor;


import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.api.methods.formatting.TimeComponent;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicanchors.EpicAnchorsPlugin;
import com.songoda.epicanchors.api.anchor.Anchor;
import com.songoda.epicanchors.utils.Methods;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;

//ToDo: I want there to be a GUI for this that has the timer going down in real time.
public class EAnchor implements Anchor {

    private Location location;
    private int ticksLeft;

    public EAnchor(Location location, int ticksLeft) {
        this.location = location;
        this.ticksLeft = ticksLeft;
    }

    public void overview(Player player) {
        EpicAnchorsPlugin instance = EpicAnchorsPlugin.getInstance();

        String timeRemaining = TimeComponent.makeReadable((long) (ticksLeft / 20) * 1000) + " remaining.";

        Inventory inventory = Bukkit.createInventory(null, 27, TextComponent.formatText(instance.getLocale().getMessage("interface.anchor.title")));

        int nu = 0;
        while (nu != 27) {
            inventory.setItem(nu, Methods.getGlass());
            nu++;
        }
        inventory.setItem(0, Methods.getBackgroundGlass(true));
        inventory.setItem(1, Methods.getBackgroundGlass(true));
        inventory.setItem(2, Methods.getBackgroundGlass(false));
        inventory.setItem(6, Methods.getBackgroundGlass(false));
        inventory.setItem(7, Methods.getBackgroundGlass(true));
        inventory.setItem(8, Methods.getBackgroundGlass(true));
        inventory.setItem(9, Methods.getBackgroundGlass(true));
        inventory.setItem(10, Methods.getBackgroundGlass(false));
        inventory.setItem(16, Methods.getBackgroundGlass(false));
        inventory.setItem(17, Methods.getBackgroundGlass(true));
        inventory.setItem(18, Methods.getBackgroundGlass(true));
        inventory.setItem(19, Methods.getBackgroundGlass(true));
        inventory.setItem(20, Methods.getBackgroundGlass(false));
        inventory.setItem(24, Methods.getBackgroundGlass(false));
        inventory.setItem(25, Methods.getBackgroundGlass(true));
        inventory.setItem(26, Methods.getBackgroundGlass(true));

        ItemStack itemXP = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.XP Icon")), 1);
        ItemMeta itemmetaXP = itemXP.getItemMeta();
        itemmetaXP.setDisplayName(instance.getLocale().getMessage("interface.button.addtimewithxp"));
        ArrayList<String> loreXP = new ArrayList<>();
        loreXP.add(instance.getLocale().getMessage("interface.button.addtimewithxplore", Integer.toString(instance.getConfig().getInt("Main.XP Cost"))));
        itemmetaXP.setLore(loreXP);
        itemXP.setItemMeta(itemmetaXP);

        ItemStack itemECO = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.Economy Icon")), 1);
        ItemMeta itemmetaECO = itemECO.getItemMeta();
        itemmetaECO.setDisplayName(instance.getLocale().getMessage("interface.button.addtimewitheconomy"));
        ArrayList<String> loreECO = new ArrayList<>();
        loreECO.add(instance.getLocale().getMessage("interface.button.addtimewitheconomylore", Arconix.pl().getApi().format().formatEconomy(instance.getConfig().getInt("Main.Economy Cost"))));
        itemmetaECO.setLore(loreECO);
        itemECO.setItemMeta(itemmetaECO);

        ItemStack item = instance.makeAnchorItem(ticksLeft);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(TextComponent.formatText(instance.getLocale().getMessage("interface.anchor.smalltitle")));
        List<String> lore = new ArrayList<>();

        lore.add(TextComponent.formatText("&7" + timeRemaining));

        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.setItem(13, item);


        if (instance.getConfig().getBoolean("Main.Add Time With Economy")) {
            inventory.setItem(11, itemXP);
        }

        if (instance.getConfig().getBoolean("Main.Add Time With XP")) {
            inventory.setItem(15, itemECO);
        }

        player.openInventory(inventory);
        EpicAnchorsPlugin.getInstance().getMenuHandler().addPlayer(player, location);
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
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.6F, 15.0F);
        player.getWorld().spawnParticle(Particle.SPELL_WITCH, getLocation().add(.5,.5,.5), 100, .5, .5, .5);
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
