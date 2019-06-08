package com.songoda.epicanchors.utils.gui;

import com.songoda.epicanchors.EpicAnchorsPlugin;
import com.songoda.epicanchors.utils.ServerVersion;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;

public class Range {

    private int min;
    private int max;
    private ClickType clickType;
    private boolean bottom;
    private Sound onClickSound;

    public Range(int min, int max, ClickType clickType, boolean bottom) {
        this(min, max, null, clickType, bottom);
    }

    public Range(int min, int max, Sound onClickSound, ClickType clickType, boolean bottom) {
        this.min = min;
        this.max = max;
        this.clickType = clickType;
        this.bottom = bottom;

        if (onClickSound == null) {
            if (EpicAnchorsPlugin.getInstance().isServerVersionAtLeast(ServerVersion.V1_9)) {
                this.onClickSound = Sound.UI_BUTTON_CLICK;
            } else {
                this.onClickSound = Sound.valueOf("CLICK");
            }
        } else {
            this.onClickSound = onClickSound;
        }
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public boolean isBottom() {
        return bottom;
    }

    public Sound getOnClickSound() {
        return onClickSound;
    }
}