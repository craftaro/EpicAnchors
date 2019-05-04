package com.songoda.epicanchors;

import com.google.common.base.Preconditions;
import com.songoda.epicanchors.anchor.EAnchor;
import com.songoda.epicanchors.anchor.EAnchorManager;
import com.songoda.epicanchors.api.EpicAnchors;
import com.songoda.epicanchors.api.anchor.Anchor;
import com.songoda.epicanchors.api.anchor.AnchorManager;
import com.songoda.epicanchors.api.utils.ClaimableProtectionPluginHook;
import com.songoda.epicanchors.api.utils.ProtectionPluginHook;
import com.songoda.epicanchors.command.CommandManager;
import com.songoda.epicanchors.listeners.BlockListeners;
import com.songoda.epicanchors.listeners.InteractListeners;
import com.songoda.epicanchors.handlers.AnchorHandler;
import com.songoda.epicanchors.hooks.*;
import com.songoda.epicanchors.utils.ConfigWrapper;
import com.songoda.epicanchors.utils.Methods;
import com.songoda.epicanchors.utils.ServerVersion;
import com.songoda.epicanchors.utils.SettingsManager;
import com.songoda.epicanchors.utils.updateModules.LocaleModule;
import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EpicAnchorsPlugin extends JavaPlugin implements EpicAnchors {

    public ConfigWrapper dataFile = new ConfigWrapper(this, "", "data.yml");

    private List<ProtectionPluginHook> protectionHooks = new ArrayList<>();
    private ClaimableProtectionPluginHook factionsHook, townyHook, aSkyblockHook, uSkyblockHook;

    private ConfigWrapper hooksFile = new ConfigWrapper(this, "", "hooks.yml");

    private ServerVersion serverVersion = ServerVersion.fromPackageName(Bukkit.getServer().getClass().getPackage().getName());

    private static EpicAnchorsPlugin INSTANCE;

    private SettingsManager settingsManager;
    private EAnchorManager anchorManager;

    private CommandManager commandManager;

    private References references;

    private Locale locale;

    public static EpicAnchorsPlugin getInstance() {
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

        setupConfig();

        // Locales
        String langMode = SettingsManager.Setting.LANGUGE_MODE.getString();
        Locale.init(this);
        Locale.saveDefaultLocale("en_US");
        this.locale = Locale.getLocale(langMode);

        //Running Songoda Updater
        Plugin plugin = new Plugin(this, 31);
        plugin.addModule(new LocaleModule());
        SongodaUpdate.load(plugin);

        dataFile.createNewFile("Loading Data File", "EpicAnchors Data File");

        this.references = new References();
        this.anchorManager = new EAnchorManager();
        this.commandManager = new CommandManager(this);

        loadAnchorsFromFile();

        new AnchorHandler(this);

        // Command registration
        this.getCommand("EpicAnchors").setExecutor(new CommandManager(this));

        PluginManager pluginManager = Bukkit.getPluginManager();

        // Event registration
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this), this);

        // Register default hooks
        if (pluginManager.isPluginEnabled("ASkyBlock")) this.register(HookASkyBlock::new);
        if (pluginManager.isPluginEnabled("FactionsFramework")) this.register(HookFactions::new);
        if (pluginManager.isPluginEnabled("GriefPrevention")) this.register(HookGriefPrevention::new);
        if (pluginManager.isPluginEnabled("Kingdoms")) this.register(HookKingdoms::new);
        if (pluginManager.isPluginEnabled("PlotSquared")) this.register(HookPlotSquared::new);
        if (pluginManager.isPluginEnabled("RedProtect")) this.register(HookRedProtect::new);
        if (pluginManager.isPluginEnabled("Towny")) this.register(HookTowny::new);
        if (pluginManager.isPluginEnabled("USkyBlock")) this.register(HookUSkyBlock::new);
        if (pluginManager.isPluginEnabled("WorldGuard")) this.register(HookWorldGuard::new);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::saveToFile, 6000, 6000);
        console.sendMessage(Methods.formatText("&a============================="));
    }

    public void onDisable() {
        saveToFile();
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7EpicAnchors " + this.getDescription().getVersion() + " by &5Brianna <3!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Methods.formatText("&a============================="));
    }

    private void setupConfig() {
        settingsManager.updateSettings();

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void loadAnchorsFromFile() {
        if (dataFile.getConfig().contains("Anchors")) {
            for (String locationStr : dataFile.getConfig().getConfigurationSection("Anchors").getKeys(false)) {
                Location location = Methods.unserializeLocation(locationStr);
                int ticksLeft = dataFile.getConfig().getInt("Anchors." + locationStr + ".ticksLeft");

                EAnchor anchor = new EAnchor(location, ticksLeft);

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
        this.reloadConfig();
        this.setupConfig();
    }


    private void register(Supplier<ProtectionPluginHook> hookSupplier) {
        this.registerProtectionHook(hookSupplier.get());
    }


    @Override
    public void registerProtectionHook(ProtectionPluginHook hook) {
        Preconditions.checkNotNull(hook, "Cannot register null hook");
        Preconditions.checkNotNull(hook.getPlugin(), "Protection plugin hook returns null plugin instance (#getPlugin())");

        JavaPlugin hookPlugin = hook.getPlugin();
        for (ProtectionPluginHook existingHook : protectionHooks) {
            if (existingHook.getPlugin().equals(hookPlugin)) {
                throw new IllegalArgumentException("Hook already registered");
            }
        }

        this.hooksFile.getConfig().addDefault("hooks." + hookPlugin.getName(), true);
        if (!hooksFile.getConfig().getBoolean("hooks." + hookPlugin.getName(), true)) return;
        this.hooksFile.getConfig().options().copyDefaults(true);
        this.hooksFile.saveConfig();

        this.protectionHooks.add(hook);
        this.getLogger().info("Registered protection hook for plugin: " + hook.getPlugin().getName());
    }

    public boolean canBuild(Player player, Location location) {
        if (player.hasPermission(getDescription().getName() + ".bypass")) {
            return true;
        }

        for (ProtectionPluginHook hook : protectionHooks)
            if (!hook.canBuild(player, location)) return false;
        return true;
    }

    @Override
    public int getTicksFromItem(ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return 0;
        if (item.getItemMeta().getDisplayName().contains(":")) {
            return NumberUtils.toInt(item.getItemMeta().getDisplayName().replace("\u00A7", "").split(":")[0], 0);
        }
        return 0;
    }

    @Override
    public ItemStack makeAnchorItem(int ticks) {
        ItemStack item = new ItemStack(Material.valueOf(EpicAnchorsPlugin.getInstance().getConfig().getString("Main.Anchor Block Material")), 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Methods.formatText(Methods.formatName(ticks, true)));
        ArrayList<String> lore = new ArrayList<>();
        String[] parts = getConfig().getString("Main.Anchor-Lore").split("\\|");
        for (String line : parts) {
            lore.add(Methods.formatText(line));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void bust(Location location) {
        if (!getAnchorManager().isAnchor(location)) return;

        Anchor anchor = getAnchorManager().getAnchor(location);

        if (getConfig().getBoolean("Main.Allow Anchor Breaking")) {
            ItemStack item = makeAnchorItem(anchor.getTicksLeft());
            anchor.getLocation().getWorld().dropItemNaturally(anchor.getLocation(), item);
        }
        location.getBlock().setType(Material.AIR);
        getAnchorManager().removeAnchor(location);
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

    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public AnchorManager getAnchorManager() {
        return anchorManager;
    }

    public References getReferences() {
        return references;
    }
}
