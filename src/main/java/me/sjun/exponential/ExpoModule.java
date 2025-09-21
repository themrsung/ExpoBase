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
     * Called when this module is registered.
     * @param expo The expo base
     */
    void onRegistered(@NotNull ExpoBase expo);

    /**
     * Called when this module is unregistered.
     * @param expo The expo base
     */
    void onUnregistered(@NotNull ExpoBase expo);
}
