package me.fendi.eternalMythicSpawners.listener;

import me.fendi.eternalMythicSpawners.EternalMythicSpawners;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public final class ServerLoadListener implements Listener {

    private final EternalMythicSpawners plugin;

    public ServerLoadListener(EternalMythicSpawners plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        plugin.rebuildAfterServerLoad();
    }
}
