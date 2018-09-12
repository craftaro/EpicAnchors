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
        o1("Main.Name-Tag", "&eAnchor &8(&7{REMAINING}&8)"),
        o2("Main.Anchor-Lore", "&7Place down to keep that chunk|&7loaded until the time runs out."),
        o3("Main.Anchor Block Material", "END_PORTAL_FRAME"),
        o4("Main.Add Time With Economy", true),
        o5("Main.Economy Cost", 5000.0),
        o6("Main.Add Time With XP", true),
        o7("Main.XP Cost", 10),
        o8("Main.Allow Anchor Breaking", false),
        o9("Interfaces.Economy Icon", "SUNFLOWER"),
        o10("Interfaces.XP Icon", "EXPERIENCE_BOTTLE"),
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