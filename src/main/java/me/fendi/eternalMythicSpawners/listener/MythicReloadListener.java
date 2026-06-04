package me.fendi.eternalMythicSpawners.listener;

import io.lumine.mythic.bukkit.events.MythicReloadCompleteEvent;
import me.fendi.eternalMythicSpawners.EternalMythicSpawners;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class MythicReloadListener implements Listener {

    private final EternalMythicSpawners plugin;

    public MythicReloadListener(EternalMythicSpawners plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMythicReloadComplete(MythicReloadCompleteEvent event) {
        plugin.rebuildAfterMythicReload();
    }
}
