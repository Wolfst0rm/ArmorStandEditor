package io.github.rypofalem.armorstandeditor;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import io.github.rypofalem.armorstandeditor.util.Util;

import java.util.HashMap;
import java.util.UUID;

public class PlayerEditorManager implements Listener {
    private ArmorStandEditorPlugin plugin;
    private HashMap<UUID, PlayerEditor> players;
    double coarseAdj;
    double fineAdj;
    double coarseMov;
    double fineMov;
    private boolean ignoreNextInteract = false;
    private TickCounter counter;

    PlayerEditorManager(final ArmorStandEditorPlugin plugin) {
        this.plugin = plugin;
        players = new HashMap<>();
        coarseAdj = Util.FULL_CIRCLE / plugin.getCoarseConfig();
        fineAdj = Util.FULL_CIRCLE / plugin.getFineConfig();
        coarseMov = 1;
        fineMov = .03125; // 1/32
        counter = new TickCounter();
        Bukkit.getServer().getScheduler().runTaskTimer(plugin, counter, 0, 1);
    }

    class TickCounter implements Runnable {
        long ticks = 0; //I am optimistic

        @Override
        public void run() {
            ticks++;
        }

        public long getTime() {
            return ticks;
        }
    }
}
