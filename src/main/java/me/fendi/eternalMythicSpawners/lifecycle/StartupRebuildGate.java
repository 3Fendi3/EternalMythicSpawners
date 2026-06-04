package me.fendi.eternalMythicSpawners.lifecycle;

public final class StartupRebuildGate {

    private boolean completed;

    public synchronized boolean runOnce(Runnable rebuildAction) {
        if (completed) {
            return false;
        }

        completed = true;
        rebuildAction.run();
        return true;
    }
}
