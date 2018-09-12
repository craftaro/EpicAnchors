package com.songoda.epicanchors.anchor;

import com.songoda.epicanchors.api.anchor.Anchor;
import com.songoda.epicanchors.api.anchor.AnchorManager;
import org.bukkit.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EAnchorManager implements AnchorManager {

    private final Map<Location, Anchor> registeredAnchors = new HashMap<>();

    @Override
    public Anchor addAnchor(Location location, Anchor anchor) {
        return registeredAnchors.put(roundLocation(location), anchor);
    }

    @Override
    public void removeAnchor(Location location) {
        registeredAnchors.remove(roundLocation(location));
    }

    @Override
    public Anchor getAnchor(Location location) {
        return registeredAnchors.get(roundLocation(location));
    }

    @Override
    public boolean isAnchor(Location location) {
        return registeredAnchors.containsKey(location);
    }

    @Override
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
