package com.songoda.epicanchors.utils;

import com.songoda.epicanchors.EpicAnchorsPlugin;
import org.bukkit.event.Listener;

/**
 * Created by songo on 6/4/2017.
 */
public class SettingsManager implements Listener {

    private final EpicAnchorsPlugin instance;

    public SettingsManager(EpicAnchorsPlugin instance) {
        this.instance = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    public void updateSettings() {
        for (settings s : settings.values()) {
            instance.getConfig().addDefault(s.setting, s.option);
        }
    }

    public enum settings {
        o8("Main.Anchor Block Material", "ENDER_PORTAL_FRAME"),
        o11("Interfaces.Glass Type 1", 7),
        o12("Interfaces.Glass Type 2", 11),
        o13("Interfaces.Glass Type 3", 3);

        private String setting;
        private Object option;

        settings(String setting, Object option) {
            this.setting = setting;
            this.option = option;
        }

    }
}