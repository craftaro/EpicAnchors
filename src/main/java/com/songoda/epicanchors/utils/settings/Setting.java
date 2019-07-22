package com.songoda.epicanchors.utils.settings;

import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.utils.ServerVersion;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Setting {

    NAMETAG("Main.Name Tag", "&6Anchor &8(&7{REMAINING}&8)",
            "The anchor name tag used on the item and in the hologram."),

    LORE("Main.Anchor Lore", "&7Place down to keep that chunk|&7loaded until the time runs out.",
            "The lore on the anchor item."),

    MATERIAL("Main.Anchor Block Material", EpicAnchors.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ? "END_PORTAL_FRAME" : "ENDER_PORTAL_FRAME",
            "The material an anchor is represented with?"),

    ADD_TIME_WITH_ECONOMY("Main.Add Time With Economy", true,
            "Should players be able to add time to their anchors",
            "by using economy?"),

    ECONOMY_COST("Main.Economy Cost", 5000.0,
            "The cost in economy to add 30 minutes to an anchor."),

    ADD_TIME_WITH_XP("Main.Add Time With XP", true,
            "Should players be able to add time to their anchors",
            "by using experience?"),

    XP_COST("Main.XP Cost", 10,
            "The cost in experience to add 30 minutes to an anchor."),

    ALLOW_ANCHOR_BREAKING("Main.Allow Anchor Breaking", false,
            "Should players be able to break anchors?"),

    HOLOGRAMS("Main.Holograms", true,
            "Toggle holograms showing above anchors."),

    VAULT_ECONOMY("Economy.Use Vault Economy", true,
            "Should Vault be used?"),

    RESERVE_ECONOMY("Economy.Use Reserve Economy", true,
            "Should Reserve be used?"),

    PLAYER_POINTS_ECONOMY("Economy.Use Player Points Economy", false,
            "Should PlayerPoints be used?"),

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