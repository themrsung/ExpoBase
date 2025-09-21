package me.sjun.exponential.command;

import me.sjun.exponential.ExpoBase;
import me.sjun.exponential.ExpoModule;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Reload module command.
 */
public class DisableCommand extends Command {
    public DisableCommand() {
        super("expodisable");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("Insufficient permissions."));
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /expodisable <module name>"));
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
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
        if (!sender.isOp() || args.length < 1) return List.of();
        return ExpoBase.getRegisteredModules().stream()
                .map(ExpoModule::getName)
                .filter(m -> m.toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();
    }
}
