package com.craftaro.epicanchors.utils;

import org.jetbrains.annotations.Nullable;

public interface Callback<T> {
    void accept(@Nullable Exception ex, T result);
}
