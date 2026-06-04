package me.fendi.eternalMythicSpawners.spawner;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import me.fendi.eternalMythicSpawners.config.MobTimerSettings;
import me.fendi.eternalMythicSpawners.config.TimerConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class SpawnerRegistry {

    private static final Pattern MINIMESSAGE_TAGS = Pattern.compile("<[^>]+>");

    private final Logger logger;
    private List<TrackedSpawner> trackedSpawners = List.of();

    public SpawnerRegistry(Logger logger) {
        this.logger = logger;
    }

    public void rebuild(TimerConfig config) {
        Collection<MythicSpawner> mythicSpawners = MythicBukkit.inst().getSpawnerManager().getSpawners();
        List<TrackedSpawner> rebuilt = new ArrayList<>();

        for (MythicSpawner spawner : mythicSpawners) {
            Optional<MobTimerSettings> settings = config.getMobSettings(spawner.getTypeName());
            if (settings.isEmpty()) {
                continue;
            }

            Location hologramLocation = toHologramLocation(spawner, settings.get());
            if (hologramLocation == null) {
                logger.warning("Skipping MythicSpawner '" + spawner.getName() + "' because its world is not loaded.");
                continue;
            }

            rebuilt.add(new TrackedSpawner(
                    spawner,
                    spawner.getName(),
                    spawner.getTypeName(),
                    resolveDisplayName(spawner.getTypeName()),
                    settings.get(),
                    hologramLocation
            ));
        }

        trackedSpawners = List.copyOf(rebuilt);
        logger.info("Cached " + trackedSpawners.size() + " MythicMobs spawners for hologram timers.");
    }

    public List<TrackedSpawner> trackedSpawners() {
        return trackedSpawners;
    }

    private Location toHologramLocation(MythicSpawner spawner, MobTimerSettings settings) {
        AbstractLocation location = spawner.getLocation();
        String worldName = location.getWorld().getName();
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }

        return new Location(
                world,
                location.getBlockX() + 0.5D + settings.offsetX(),
                location.getBlockY() + 0.5D + settings.offsetY(),
                location.getBlockZ() + 0.5D + settings.offsetZ()
        );
    }

    private String resolveDisplayName(String mobType) {
        Optional<MythicMob> mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(mobType);
        if (mythicMob.isEmpty()) {
            return mobType;
        }

        PlaceholderString displayName = mythicMob.get().getDisplayName();
        if (displayName == null) {
            return mobType;
        }

        return stripMiniMessageTags(displayName.get());
    }

    private String stripMiniMessageTags(String text) {
        if (text == null) {
            return "";
        }
        return MINIMESSAGE_TAGS.matcher(text).replaceAll("");
    }
}
