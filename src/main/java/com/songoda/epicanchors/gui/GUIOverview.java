package com.songoda.epicanchors.gui;

import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.anchor.Anchor;
import com.songoda.epicanchors.settings.Settings;
import com.songoda.epicanchors.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIOverview extends Gui {

    private final EpicAnchors plugin;
    private final Anchor anchor;
    private final Player player;

    private int task;

    public GUIOverview(EpicAnchors plugin, Anchor anchor, Player player) {
        this.plugin = plugin;
        this.anchor = anchor;
        this.player = player;

        this.setRows(3);
        this.setTitle(Methods.formatText(plugin.getLocale().getMessage("interface.anchor.title").getMessage()));

        runTask();
        constructGUI();
        this.setOnClose(action -> Bukkit.getScheduler().cancelTask(task));
    }

    private void constructGUI() {
        String timeRemaining = Methods.makeReadable((long) (anchor.getTicksLeft() / 20) * 1000) + " remaining.";

        ItemStack glass1 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial());
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        setDefaultItem(glass1);

        GuiUtils.mirrorFill(this, 0, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 1, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 2, true, true, glass3);
        GuiUtils.mirrorFill(this, 1, 0, false, true, glass2);
        GuiUtils.mirrorFill(this, 1, 1, false, true, glass3);

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

        ItemStack item = plugin.makeAnchorItem(anchor.getTicksLeft());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Methods.formatText(plugin.getLocale().getMessage("interface.anchor.smalltitle").getMessage()));
        List<String> lore = new ArrayList<>();

        lore.add(Methods.formatText("&7" + timeRemaining));

        meta.setLore(lore);
        item.setItemMeta(meta);
        setItem(13, item);

        if (EconomyManager.isEnabled() && plugin.getConfig().getBoolean("Main.Add Time With Economy"))
            setButton(11, itemECO, (event) -> anchor.addTime("ECO", player));

        if (plugin.getConfig().getBoolean("Main.Add Time With XP"))
            setButton(15, itemXP, (event) -> anchor.addTime("XP", player));
    }

    private void runTask() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::constructGUI, 5L, 5L);
    }
}
