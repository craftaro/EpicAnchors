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
        o1("Main.Anchor Block Material", "END_PORTAL_FRAME"),
        o2("Main.Add Time With Economy", true),
        o3("Main.Economy Cost", 5000.0),
        o4("Main.Add Time With XP", true),
        o5("Main.XP Cost", 10),
        o6("Main.Allow Anchor Breaking", false),
        o7("Interfaces.Economy Icon", "SUNFLOWER"),
        o8("Interfaces.XP Icon", "EXPERIENCE_BOTTLE"),
        o9("Interfaces.Glass Type 1", 7),
        o10("Interfaces.Glass Type 2", 11),
        o11("Interfaces.Glass Type 3", 3);

        private String setting;
        private Object option;

        settings(String setting, Object option) {
            this.setting = setting;
            this.option = option;
        }

    }
}