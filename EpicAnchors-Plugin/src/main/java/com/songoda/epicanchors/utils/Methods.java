package com.songoda.epicanchors.utils;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicanchors.EpicAnchorsPlugin;
import com.songoda.epicanchors.api.EpicAnchors;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    public static String formatName(int ticks2, boolean full) {
        int hours = ((ticks2 / 20) / 60) / 60;
        int minutes = ((ticks2 / 20) / 60) - hours * 60;

        String remaining = minutes == 0 ? String.format("%sh", hours) : String.format("%sh %sm", hours, minutes);


        String name = EpicAnchorsPlugin.getInstance().getConfig().getString("Main.Name-Tag").replace("{REMAINING}", remaining);

        String info = "";
        if (full) {
            info += Arconix.pl().getApi().format().convertToInvisibleString(ticks2 + ":");
        }

        return info + Arconix.pl().getApi().format().formatText(name);
    }

}
