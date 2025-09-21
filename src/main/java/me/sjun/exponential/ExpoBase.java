package me.sjun.exponential;

import me.sjun.exponential.command.DisableCommand;
import me.sjun.exponential.command.InfoCommand;
import me.sjun.exponential.command.ReloadCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class ExpoBase extends JavaPlugin {
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
        UUID uniqueId = UUID.randomUUID();
        registeredModules.put(uniqueId, module);
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
        return registeredModules.remove(uniqueId) != null;
    }

    @Override
    public void onEnable() {
        registerCommand(
                "exporeload",
                new ReloadCommand(),
                "Reloads Expo modules.",
                "/exporeload [module name]"
        );
        registerCommand(
                "expodisable",
                new DisableCommand(),
                "Disables an Expo module.",
                "/expodisable <module name>"
        );
        registerCommand(
                "expoinfo",
                new InfoCommand(),
                "Displays Expo plugin information and registered modules.",
                "/expoinfo"
        );
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommand(
            @NotNull String name,
            @NotNull CommandExecutor executor,
            @Nullable String description,
            @Nullable String usage
    ) {
        PluginCommand command = createCommandInstance(name);
        if (command == null) {
            getLogger().severe("Failed to create command \"" + name + "\"; it will not be registered.");
            return;
        }

        command.setExecutor(executor);
        if (executor instanceof TabCompleter tabCompleter) {
            command.setTabCompleter(tabCompleter);
        }

        if (description != null) {
            command.setDescription(description);
        }
        if (usage != null) {
            command.setUsage(usage);
        }

        CommandMap commandMap = getServer().getCommandMap();
        String fallbackPrefix = getDescription().getName().toLowerCase(Locale.ROOT);
        if (!commandMap.register(fallbackPrefix, command)) {
            getLogger().warning("Command \"" + name + "\" could not be registered because another command with the same name already exists.");
        }
    }

    private @Nullable PluginCommand createCommandInstance(@NotNull String name) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, JavaPlugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, this);
        } catch (ReflectiveOperationException | InaccessibleObjectException e) {
            getLogger().log(Level.SEVERE, "Unable to instantiate command \"" + name + "\".", e);
            return null;
        }
    }
}
