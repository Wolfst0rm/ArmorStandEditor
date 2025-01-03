package io.github.rypofalem.armorstandeditor;

import io.rypofalem.armorstandeditor.ArmorStandEditorPlugin;

import java.util.logging.Level;

public class Debug {

    boolean debugTurnedOn;
    private final ArmorStandEditorPlugin plugin;
    String debugOutputToConsole;

    public Debug(ArmorStandEditorPlugin plugin) {
        this.plugin = plugin;
    }

    public void log(String msg) {
        debugTurnedOn = plugin.isDebug();
        if (!debugTurnedOn) return;

        debugOutputToConsole = String.format("[ArmorStandEditor-Debug] %s", msg);
        plugin.getServer().getLogger().log(Level.INFO, debugOutputToConsole);
    }
}