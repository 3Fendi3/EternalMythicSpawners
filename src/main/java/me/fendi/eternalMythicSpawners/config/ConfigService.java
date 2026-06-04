package me.fendi.eternalMythicSpawners.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class ConfigService {

    private static final String DEFAULT_MOB_TYPE_MESSAGE = "<#008bff>&l%mt%";
    private static final String DEFAULT_SPAWNING_MESSAGE = "&7Spawning...";
    private static final String DEFAULT_SPAWN_IN_MESSAGE = "&7Spawn in &l%sec%&r&7 seconds";

    private final JavaPlugin plugin;
    private TimerConfig current;

    public ConfigService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.current = parse(plugin.getConfig());
    }

    public TimerConfig loadDefaultConfig() {
        plugin.saveDefaultConfig();
        current = parse(plugin.getConfig());
        return current;
    }

    public TimerConfig reload() {
        plugin.reloadConfig();
        current = parse(plugin.getConfig());
        return current;
    }

    public TimerConfig current() {
        return current;
    }

    public static TimerConfig parse(FileConfiguration config) {
        Map<String, MobTimerSettings> mobSettings = new LinkedHashMap<>();
        ConfigurationSection mobTypes = config.getConfigurationSection("list-mobs-type");
        if (mobTypes != null) {
            for (String mobType : mobTypes.getKeys(false)) {
                ConfigurationSection section = mobTypes.getConfigurationSection(mobType);
                if (section == null || !section.getBoolean("show-holo", false)) {
                    continue;
                }

                MobTimerSettings settings = new MobTimerSettings(
                        mobType,
                        section.getDouble("offset-holo.x", 0.0D),
                        section.getDouble("offset-holo.y", 2.5D),
                        section.getDouble("offset-holo.z", 0.0D)
                );
                mobSettings.put(mobType.toLowerCase(Locale.ROOT), settings);
            }
        }

        return new TimerConfig(
                mobSettings,
                stringOrDefault(config, "holo-message-mobtype", DEFAULT_MOB_TYPE_MESSAGE),
                stringOrDefault(config, "holo-message-spawning", DEFAULT_SPAWNING_MESSAGE),
                stringOrDefault(config, "holo-message-spawn-in", DEFAULT_SPAWN_IN_MESSAGE)
        );
    }

    private static String stringOrDefault(FileConfiguration config, String path, String fallback) {
        String value = config.getString(path);
        return value == null ? fallback : value;
    }
}
