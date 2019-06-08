package com.songoda.epicanchors.utils;

import com.songoda.epicanchors.EpicAnchors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by songo on 6/4/2017.
 */
public class SettingsManager implements Listener {

    private static final Pattern SETTINGS_PATTERN = Pattern.compile("(.{1,28}(?:\\s|$))|(.{0,28})", Pattern.DOTALL);
    private final EpicAnchors instance;
    private String pluginName = "EpicAnchors";
    private Map<Player, String> cat = new HashMap<>();
    private Map<Player, String> current = new HashMap<>();

    public SettingsManager(EpicAnchors plugin) {
        this.instance = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();

        if (event.getInventory() != event.getWhoClicked().getOpenInventory().getTopInventory()
                || clickedItem == null || !clickedItem.hasItemMeta()
                || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        if (event.getView().getTitle().equals(pluginName + " Settings Manager")) {
            event.setCancelled(true);
            if (clickedItem.getType().name().contains("STAINED_GLASS")) return;

            String type = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            this.cat.put((Player) event.getWhoClicked(), type);
            this.openEditor((Player) event.getWhoClicked());
        } else if (event.getView().getTitle().equals(pluginName + " Settings Editor")) {
            event.setCancelled(true);
            if (clickedItem.getType().name().contains("STAINED_GLASS")) return;

            Player player = (Player) event.getWhoClicked();

            String key = cat.get(player) + "." + ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            if (instance.getConfig().get(key).getClass().getName().equals("java.lang.Boolean")) {
                this.instance.getConfig().set(key, !instance.getConfig().getBoolean(key));
                this.finishEditing(player);
            } else {
                this.editObject(player, key);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!current.containsKey(player)) return;

        String value = current.get(player);
        FileConfiguration config = instance.getConfig();
        if (config.isInt(value)) {
            config.set(value, Integer.parseInt(event.getMessage()));
        } else if (config.isDouble(value)) {
            config.set(value, Double.parseDouble(event.getMessage()));
        } else if (config.isString(value)) {
            config.set(value, event.getMessage());
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(EpicAnchors.getInstance(), () ->
                this.finishEditing(player), 0L);

        event.setCancelled(true);
    }

    private void finishEditing(Player player) {
        this.current.remove(player);
        this.instance.saveConfig();
        this.openEditor(player);
    }

    private void editObject(Player player, String current) {
        this.current.put(player, ChatColor.stripColor(current));

        player.closeInventory();
        player.sendMessage("");
        player.sendMessage(Methods.formatText("&7Please enter a value for &6" + current + "&7."));
        if (instance.getConfig().isInt(current) || instance.getConfig().isDouble(current)) {
            player.sendMessage(Methods.formatText("&cUse only numbers."));
        }
        player.sendMessage("");
    }

    public void openSettingsManager(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, pluginName + " Settings Manager");
        ItemStack glass = Methods.getGlass();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }

        int slot = 10;
        for (String key : instance.getConfig().getDefaultSection().getKeys(false)) {
            ItemStack item = new ItemStack(instance.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.WHITE_WOOL : Material.valueOf("WOOL"), 1, (byte) (slot - 9)); //ToDo: Make this function as it was meant to.
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Collections.singletonList(Methods.formatText("&6Click To Edit This Category.")));
            meta.setDisplayName(Methods.formatText("&f&l" + key));
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
            slot++;
        }

        player.openInventory(inventory);
    }

    private void openEditor(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, pluginName + " Settings Editor");
        FileConfiguration config = instance.getConfig();

        int slot = 0;
        for (String key : config.getConfigurationSection(cat.get(player)).getKeys(true)) {
            String fKey = cat.get(player) + "." + key;
            ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Methods.formatText("&6" + key));

            List<String> lore = new ArrayList<>();
            if (config.isBoolean(fKey)) {
                item.setType(Material.LEVER);
                lore.add(Methods.formatText(config.getBoolean(fKey) ? "&atrue" : "&cfalse"));
            } else if (config.isString(fKey)) {
                item.setType(Material.PAPER);
                lore.add(Methods.formatText("&9" + config.getString(fKey)));
            } else if (config.isInt(fKey)) {
                item.setType(instance.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.CLOCK : Material.valueOf("WATCH"));
                lore.add(Methods.formatText("&5" + config.getInt(fKey)));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);

            inventory.setItem(slot, item);
            slot++;
        }

        player.openInventory(inventory);
    }

    public void updateSettings() {
        FileConfiguration config = instance.getConfig();

        for (Setting setting : Setting.values()) {
            config.addDefault(setting.setting, setting.option);
        }
    }

    public enum Setting {
        o1("Main.Name-Tag", "&Anchor &8(&7{REMAINING}&8)"),
        o2("Main.Anchor-Lore", "&7Place down to keep that chunk|&7loaded until the time runs out."),
        o3("Main.Anchor Block Material", EpicAnchors.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ? "END_PORTAL_FRAME" : "ENDER_PORTAL_FRAME"),
        o4("Main.Add Time With Economy", true),
        o5("Main.Economy Cost", 5000.0),
        o6("Main.Add Time With XP", true),
        o7("Main.XP Cost", 10),
        o8("Main.Allow Anchor Breaking", false),
        o9("Interfaces.Economy Icon", EpicAnchors.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ? "SUNFLOWER" : "GOLD_INGOT"),
        o10("Interfaces.XP Icon", EpicAnchors.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ? "EXPERIENCE_BOTTLE" : "EXP_BOTTLE"),
        o11("Interfaces.Glass Type 1", 7),
        o12("Interfaces.Glass Type 2", 11),
        o13("Interfaces.Glass Type 3", 3),

        LANGUGE_MODE("System.Language Mode", "en_US");

        private String setting;
        private Object option;

        Setting(String setting, Object option) {
            this.setting = setting;
            this.option = option;
        }

        public List<String> getStringList() {
            return EpicAnchors.getInstance().getConfig().getStringList(setting);
        }

        public boolean getBoolean() {
            return EpicAnchors.getInstance().getConfig().getBoolean(setting);
        }

        public int getInt() {
            return EpicAnchors.getInstance().getConfig().getInt(setting);
        }

        public String getString() {
            return EpicAnchors.getInstance().getConfig().getString(setting);
        }

        public char getChar() {
            return EpicAnchors.getInstance().getConfig().getString(setting).charAt(0);
        }


    }
}