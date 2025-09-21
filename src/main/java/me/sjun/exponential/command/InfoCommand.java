package me.sjun.exponential.command;

import me.sjun.exponential.ExpoBase;
import me.sjun.exponential.ExpoModule;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Reload module command.
 */
public class InfoCommand extends Command {
    public InfoCommand() {
        super("expoinfo");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String @NotNull [] args) {
        PluginDescriptionFile description = JavaPlugin.getPlugin(ExpoBase.class).getDescription();

        sender.sendMessage(Component.text(description.getName() + " v" + description.getVersion()));
        if (description.getDescription() != null && !description.getDescription().isBlank()) {
            sender.sendMessage(Component.text(description.getDescription()));
        }

        List<String> authors = description.getAuthors();
        if (!authors.isEmpty()) {
            sender.sendMessage(Component.text("Authors: " + String.join(", ", authors)));
        }

        List<String> moduleNames = ExpoBase.getRegisteredModules().stream()
                .map(ExpoModule::getName)
                .sorted(Comparator.naturalOrder())
                .toList();

        if (moduleNames.isEmpty()) {
            sender.sendMessage(Component.text("No modules are currently registered."));
        } else {
            sender.sendMessage(Component.text("Registered modules (" + moduleNames.size() + "): "
                    + String.join(", ", moduleNames)));
        }

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
