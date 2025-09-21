package me.sjun.exponential.command;

import me.sjun.exponential.ExpoBase;
import me.sjun.exponential.ExpoModule;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Disable module command.
 */
public class DisableCommand implements TabExecutor {
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

        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /" + label + " <module name>"));
            return false;
        }

        String moduleName = args[0];
        Map<UUID, ExpoModule> moduleMap = ExpoBase.getRegisteredModuleMap();
        Optional<Map.Entry<UUID, ExpoModule>> target = moduleMap.entrySet().stream()
                .filter(entry -> entry.getValue().getName().equalsIgnoreCase(moduleName))
                .findFirst();

        if (target.isEmpty()) {
            sender.sendMessage(Component.text("Cannot find module \"" + moduleName + "\"."));
            return false;
        }

        UUID moduleId = target.get().getKey();
        ExpoModule module = target.get().getValue();

        if (!ExpoBase.unregisterModule(moduleId)) {
            sender.sendMessage(Component.text("Error disabling module \"" + module.getName() + "\"."));
            return false;
        }

        sender.sendMessage(Component.text("Module \"" + module.getName() + "\" disabled."));
        return true;
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
                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();
    }
}
