package me.fendi.eternalMythicSpawners.command;

import me.fendi.eternalMythicSpawners.EternalMythicSpawners;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public final class EternalMythicSpawnersCommand implements CommandExecutor, TabCompleter {

    private static final String ADMIN_PERMISSION = "eternalmythicspawners.admin";

    private final EternalMythicSpawners plugin;

    public EternalMythicSpawnersCommand(EternalMythicSpawners plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(ADMIN_PERMISSION)) {
                sender.sendMessage("§cNie masz permisji.");
                return true;
            }

            plugin.reloadPlugin();
            sender.sendMessage("§aEternalMythicSpawners przeladowany.");
            return true;
        }

        sender.sendMessage("§e/" + label + " reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission(ADMIN_PERMISSION)) {
            return List.of("reload");
        }
        return List.of();
    }
}
