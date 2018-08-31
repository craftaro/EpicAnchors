package com.songoda.epicanchors.api.anchor;

import org.bukkit.Location;

public interface Anchor {

    Location getLocation();

    int getTicksLeft();

    void setTicksLeft(int ticksLeft);
}
