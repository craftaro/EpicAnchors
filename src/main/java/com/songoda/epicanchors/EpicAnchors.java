package com.songoda.epicanchors;

import com.songoda.epicanchors.anchor.Anchor;
import com.songoda.epicanchors.anchor.AnchorManager;
import com.songoda.epicanchors.command.CommandManager;
import com.songoda.epicanchors.hologram.Hologram;
import com.songoda.epicanchors.hologram.HologramHolographicDisplays;
import com.songoda.epicanchors.tasks.AnchorTask;
import com.songoda.epicanchors.listeners.BlockListeners;
import com.songoda.epicanchors.listeners.InteractListeners;
import com.songoda.epicanchors.utils.*;
import com.songoda.epicanchors.utils.settings.Setting;
import com.songoda.epicanchors.utils.settings.SettingsManager;
import com.songoda.epicanchors.utils.updateModules.LocaleModule;
import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class EpicAnchors extends JavaPlugin {

    public ConfigWrapper dataFile = new ConfigWrapper(this, "", "data.yml");

    private ServerVersion serverVersion = ServerVersion.fromPackageName(Bukkit.getServer().getClass().getPackage().getName());

    private Hologram hologram;

    private static EpicAnchors INSTANCE;

    private SettingsManager settingsManager;
    private AnchorManager anchorManager;

    private CommandManager commandManager;

    private References references;

    private Locale locale;

    public static EpicAnchors getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7EpicAnchors " + this.getDescription().getVersion() + " by &5Brianna <3&7!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));

        this.settingsManager = new SettingsManager(this);
        this.settingsManager.setupConfig();

        // Locales
        String langMode = Setting.LANGUGE_MODE.getString();
        Locale.init(this);
        Locale.saveDefaultLocale("en_US");
        this.locale = Locale.getLocale(langMode);

        //Running Songoda Updater
        Plugin plugin = new Plugin(this, 31);
        plugin.addModule(new LocaleModule());
        SongodaUpdate.load(plugin);

        dataFile.createNewFile("Loading Data File", "EpicAnchors Data File");

        this.references = new References();
        this.anchorManager = new AnchorManager();
        this.commandManager = new CommandManager(this);

        loadAnchorsFromFile();

        // Start tasks
        new AnchorTask(this);

        // Command registration
        this.getCommand("EpicAnchors").setExecutor(new CommandManager(this));

        PluginManager pluginManager = Bukkit.getPluginManager();

        // Event registration
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this), this);

        // Register Hologram Plugin
        if (Setting.HOLOGRAMS.getBoolean()
                && pluginManager.isPluginEnabled("HolographicDisplays"))
            hologram = new HologramHolographicDisplays(this);

        if (hologram != null)
            hologram.loadHolograms();

        // Start Metrics
        new Metrics(this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::saveToFile, 6000, 6000);
        console.sendMessage(Methods.formatText("&a============================="));
    }

    public void onDisable() {
        this.saveToFile();
        if (hologram != null)
            this.hologram.unloadHolograms();
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7EpicAnchors " + this.getDescription().getVersion() + " by &5Brianna <3!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Methods.formatText("&a============================="));
    }

    private void loadAnchorsFromFile() {
        if (dataFile.getConfig().contains("Anchors")) {
            for (String locationStr : dataFile.getConfig().getConfigurationSection("Anchors").getKeys(false)) {
                Location location = Methods.unserializeLocation(locationStr);
                int ticksLeft = dataFile.getConfig().getInt("Anchors." + locationStr + ".ticksLeft");

                Anchor anchor = new Anchor(location, ticksLeft);

                anchorManager.addAnchor(location, anchor);
            }
        }
    }

    private void saveToFile() {
        dataFile.getConfig().set("Anchors", null);
        for (Anchor anchor : anchorManager.getAnchors().values()) {
            String locationStr = Methods.serializeLocation(anchor.getLocation());
            dataFile.getConfig().set("Anchors." + locationStr + ".ticksLeft", anchor.getTicksLeft());
        }
        dataFile.saveConfig();
    }


    public void reload() {
        this.locale.reloadMessages();
        this.references = new References();
        this.loadAnchorsFromFile();
        this.settingsManager.reloadConfig();
    }

    public int getTicksFromItem(ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return 0;
        if (item.getItemMeta().getDisplayName().contains(":")) {
            return NumberUtils.toInt(item.getItemMeta().getDisplayName().replace("\u00A7", "").split(":")[0], 0);
        }
        return 0;
    }

    public ItemStack makAnchorItem(int ticks) {
        ItemStack item = new ItemStack(Material.valueOf(EpicAnchors.getInstance().getConfig().getString("Main.Anchor Block Material")), 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Methods.formatName(ticks, true));
        ArrayList<String> lore = new ArrayList<>();
        String[] parts = Setting.LORE.getString().split("\\|");
        for (String line : parts) {
            lore.add(Methods.formatText(line));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    public boolean isServerVersion(ServerVersion version) {
        return serverVersion == version;
    }

    public boolean isServerVersion(ServerVersion... versions) {
        return ArrayUtils.contains(versions, serverVersion);
    }

    public boolean isServerVersionAtLeast(ServerVersion version) {
        return serverVersion.ordinal() >= version.ordinal();
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public Locale getLocale() {
        return locale;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AnchorManager getAnchorManager() {
        return anchorManager;
    }

    public References getReferences() {
        return references;
    }
}
