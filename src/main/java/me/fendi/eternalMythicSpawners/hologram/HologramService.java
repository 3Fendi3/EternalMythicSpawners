package me.fendi.eternalMythicSpawners.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.fendi.eternalMythicSpawners.config.TimerConfig;
import me.fendi.eternalMythicSpawners.spawner.TrackedSpawner;
import me.fendi.eternalMythicSpawners.util.MessageFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HologramService {

    private static final String HOLOGRAM_PREFIX = "ems-spawner-";

    private final Logger logger;
    private final Map<String, HologramState> hologramsBySpawner = new HashMap<>();

    public HologramService(Logger logger) {
        this.logger = logger;
    }

    public void showOrUpdate(TrackedSpawner trackedSpawner, TimerConfig config, int secondsUntilSpawn) {
        List<String> lines = renderLines(trackedSpawner, config, secondsUntilSpawn);
        HologramState state = hologramsBySpawner.get(trackedSpawner.spawnerName());

        if (state == null) {
            create(trackedSpawner, lines);
            return;
        }

        updateChangedLines(state, lines);
    }

    public void hide(String spawnerName) {
        HologramState state = hologramsBySpawner.remove(spawnerName);
        if (state == null) {
            return;
        }

        try {
            DHAPI.removeHologram(state.hologramName());
        } catch (RuntimeException exception) {
            logger.log(Level.WARNING, "Failed to remove hologram '" + state.hologramName() + "'.", exception);
        }
    }

    public void hideAll() {
        List<String> spawnerNames = List.copyOf(hologramsBySpawner.keySet());
        for (String spawnerName : spawnerNames) {
            hide(spawnerName);
        }
    }

    private void create(TrackedSpawner trackedSpawner, List<String> lines) {
        String hologramName = hologramName(trackedSpawner.spawnerName());
        try {
            putState(trackedSpawner, hologramName, lines);
        } catch (IllegalArgumentException exception) {
            try {
                DHAPI.removeHologram(hologramName);
                putState(trackedSpawner, hologramName, lines);
            } catch (RuntimeException retryException) {
                logger.log(Level.WARNING, "Failed to create hologram '" + hologramName + "'.", retryException);
            }
        }
    }

    private void putState(TrackedSpawner trackedSpawner, String hologramName, List<String> lines) {
        Hologram hologram = DHAPI.createHologram(hologramName, trackedSpawner.hologramLocation(), false, lines);
        hologramsBySpawner.put(trackedSpawner.spawnerName(), new HologramState(hologramName, hologram, lines));
    }

    private void updateChangedLines(HologramState state, List<String> lines) {
        List<String> previousLines = state.lines();
        int lineCount = Math.min(previousLines.size(), lines.size());
        for (int index = 0; index < lineCount; index++) {
            String newLine = lines.get(index);
            if (!newLine.equals(previousLines.get(index))) {
                DHAPI.setHologramLine(state.hologram(), index, newLine);
            }
        }
        state.setLines(lines);
    }

    private List<String> renderLines(TrackedSpawner trackedSpawner, TimerConfig config, int secondsUntilSpawn) {
        String timerLine = secondsUntilSpawn <= 0
                ? MessageFormatter.colorize(config.spawningMessage())
                : MessageFormatter.renderTimerLine(config.spawnInMessage(), secondsUntilSpawn);

        return List.of(
                MessageFormatter.renderMobTypeLine(config.mobTypeMessage(), trackedSpawner.displayName()),
                timerLine
        );
    }

    private String hologramName(String spawnerName) {
        String normalized = spawnerName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]", "_");
        return HOLOGRAM_PREFIX + normalized + "-" + Integer.toHexString(spawnerName.hashCode());
    }

    private static final class HologramState {
        private final String hologramName;
        private final Hologram hologram;
        private List<String> lines;

        private HologramState(String hologramName, Hologram hologram, List<String> lines) {
            this.hologramName = hologramName;
            this.hologram = hologram;
            this.lines = List.copyOf(lines);
        }

        private String hologramName() {
            return hologramName;
        }

        private Hologram hologram() {
            return hologram;
        }

        private List<String> lines() {
            return lines;
        }

        private void setLines(List<String> lines) {
            this.lines = List.copyOf(lines);
        }
    }
}
