package com.songoda.epicanchors.gui;

import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.anchor.Anchor;
import com.songoda.epicanchors.utils.Methods;
import com.songoda.epicanchors.utils.gui.AbstractGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIOverview extends AbstractGUI {

    private final EpicAnchors plugin;
    private final Anchor anchor;

    private int task;

    public GUIOverview(EpicAnchors plugin, Anchor anchor, Player player) {
        super(player);
        this.plugin = plugin;
        this.anchor = anchor;


        init(Methods.formatText(plugin.getLocale().getMessage("interface.anchor.title").getMessage()), 27);
        runTask();
    }

    @Override
    public void constructGUI() {
        String timeRemaining = Methods.makeReadable((long) (anchor.getTicksLeft() / 20) * 1000) + " remaining.";

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

        ItemStack itemXP = new ItemStack(Material.valueOf(plugin.getConfig().getString("Interfaces.XP Icon")), 1);
        ItemMeta itemmetaXP = itemXP.getItemMeta();
        itemmetaXP.setDisplayName(plugin.getLocale().getMessage("interface.button.addtimewithxp").getMessage());
        ArrayList<String> loreXP = new ArrayList<>();
        loreXP.add(plugin.getLocale().getMessage("interface.button.addtimewithxplore")
                .processPlaceholder("cost", Integer.toString(plugin.getConfig().getInt("Main.XP Cost")))
        .getMessage());
        itemmetaXP.setLore(loreXP);
        itemXP.setItemMeta(itemmetaXP);

        ItemStack itemECO = new ItemStack(Material.valueOf(plugin.getConfig().getString("Interfaces.Economy Icon")), 1);
        ItemMeta itemmetaECO = itemECO.getItemMeta();
        itemmetaECO.setDisplayName(plugin.getLocale().getMessage("interface.button.addtimewitheconomy").getMessage());
        ArrayList<String> loreECO = new ArrayList<>();
        loreECO.add(plugin.getLocale().getMessage("interface.button.addtimewitheconomylore")
                .processPlaceholder("cost", Methods.formatEconomy(plugin.getConfig().getInt("Main.Economy Cost")))
                .getMessage());
        itemmetaECO.setLore(loreECO);
        itemECO.setItemMeta(itemmetaECO);

        ItemStack item = plugin.makAnchorItem(anchor.getTicksLeft());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Methods.formatText(plugin.getLocale().getMessage("interface.anchor.smalltitle").getMessage()));
        List<String> lore = new ArrayList<>();

        lore.add(Methods.formatText("&7" + timeRemaining));

        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.setItem(13, item);


        if (plugin.getConfig().getBoolean("Main.Add Time With Economy")) {
            inventory.setItem(11, itemECO);
        }

        if (plugin.getConfig().getBoolean("Main.Add Time With XP")) {
            inventory.setItem(15, itemXP);
        }
    }

    private void runTask() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::constructGUI, 5L, 5L);
    }

    @Override
    protected void registerClickables() {
        registerClickable(11, ((player, inventory, cursor, slot, type) -> {
            if (plugin.getConfig().getBoolean("Main.Add Time With Economy"))
                anchor.addTime("ECO", player);
        }));

        registerClickable(15, ((player, inventory, cursor, slot, type) -> {
            if (plugin.getConfig().getBoolean("Main.Add Time With XP"))
                anchor.addTime("XP", player);
        }));
    }

    @Override
    protected void registerOnCloses() {
        registerOnClose(((player1, inventory1) -> Bukkit.getScheduler().cancelTask(task)));
    }
}
