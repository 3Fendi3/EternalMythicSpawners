package me.fendi.eternalMythicSpawners;

import me.fendi.eternalMythicSpawners.command.EternalMythicSpawnersCommand;
import me.fendi.eternalMythicSpawners.config.ConfigService;
import me.fendi.eternalMythicSpawners.config.TimerConfig;
import me.fendi.eternalMythicSpawners.hologram.HologramService;
import me.fendi.eternalMythicSpawners.lifecycle.StartupRebuildGate;
import me.fendi.eternalMythicSpawners.listener.MythicReloadListener;
import me.fendi.eternalMythicSpawners.listener.ServerLoadListener;
import me.fendi.eternalMythicSpawners.spawner.SpawnerRegistry;
import me.fendi.eternalMythicSpawners.task.TimerTask;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class EternalMythicSpawners extends JavaPlugin {

    private ConfigService configService;
    private SpawnerRegistry spawnerRegistry;
    private HologramService hologramService;
    private TimerTask timerTask;
    private StartupRebuildGate startupRebuildGate;
    private BukkitTask startupRebuildFallbackTask;

    @Override
    public void onEnable() {
        startupRebuildGate = new StartupRebuildGate();
        configService = new ConfigService(this);
        configService.loadDefaultConfig();

        spawnerRegistry = new SpawnerRegistry(getLogger());
        hologramService = new HologramService(getLogger());

        timerTask = new TimerTask(configService, spawnerRegistry, hologramService, getLogger());
        timerTask.start(this);

        EternalMythicSpawnersCommand commandExecutor = new EternalMythicSpawnersCommand(this);
        PluginCommand command = getCommand("eternalmythicspawners");
        if (command != null) {
            command.setExecutor(commandExecutor);
            command.setTabCompleter(commandExecutor);
        }

        getServer().getPluginManager().registerEvents(new MythicReloadListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerLoadListener(this), this);
        scheduleStartupRebuildFallback();
    }

    @Override
    public void onDisable() {
        cancelStartupRebuildFallback();
        if (timerTask != null) {
            timerTask.stop();
        }
        if (hologramService != null) {
            hologramService.hideAll();
        }
    }

    public void reloadPlugin() {
        hologramService.hideAll();
        TimerConfig config = configService.reload();
        spawnerRegistry.rebuild(config);
    }

    public void rebuildAfterServerLoad() {
        if (startupRebuildGate.runOnce(() -> {
            hologramService.hideAll();
            spawnerRegistry.rebuild(configService.current());
        })) {
            cancelStartupRebuildFallback();
        }
    }

    public void rebuildAfterMythicReload() {
        hologramService.hideAll();
        spawnerRegistry.rebuild(configService.current());
    }

    private void scheduleStartupRebuildFallback() {
        startupRebuildFallbackTask = getServer().getScheduler().runTaskLater(this, this::rebuildAfterServerLoad, 40L);
    }

    private void cancelStartupRebuildFallback() {
        if (startupRebuildFallbackTask != null) {
            startupRebuildFallbackTask.cancel();
            startupRebuildFallbackTask = null;
        }
    }
}
