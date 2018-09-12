package com.songoda.epicanchors.api.anchor;

import org.bukkit.Location;

import java.util.Map;

public interface AnchorManager {
    Anchor addAnchor(Location location, Anchor anchor);

    void removeAnchor(Location location);

    Anchor getAnchor(Location location);

    boolean isAnchor(Location location);

    Map<Location, Anchor> getAnchors();
}
