package me.sjun.exponential;

import org.jetbrains.annotations.NotNull;

/**
 * Exponential Module.
 */
public interface ExpoModule {
    /**
     * Returns the name of this module.
     * @return The name of this module
     */
    @NotNull String getName();

    /**
     * Called when this module is loaded.
     * @param expo The Expo base
     */
    default void onLoaded(@NotNull ExpoBase expo) {}

    /**
     * Called when this module is unloaded.
     */
    default void onUnloaded() {}
}
