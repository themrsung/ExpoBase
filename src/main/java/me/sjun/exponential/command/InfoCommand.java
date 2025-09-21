package me.sjun.exponential.command;

import me.sjun.exponential.ExpoBase;
import me.sjun.exponential.ExpoModule;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * Display plugin information.
 */
public class InfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
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
}
