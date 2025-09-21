package me.sjun.exponential;

import me.sjun.exponential.command.DisableCommand;
import me.sjun.exponential.command.InfoCommand;
import me.sjun.exponential.command.ReloadCommand;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ExpoBase extends JavaPlugin {
    /**
     * Instance.
     */
    private static ExpoBase instance = null;

    /**
     * Returns the instance.
     * @return The instance
     */
    public static @NotNull ExpoBase getInstance() {
        return Optional.ofNullable(instance).orElseThrow(IllegalStateException::new);
    }

    /**
     * The loader instance.
     */
    private static final @NotNull ExpoLoader loader = new ExpoLoader();

    /**
     * Returns the loader instance.
     * @return The loader instance
     */
    public static @NotNull ExpoLoader getLoader() {
        return loader;
    }

    /**
     * The map of registered modules.
     */
    private static final @NotNull Map<UUID, ExpoModule> registeredModules = new ConcurrentHashMap<>();

    /**
     * Returns the map of registered modules.
     * @return The map of registered modules
     */
    public static @NotNull Map<UUID, ExpoModule> getRegisteredModuleMap() {
        return Map.copyOf(registeredModules);
    }

    /**
     * Returns the list of registered modules.
     * @return The list of registered modules
     */
    public static @NotNull List<ExpoModule> getRegisteredModules() {
        return List.copyOf(registeredModules.values());
    }

    /**
     * Registers the module.
     * @param module The module to register
     * @return The unique identifier of the registry
     */
    public static @NotNull UUID registerModule(@NotNull ExpoModule module) {
        if (registeredModules.values().stream()
                .map(ExpoModule::getName)
                .anyMatch(module.getName()::equalsIgnoreCase)) {
            throw new IllegalArgumentException("Module already registered!");
        }

        UUID uniqueId = UUID.randomUUID();
        registeredModules.put(uniqueId, module);

        module.onRegistered(getInstance());

        return uniqueId;
    }

    /**
     * Finds and returns the module.
     * @param name The name of the module
     * @return The module
     */
    public static @NotNull Optional<ExpoModule> getRegisteredModule(@NotNull String name) {
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
        ExpoModule module = registeredModules.remove(uniqueId);
        if (module == null) return false;

        module.onUnregistered(getInstance());
        return true;
    }

    @Override
    public void onEnable() {
        CommandMap cm = getServer().getCommandMap();

        cm.register("expo", new DisableCommand());
        cm.register("expo", new InfoCommand());
        cm.register("expo", new ReloadCommand());

        loader.loadAllModules();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
