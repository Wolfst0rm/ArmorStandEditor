package io.github.rypofalem.armorstandeditor;

import io.rypofalem.armorstandeditor.ArmorStandEditorPlugin;

import java.util.logging.Level;

public class Debug {

    private boolean debugTurnedOn;
    private ArmorStandEditorPlugin plugin;
    String debugOutputToConsole;

    public Debug(ArmorStandEditorPlugin plugin) {
        this.plugin = plugin;
    }

    public void log(String msg) {
        debugTurnedOn = plugin.isDebug();
        if (!debugTurnedOn) return;

        debugOutputToConsole = String.format("[ArmorStandEditor-Debug] " + msg);
        plugin.getServer().getLogger().log(Level.INFO, debugOutputToConsole);
    }
}