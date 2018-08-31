package com.songoda.epicanchors.anchor;

import com.songoda.epicanchors.api.anchor.Level;

public class ELevel implements com.songoda.epicanchors.api.anchor.Level {

    private int level, ticks;

    public ELevel(int level, int ticks) {
        this.level = level;
        this.ticks = ticks;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getTicks() {
        return ticks;
    }

    @Override
    public void setTicks(int ticks) {
        this.ticks = ticks;
    }
}
