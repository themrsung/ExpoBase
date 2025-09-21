package me.sjun.exponential.command;

import me.sjun.exponential.ExpoBase;
import me.sjun.exponential.ExpoModule;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Reload module command.
 */
public class ReloadCommand implements TabExecutor {
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("Insufficient permissions."));
            return false;
        }

        Map<UUID, ExpoModule> moduleMap = ExpoBase.getRegisteredModuleMap();

        if (args.length > 0) {
            UUID[] moduleId = new UUID[1];

            moduleMap.forEach((id, module) -> {
                if (moduleId[0] != null) return;
                if (module.getName().equalsIgnoreCase(args[0])) moduleId[0] = id;
            });

            if (moduleId[0] == null) {
                sender.sendMessage(Component.text("Cannot find module \"" + args[0] + "\"."));
                return false;
            }

            if (!unregisterModule(moduleId[0], sender, args[0])) return false;

            if (!ExpoBase.getLoader().loadModule(args[0])) {
                sender.sendMessage(Component.text("Failed to load module \"" + args[0] + "\""));
                return false;
            } else {
                sender.sendMessage(Component.text("Loaded and registered module \"" + args[0] + "\""));
                return true;
            }
        }

        List<String> moduleNames = new ArrayList<>();

        moduleMap.forEach((id, module) -> {
            String name = module.getName();
            if (unregisterModule(id, sender, name)) {
                moduleNames.add(name);
            }
        });

        List<String> failedModules = new ArrayList<>();
        moduleNames.forEach(name -> {
            if (ExpoBase.getLoader().loadModule(name)) return;
            failedModules.add(name);
        });

        sender.sendMessage(Component.text("Failed to load modules: ")
                .append(Component.text(String.join(", ", failedModules.toArray(String[]::new)))));

        if (failedModules.isEmpty()) {
            sender.sendMessage(Component.text("Success!"));
            return true;
        } else {
            sender.sendMessage(Component.text("One or more modules failed to reload."));
            return false;
        }
    }

    private boolean unregisterModule(@NotNull UUID moduleId, @NotNull CommandSender sender, @NotNull String name) {
        if (!ExpoBase.unregisterModule(moduleId)) {
            sender.sendMessage(Component.text("Error unregistering module \"" + name + "\"."));
            return false;
        } else {
            sender.sendMessage(Component.text("Module \"" + name + "\" unregistered!"));
            return true;
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {
        if (!sender.isOp() || args.length < 1) return List.of();
        return ExpoBase.getRegisteredModules().stream()
                .map(ExpoModule::getName)
                .filter(m -> m.toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();
    }
}
