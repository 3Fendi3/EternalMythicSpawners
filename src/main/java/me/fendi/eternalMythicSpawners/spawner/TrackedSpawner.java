package me.fendi.eternalMythicSpawners.spawner;

import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import me.fendi.eternalMythicSpawners.config.MobTimerSettings;
import org.bukkit.Location;

public record TrackedSpawner(
        MythicSpawner spawner,
        String spawnerName,
        String mobType,
        String displayName,
        MobTimerSettings settings,
        Location hologramLocation
) {
}
