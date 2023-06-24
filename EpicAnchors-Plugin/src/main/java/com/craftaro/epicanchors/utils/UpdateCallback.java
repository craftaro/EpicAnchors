package com.craftaro.epicanchors.utils;

import org.jetbrains.annotations.Nullable;

public interface UpdateCallback {
    void accept(@Nullable Exception ex);
}
