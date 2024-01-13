package com.craftaro.epicanchors;

import com.craftaro.core.SongodaCore;
import com.craftaro.core.SongodaPlugin;
import com.craftaro.core.commands.CommandManager;
import com.craftaro.core.configuration.Config;
import com.craftaro.core.dependency.Dependency;
import com.craftaro.core.gui.GuiManager;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.hooks.HologramManager;
import com.craftaro.epicanchors.api.AnchorManager;
import com.craftaro.epicanchors.commands.EpicAnchorsCommand;
import com.craftaro.epicanchors.commands.sub.GiveCommand;
import com.craftaro.epicanchors.commands.sub.ReloadCommand;
import com.craftaro.epicanchors.commands.sub.SettingsCommand;
import com.craftaro.epicanchors.commands.sub.ShowCommand;
import com.craftaro.epicanchors.files.AnchorsDataManager;
import com.craftaro.epicanchors.files.Settings;
import com.craftaro.epicanchors.files.migration.LegacyYamlAnchorsMigrator;
import com.craftaro.epicanchors.files.migration._1_InitialMigration;
import com.craftaro.epicanchors.listener.AnchorListener;
import com.craftaro.epicanchors.listener.BlockListener;
import com.craftaro.epicanchors.listener.WorldListener;
import com.craftaro.epicanchors.tasks.AnchorTask;
import com.craftaro.epicanchors.tasks.VisualizeTask;
import com.craftaro.epicanchors.utils.ThreadSync;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public final class EpicAnchors extends SongodaPlugin {
    private GuiManager guiManager;
    private AnchorManagerImpl anchorManager;

    private AnchorsDataManager dataManager;

    @Override
    public void onPluginLoad() {
    }

    @Override
    public void onPluginEnable() {
        SongodaCore.registerPlugin(this, 31, XMaterial.END_PORTAL_FRAME);

        initializeDataManager();

        this.anchorManager = new AnchorManagerImpl(this, this.dataManager);
        EpicAnchorsApi.initApi(this.anchorManager);

        // Economy [1/2]
        EconomyManager.load();

        // Config
        Settings.setupConfig();
        this.setLocale(Settings.LANGUAGE.getString(), false);

        // Economy [2/2]
        EconomyManager.getManager().setPreferredHook(Settings.ECONOMY_PLUGIN.getString());

        // Holograms
        HologramManager.load(this);

        // Event Listener
        this.guiManager = new GuiManager(this);
        this.guiManager.init();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new WorldListener(
                        world -> this.anchorManager.initAnchorsAsync(world, null),
                        world -> this.anchorManager.deInitAnchors(world)),
                this);
        pluginManager.registerEvents(new AnchorListener(this), this);
        pluginManager.registerEvents(new BlockListener(this.anchorManager), this);

        // Commands
        CommandManager commandManager = new CommandManager(this);
        commandManager.addCommand(new EpicAnchorsCommand(this, commandManager))
                .addSubCommands(
                        new GiveCommand(this),
                        new ReloadCommand(this),
                        new SettingsCommand(this, this.guiManager),
                        new ShowCommand(this)
                );
    }

    @Override
    public void onPluginDisable() {
        // Save all Anchors
        if (this.dataManager != null) {
            this.anchorManager.deInitAll();

            this.dataManager.close();
        }

        // Remove all holograms
        HologramManager.removeAllHolograms();
    }

    @Override
    public void onDataLoad() {
        new Thread(() -> {
            ThreadSync tSync = new ThreadSync();

            for (World w : Bukkit.getWorlds()) {
                this.anchorManager.initAnchorsAsync(w, (ex) -> {
                    if (ex != null) {
                        this.getLogger().log(Level.SEVERE, ex, () -> "Failed to initialize world '" + w.getName() + "'");
                    }

                    tSync.release();
                });

                tSync.waitForRelease();
                tSync.reset();
            }

            this.anchorManager.setReady();

            // Start tasks
            new AnchorTask(this).startTask();
            new VisualizeTask(this).startTask();
        }).start();
    }

    @Override
    public void onConfigReload() {
        this.setLocale(Settings.LANGUAGE.getString(), true);
    }

    @Override
    public List<Config> getExtraConfig() {
        return Collections.emptyList();
    }

    public GuiManager getGuiManager() {
        return this.guiManager;
    }

    public AnchorManager getAnchorManager() {
        return this.anchorManager;
    }

    @Override
    public Config getDatabaseConfig() {
        Config staticDatabaseConfig = new Config();
        staticDatabaseConfig.set("Connection Settings.Type", "H2");
        staticDatabaseConfig.set("Connection Settings.Pool Size", 1);
        return staticDatabaseConfig;
    }

    private void initializeDataManager() {
        super.initDatabase(new _1_InitialMigration());

        this.dataManager = new AnchorsDataManager(this);
        LegacyYamlAnchorsMigrator.migrateLegacyData(this, this.dataManager);
    }
}
