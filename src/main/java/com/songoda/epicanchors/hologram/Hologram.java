package com.songoda.epicanchors.hologram;

import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.anchor.Anchor;
import com.songoda.epicanchors.utils.Methods;
import org.bukkit.Location;

import java.util.Collection;

public abstract class Hologram {

    protected final EpicAnchors plugin;

    Hologram(EpicAnchors plugin) {
        this.plugin = plugin;
    }

    public void loadHolograms() {
        Collection<Anchor> anchors = plugin.getAnchorManager().getAnchors().values();
        if (anchors.size() == 0) return;

        for (Anchor anchor : anchors) {
            if (anchor.getWorld() == null) continue;
            add(anchor);
        }
    }

    public void unloadHolograms() {
        Collection<Anchor> anchors = plugin.getAnchorManager().getAnchors().values();
        if (anchors.size() == 0) return;

        for (Anchor anchor : anchors) {
            if (anchor.getWorld() == null) continue;
            remove(anchor);
        }
    }

    public void add(Anchor anchor) {
        String name = Methods.formatName(anchor.getTicksLeft(), false).trim();

        add(anchor.getLocation(), name);
    }

    public void remove(Anchor anchor) {
        remove(anchor.getLocation());
    }

    public void update(Anchor anchor) {
        String name = Methods.formatName(anchor.getTicksLeft(), false).trim();

        update(anchor.getLocation(), name);
    }

    protected abstract void add(Location location, String line);

    protected abstract void remove(Location location);

    protected abstract void update(Location location, String line);

}
