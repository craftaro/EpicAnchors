package com.songoda.epicanchors.utils;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicanchors.EpicAnchorsPlugin;
import com.songoda.epicanchors.anchor.ELevel;
import com.songoda.epicanchors.api.anchor.Level;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Methods {

    public static ItemStack getGlass() {
            EpicAnchorsPlugin plugin = EpicAnchorsPlugin.getInstance();
            return Arconix.pl().getApi().getGUI().getGlass(plugin.getConfig().getBoolean("settings.Rainbow-Glass"), plugin.getConfig().getInt("Interfaces.Glass Type 1"));

    }

    public static ItemStack getBackgroundGlass(boolean type) {
            EpicAnchorsPlugin plugin = EpicAnchorsPlugin.getInstance();
            if (type)
                return Arconix.pl().getApi().getGUI().getGlass(false, plugin.getConfig().getInt("Interfaces.Glass Type 2"));
            else
                return Arconix.pl().getApi().getGUI().getGlass(false, plugin.getConfig().getInt("Interfaces.Glass Type 3"));

    }


    public static void takeItem(Player p, int amt) {
            if (p.getGameMode() != GameMode.CREATIVE) {
                int result = p.getInventory().getItemInHand().getAmount() - amt;
                if (result > 0) {
                    ItemStack is = p.getItemInHand();
                    is.setAmount(is.getAmount() - amt);
                    p.setItemInHand(is);
                } else {
                    p.setItemInHand(null);
                }
            }
    }

    public static String formatName(Level level, boolean full) {
        int ticks = (((level.getTicks() / 20) / 60) / 60);
        String hours = "Hours";
        if (ticks == 1) hours = "Hour";

        String name = "&eAnchor &8(&7" + ticks + " " + hours + "&8)";

        String info = "";
        if (full) {
            info += Arconix.pl().getApi().format().convertToInvisibleString(level.getLevel() + ":");
        }

        return info + Arconix.pl().getApi().format().formatText(name);
    }

}
