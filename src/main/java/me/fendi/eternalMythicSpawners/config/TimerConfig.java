package me.fendi.eternalMythicSpawners.config;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public record TimerConfig(
        Map<String, MobTimerSettings> mobSettings,
        String mobTypeMessage,
        String spawningMessage,
        String spawnInMessage
) {

    public TimerConfig {
        mobSettings = Map.copyOf(mobSettings);
    }

    public Optional<MobTimerSettings> getMobSettings(String mobType) {
        if (mobType == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(mobSettings.get(mobType.toLowerCase(Locale.ROOT)));
    }
}
