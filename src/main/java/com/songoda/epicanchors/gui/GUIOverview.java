package com.songoda.epicanchors.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.anchor.Anchor;
import com.songoda.epicanchors.settings.Settings;
import com.songoda.epicanchors.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        ItemStack glass1 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial());
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        setDefaultItem(glass1);

        GuiUtils.mirrorFill(this, 0, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 1, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 2, true, true, glass3);
        GuiUtils.mirrorFill(this, 1, 0, false, true, glass2);
        GuiUtils.mirrorFill(this, 1, 1, false, true, glass3);

        setItem(13, GuiUtils.createButtonItem(plugin.makeAnchorItem(anchor.getTicksLeft()),
                plugin.getLocale().getMessage("interface.anchor.smalltitle").getMessage(),
                ChatColor.GRAY + Methods.makeReadable((long) (anchor.getTicksLeft() / 20) * 1000) + " remaining."));

        if (EconomyManager.isEnabled() && plugin.getConfig().getBoolean("Main.Add Time With Economy")) {
            setButton(11, GuiUtils.createButtonItem(plugin.getConfig().getMaterial("Interfaces.Economy Icon", CompatibleMaterial.SUNFLOWER),
                    plugin.getLocale().getMessage("interface.button.addtimewitheconomy").getMessage(),
                    plugin.getLocale().getMessage("interface.button.addtimewitheconomylore")
                    .processPlaceholder("cost", Methods.formatEconomy(plugin.getConfig().getInt("Main.Economy Cost")))
                    .getMessage()),
                    (event) -> anchor.addTime("ECO", player));
        }

        if (plugin.getConfig().getBoolean("Main.Add Time With XP")) {
            setButton(15, GuiUtils.createButtonItem(plugin.getConfig().getMaterial("Interfaces.XP Icon", CompatibleMaterial.EXPERIENCE_BOTTLE),
                    plugin.getLocale().getMessage("interface.button.addtimewithxp").getMessage(),
                    plugin.getLocale().getMessage("interface.button.addtimewithxplore")
                        .processPlaceholder("cost", String.valueOf(plugin.getConfig().getInt("Main.XP Cost"))).getMessage()),
                    (event) -> anchor.addTime("XP", player));
        }
    }

    private void runTask() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            updateItem(11, plugin.getLocale().getMessage("interface.anchor.smalltitle").getMessage(),
                    ChatColor.GRAY + Methods.makeReadable((long) (anchor.getTicksLeft() / 20) * 1000) + " remaining.");
        }, 5L, 5L);
    }
}
