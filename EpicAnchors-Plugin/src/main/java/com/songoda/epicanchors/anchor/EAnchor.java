package com.songoda.epicanchors.anchor;


import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.api.methods.formatting.TimeComponent;
import com.songoda.epicanchors.api.anchor.Anchor;
import com.songoda.epicanchors.api.anchor.Level;
import com.songoda.epicanchors.utils.Methods;
import com.songoda.epicanchors.EpicAnchorsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

//ToDo: I want there to be a GUI for this that has the timer going down in real time.
public class EAnchor implements Anchor {

    private Location location;
    private int ticksLeft;

    public EAnchor(Location location, Level level) {
        this(location, level.getTicks());
    }

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

        ItemStack item = instance.makeAnchorItem(EpicAnchorsPlugin.getInstance().getLevelManager().getLowestLevel());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(TextComponent.formatText(instance.getLocale().getMessage("interface.anchor.smalltitle")));
        List<String> lore = new ArrayList<>();

        lore.add(TextComponent.formatText("&7" + timeRemaining));

        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.setItem(13, item);

        player.openInventory(inventory);
        EpicAnchorsPlugin.getInstance().getMenuHandler().addPlayer(player, location);
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
