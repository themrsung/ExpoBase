package me.sjun.exponential;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        String targetName = name.trim();
        if (targetName.isEmpty()) return false;

        Path moduleDirectory = Paths.get(MODULES_DIRECTORY);
        if (!Files.isDirectory(moduleDirectory)) return false;

        Path moduleJar = null;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(moduleDirectory)) {
            for (Path path : stream) {
                if (!Files.isRegularFile(path)) continue;
                String fileName = path.getFileName().toString();
                int extensionIndex = fileName.lastIndexOf('.');
                if (extensionIndex <= 0) continue;

                String extension = fileName.substring(extensionIndex + 1);
                if (!"jar".equalsIgnoreCase(extension)) continue;

                String baseName = fileName.substring(0, extensionIndex);
                if (!baseName.equalsIgnoreCase(targetName)) continue;

                moduleJar = path;
                break;
            }
        } catch (IOException e) {
            log(Level.SEVERE, "Failed to read module directory", e);
            return false;
        }

        if (moduleJar == null) return false;

        URLClassLoader classLoader;
        try {
            URL jarUrl = moduleJar.toUri().toURL();
            classLoader = new URLClassLoader(new URL[]{jarUrl}, ExpoLoader.class.getClassLoader());
        } catch (MalformedURLException e) {
            log(Level.SEVERE, "Invalid module path for " + targetName, e);
            return false;
        }

        ExpoBase expo;
        try {
            expo = JavaPlugin.getPlugin(ExpoBase.class);
        } catch (IllegalStateException e) {
            log(Level.SEVERE, "ExpoBase plugin is not initialised", e);
            try {
                classLoader.close();
            } catch (IOException ignored) {
            }
            return false;
        }

        boolean loaded = false;
        try {
            ServiceLoader<ExpoModule> modules = ServiceLoader.load(ExpoModule.class, classLoader);
            for (ExpoModule module : modules) {
                if (!module.getName().equalsIgnoreCase(targetName)) continue;

                UUID moduleId = ExpoBase.registerModule(module);
                try {
                    module.onRegistered(expo);
                } catch (RuntimeException exception) {
                    ExpoBase.unregisterModule(moduleId);
                    throw exception;
                }

                loaded = true;
                break;
            }
        } catch (ServiceConfigurationError | RuntimeException e) {
            log(Level.SEVERE, "Failed to load module " + targetName, e);
            loaded = false;
        }

        if (!loaded) {
            try {
                classLoader.close();
            } catch (IOException ignored) {
            }
        }

        return loaded;
    }

    public void loadAllModules() {

    }

    private void log(@NotNull Level level, @NotNull String message, @NotNull Throwable throwable) {
        Logger logger;
        try {
            logger = JavaPlugin.getPlugin(ExpoBase.class).getLogger();
        } catch (IllegalStateException e) {
            // Plugin not initialised. Nowhere to log to.
            throwable.printStackTrace();
            return;
        }
        logger.log(level, message, throwable);
    }
}
