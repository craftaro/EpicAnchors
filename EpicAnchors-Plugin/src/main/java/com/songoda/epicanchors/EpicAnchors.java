package com.songoda.epicanchors;

import com.craftaro.core.SongodaCore;
import com.craftaro.core.SongodaPlugin;
import com.craftaro.core.commands.CommandManager;
import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.configuration.Config;
import com.craftaro.core.database.DatabaseConnector;
import com.craftaro.core.database.SQLiteConnector;
import com.craftaro.core.gui.GuiManager;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.hooks.HologramManager;
import com.songoda.epicanchors.api.AnchorManager;
import com.songoda.epicanchors.commands.EpicAnchorsCommand;
import com.songoda.epicanchors.commands.sub.GiveCommand;
import com.songoda.epicanchors.commands.sub.ReloadCommand;
import com.songoda.epicanchors.commands.sub.SettingsCommand;
import com.songoda.epicanchors.commands.sub.ShowCommand;
import com.songoda.epicanchors.files.DataManager;
import com.songoda.epicanchors.files.Settings;
import com.songoda.epicanchors.files.migration.AnchorMigration;
import com.songoda.epicanchors.files.migration._1_InitialMigration;
import com.songoda.epicanchors.listener.AnchorListener;
import com.songoda.epicanchors.listener.BlockListener;
import com.songoda.epicanchors.listener.WorldListener;
import com.songoda.epicanchors.tasks.AnchorTask;
import com.songoda.epicanchors.tasks.VisualizeTask;
import com.songoda.epicanchors.utils.ThreadSync;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public final class EpicAnchors extends SongodaPlugin {
    private GuiManager guiManager;
    private AnchorManagerImpl anchorManager;

    private DataManager dataManager;

    @Override
    public void onPluginLoad() {
    }

    @Override
    public void onPluginEnable() {
        SongodaCore.registerPlugin(this, 31, CompatibleMaterial.END_PORTAL_FRAME);

        // Initialize database
        this.getLogger().info("Initializing SQLite...");
        DatabaseConnector dbCon = new SQLiteConnector(this);
        this.dataManager = new DataManager(dbCon, this);
        AnchorMigration anchorMigration = new AnchorMigration(dbCon, this.dataManager, new _1_InitialMigration());
        anchorMigration.runMigrations();

        anchorMigration.migrateLegacyData(this);
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
}
