package com.songoda.epicanchors.anchor;

import org.bukkit.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnchorManager {

    private final Map<Location, Anchor> registeredAnchors = new HashMap<>();

    public Anchor addAnchor(Location location, Anchor anchor) {
        return registeredAnchors.put(roundLocation(location), anchor);
    }

    public void removAnchor(Location location) {
        registeredAnchors.remove(roundLocation(location));
    }

    public Anchor getAnchor(Location location) {
        return registeredAnchors.get(roundLocation(location));
    }

    public boolean isAnchor(Location location) {
        return registeredAnchors.containsKey(location);
    }

    public Map<Location, Anchor> getAnchors() {
        return Collections.unmodifiableMap(registeredAnchors);
    }

    private Location roundLocation(org.bukkit.Location location) {
        location = location.clone();
        location.setX(location.getBlockX());
        location.setY(location.getBlockY());
        location.setZ(location.getBlockZ());
        return location;
    }
}
