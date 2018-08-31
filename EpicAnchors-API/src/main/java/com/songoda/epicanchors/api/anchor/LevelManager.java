package com.songoda.epicanchors.api.anchor;

import java.util.Map;

public interface LevelManager {
    void addLevel(int level, int ticks);

    Level getLevel(int level);

    Level getLowestLevel();

    Level getHighestLevel();

    Map<Integer, Level> getLevels();
}
