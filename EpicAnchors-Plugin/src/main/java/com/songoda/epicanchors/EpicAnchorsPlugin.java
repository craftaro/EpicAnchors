package com.songoda.epicanchors;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.api.utils.ConfigWrapper;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicanchors.anchor.EAnchor;
import com.songoda.epicanchors.anchor.EAnchorManager;
import com.songoda.epicanchors.anchor.ELevel;
import com.songoda.epicanchors.anchor.ELevelManager;
import com.songoda.epicanchors.api.EpicAnchors;
import com.songoda.epicanchors.api.anchor.Anchor;
import com.songoda.epicanchors.api.anchor.AnchorManager;
import com.songoda.epicanchors.api.anchor.Level;
import com.songoda.epicanchors.api.anchor.LevelManager;
import com.songoda.epicanchors.command.CommandManager;
import com.songoda.epicanchors.events.BlockListeners;
import com.songoda.epicanchors.events.InteractListeners;
import com.songoda.epicanchors.events.InventoryListeners;
import com.songoda.epicanchors.handlers.AnchorHandler;
import com.songoda.epicanchors.handlers.MenuHandler;
import com.songoda.epicanchors.utils.Methods;
import com.songoda.epicanchors.utils.SettingsManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class EpicAnchorsPlugin extends JavaPlugin implements EpicAnchors {

    public ConfigWrapper dataFile = new ConfigWrapper(this, "", "data.yml");

    private static EpicAnchorsPlugin INSTANCE;

    private SettingsManager settingsManager;
    private EAnchorManager anchorManager;
    private ELevelManager levelManager;
    private MenuHandler menuHandler;

    public References references = null;

    private Locale locale;

    public static EpicAnchorsPlugin getInstance() {
        return INSTANCE;
    }

    private void checkVersion() {
        int workingVersion = 13;
        int currentVersion = Integer.parseInt(Bukkit.getServer().getClass()
                .getPackage().getName().split("\\.")[3].split("_")[1]);

        if (currentVersion < workingVersion) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                Bukkit.getConsoleSender().sendMessage("");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You installed the 1." + workingVersion + "+ only version of " + this.getDescription().getName() + " on a 1." + currentVersion + " server. Since you are on the wrong version we disabled the plugin for you. Please install correct version to continue using " + this.getDescription().getName() + ".");
                Bukkit.getConsoleSender().sendMessage("");
            }, 20L);
        }
    }

    @Override
    public void onEnable() {
        // Check to make sure the Bukkit version is compatible.
        checkVersion();

        INSTANCE = this;
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(TextComponent.formatText("&a============================="));
        console.sendMessage(TextComponent.formatText("&7EpicAnchors " + this.getDescription().getVersion() + " by &5Brianna <3&7!"));
        console.sendMessage(TextComponent.formatText("&7Action: &aEnabling&7..."));

        // Locales
        Locale.init(this);
        Locale.saveDefaultLocale("en_US");
        this.locale = Locale.getLocale(getConfig().getString("Locale", "en_US"));

        dataFile.createNewFile("Loading Data File", "EpicAnchors Data File");

        this.references = new References();
        this.menuHandler = new MenuHandler(this);
        this.anchorManager = new EAnchorManager();
        this.settingsManager = new SettingsManager(this);

        setupConfig();

        loadLevelManager();
        loadAnchorsFromFile();

        new AnchorHandler(this);

        // Command registration
        this.getCommand("EpicAnchors").setExecutor(new CommandManager(this));

        // Event registration
        getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
        getServer().getPluginManager().registerEvents(new InteractListeners(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);


        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::saveToFile, 6000, 6000);
        console.sendMessage(TextComponent.formatText("&a============================="));
    }

    public void onDisable() {
        saveToFile();
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(TextComponent.formatText("&a============================="));
        console.sendMessage(TextComponent.formatText("&7EpicAnchors " + this.getDescription().getVersion() + " by &5Brianna <3!"));
        console.sendMessage(TextComponent.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(TextComponent.formatText("&a============================="));
    }

    private void loadAnchorsFromFile() {
        if (dataFile.getConfig().contains("Anchors")) {
            for (String locationStr : dataFile.getConfig().getConfigurationSection("Anchors").getKeys(false)) {
                Location location = Arconix.pl().getApi().serialize().unserializeLocation(locationStr);
                int ticksLeft = dataFile.getConfig().getInt("Anchors." + locationStr + ".ticksLeft");

                EAnchor anchor = new EAnchor(location, ticksLeft);

                anchorManager.addAnchor(location, anchor);
            }
        }
    }

    private void loadLevelManager() {
        // Load an instance of LevelManager
        levelManager = new ELevelManager();

        /*
         * Register Levels into LevelManager from configuration.
         */
        levelManager.clear();
        for (String levelName : getConfig().getConfigurationSection("settings.levels").getKeys(false)) {
            int level = Integer.valueOf(levelName.split("-")[1]);
            int ticks = getConfig().getInt("settings.levels." + levelName + ".Ticks");
            levelManager.addLevel(level, ticks);
        }
    }

    private void saveToFile() {
        dataFile.getConfig().set("Anchors", null);
        for (Anchor anchor : anchorManager.getAnchors().values()) {
            String locationStr = Arconix.pl().getApi().serialize().serializeLocation(anchor.getLocation());
            dataFile.getConfig().set("Anchors." + locationStr + ".ticksLeft", anchor.getTicksLeft());
        }
        dataFile.saveConfig();
    }


    public void reload() {
        this.locale.reloadMessages();
        this.references = new References();
        this.loadAnchorsFromFile();
        this.reloadConfig();
        //this.saveConfig();
    }


    private void setupConfig() {
        settingsManager.updateSettings();

        getConfig().addDefault("settings.levels.Level-1.Ticks", 20 * 60 * 60); //1 Hours

        getConfig().addDefault("settings.levels.Level-2.Ticks", 20 * 60 * 60 * 3); //3 Hours

        getConfig().addDefault("settings.levels.Level-3.Ticks", 20 * 60 * 60 * 5); //5 Hours

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public int getLevelFromItem(ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return 0;
        if (item.getItemMeta().getDisplayName().contains(":")) {
            return NumberUtils.toInt(item.getItemMeta().getDisplayName().replace("\u00A7", "").split(":")[0], 0);
        }
        return 0;
    }

    @Override
    public ItemStack makeAnchorItem(Level level) {
        ItemStack item = new ItemStack(Material.valueOf(EpicAnchorsPlugin.getInstance().getConfig().getString("Main.Anchor Block Material")), 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Arconix.pl().getApi().format().formatText(Methods.formatName(level, true)));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public LevelManager getLevelManager() {
        return levelManager;
    }

    public MenuHandler getMenuHandler() {
        return menuHandler;
    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public AnchorManager getAnchorManager() {
        return anchorManager;
    }
}
