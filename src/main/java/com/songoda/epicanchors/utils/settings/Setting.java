package com.songoda.epicanchors.utils.settings;

import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.utils.ServerVersion;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Setting {

    NAMETAG("Main.Name Tag", "&6Anchor &8(&7{REMAINING}&8)"),

    LORE("Main.Anchor Lore", "&7Place down to keep that chunk|&7loaded until the time runs out."),

    MATERIAL("Main.Anchor Block Material", EpicAnchors.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ? "END_PORTAL_FRAME" : "ENDER_PORTAL_FRAME"),

    ADD_TIME_WITH_ECONOMY("Main.Add Time With Economy", true),

    ECONOMY_COST("Main.Economy Cost", 5000.0),

    ADD_TIME_WITH_XP("Main.Add Time With XP", true),

    XP_COST("Main.XP Cost", 10),

    ALLOW_ANCHOR_BREAKING("Main.Allow Anchor Breaking", false),

    HOLOGRAMS("Main.Holograms", true,
            "Toggle holograms showing above anchors."),

    ECO_ICON("Interfaces.Economy Icon", EpicAnchors.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ? "SUNFLOWER" : "DOUBLE_PLANT",
            "Item to be displayed as the icon for economy upgrades."),

    XP_ICON("Interfaces.XP Icon", EpicAnchors.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ? "EXPERIENCE_BOTTLE" : "EXP_BOTTLE",
            "Item to be displayed as the icon for XP upgrades."),

    GLASS_TYPE_1("Interfaces.Glass Type 1", 7),
    GLASS_TYPE_2("Interfaces.Glass Type 2", 11),
    GLASS_TYPE_3("Interfaces.Glass Type 3", 3),

    LANGUGE_MODE("System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder.");

    private String setting;
    private Object option;
    private String[] comments;

    Setting(String setting, Object option, String... comments) {
        this.setting = setting;
        this.option = option;
        this.comments = comments;
    }

    Setting(String setting, Object option) {
        this.setting = setting;
        this.option = option;
        this.comments = null;
    }

    public static com.songoda.epicanchors.utils.settings.Setting getSetting(String setting) {
        List<com.songoda.epicanchors.utils.settings.Setting> settings = Arrays.stream(values()).filter(setting1 -> setting1.setting.equals(setting)).collect(Collectors.toList());
        if (settings.isEmpty()) return null;
        return settings.get(0);
    }

    public String getSetting() {
        return setting;
    }

    public Object getOption() {
        return option;
    }

    public String[] getComments() {
        return comments;
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

    public long getLong() {
        return EpicAnchors.getInstance().getConfig().getLong(setting);
    }

    public String getString() {
        return EpicAnchors.getInstance().getConfig().getString(setting);
    }

    public char getChar() {
        return EpicAnchors.getInstance().getConfig().getString(setting).charAt(0);
    }

    public double getDouble() {
        return EpicAnchors.getInstance().getConfig().getDouble(setting);
    }
}