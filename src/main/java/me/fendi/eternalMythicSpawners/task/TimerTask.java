package me.fendi.eternalMythicSpawners.task;

import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import me.fendi.eternalMythicSpawners.config.ConfigService;
import me.fendi.eternalMythicSpawners.config.TimerConfig;
import me.fendi.eternalMythicSpawners.hologram.HologramService;
import me.fendi.eternalMythicSpawners.spawner.SpawnerRegistry;
import me.fendi.eternalMythicSpawners.spawner.TrackedSpawner;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class TimerTask implements Runnable {

    private final ConfigService configService;
    private final SpawnerRegistry spawnerRegistry;
    private final HologramService hologramService;
    private final Logger logger;
    private BukkitTask task;

    public TimerTask(
            ConfigService configService,
            SpawnerRegistry spawnerRegistry,
            HologramService hologramService,
            Logger logger
    ) {
        this.configService = configService;
        this.spawnerRegistry = spawnerRegistry;
        this.hologramService = hologramService;
        this.logger = logger;
    }

    public void start(Plugin plugin) {
        stop();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this, 20L, 20L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void run() {
        TimerConfig config = configService.current();
        for (TrackedSpawner trackedSpawner : spawnerRegistry.trackedSpawners()) {
            updateSpawner(trackedSpawner, config);
        }
    }

    private void updateSpawner(TrackedSpawner trackedSpawner, TimerConfig config) {
        try {
            MythicSpawner spawner = trackedSpawner.spawner();
            if (hasReachedMobLimit(spawner)) {
                hologramService.hide(trackedSpawner.spawnerName());
                return;
            }

            hologramService.showOrUpdate(trackedSpawner, config, remainingSeconds(spawner));
        } catch (RuntimeException exception) {
            logger.log(Level.WARNING, "Failed to update timer hologram for spawner '" + trackedSpawner.spawnerName() + "'.", exception);
        }
    }

    private boolean hasReachedMobLimit(MythicSpawner spawner) {
        int maxMobs = spawner.getMaxMobs().get();
        return maxMobs > 0 && spawner.getNumberOfMobs() >= maxMobs;
    }

    private int remainingSeconds(MythicSpawner spawner) {
        int warmup = spawner.getRemainingWarmupSeconds();
        if (warmup > 0) {
            return warmup;
        }
        return Math.max(0, spawner.getRemainingCooldownSeconds());
    }
}
