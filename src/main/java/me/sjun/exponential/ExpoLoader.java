package me.sjun.exponential;

import org.jetbrains.annotations.NotNull;

/**
 * Expo loader.
 */
public class ExpoLoader {
    /**
     * The modules directory.
     */
    public static final @NotNull String MODULES_DIRECTORY = "plugins/Expo/modules";

    /**
     * Loads the module.
     * @param name The name of the module (case-insensitive)
     * @return {@code true} if successful
     */
    public boolean loadModule(@NotNull String name) {
        // Get module.jar from MODULES_DIRECTORY
        // Call ExpoBase.registerModule(ExpoModule)
        // return false if error
    }
}
