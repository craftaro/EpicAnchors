package com.songoda.epicanchors.anchor;


import com.songoda.epicanchors.api.anchor.LevelManager;
import com.songoda.epicanchors.api.anchor.Level;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ELevelManager implements LevelManager {

    private final NavigableMap<Integer, Level> registeredLevels = new TreeMap<>();

    @Override
    public void addLevel(int level, int ticks) {
        registeredLevels.put(level, new ELevel(level, ticks));
    }

    @Override
    public Level getLevel(int level) {
        return registeredLevels.get(level);
    }

    @Override
    public Level getLowestLevel() {
        return registeredLevels.firstEntry().getValue();
    }

    @Override
    public Level getHighestLevel() {
        return registeredLevels.lastEntry().getValue();
    }

    @Override
    public Map<Integer, Level> getLevels() {
        return Collections.unmodifiableMap(registeredLevels);
    }

    public void clear() {
        registeredLevels.clear();
    }
}
