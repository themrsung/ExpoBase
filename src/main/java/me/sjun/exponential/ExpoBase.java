package me.sjun.exponential;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ExpoBase extends JavaPlugin {
    /**
     * The modules directory.
     */
    public static final @NotNull String MODULES_DIRECTORY = "plugins/Expo/modules";

    /**
     * The map of registered modules.
     */
    private static final @NotNull Map<UUID, ExpoModule> registeredModules = new ConcurrentHashMap<>();

    /**
     * Registers the module.
     * @param module The module to register
     * @return The unique identifier of the registry
     */
    public static @NotNull UUID registerModule(@NotNull ExpoModule module) {
        UUID uniqueId = UUID.randomUUID();
        registeredModules.put(uniqueId, module);
        return uniqueId;
    }

    /**
     * Finds and returns the module.
     * @param name The name of the module
     * @return The module
     */
    public static @NotNull Optional<ExpoModule> getModule(@NotNull String name) {
        return registeredModules.values().stream()
                .filter(v -> Objects.equals(v.getName(), name))
                .findAny();
    }

    /**
     * Unregisters the module.
     * @param uniqueId The unique identifier of the registry
     * @return {@code true} if it was successfully removed
     */
    public static boolean unregisterModule(@NotNull UUID uniqueId) {
        return registeredModules.remove(uniqueId) != null;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
