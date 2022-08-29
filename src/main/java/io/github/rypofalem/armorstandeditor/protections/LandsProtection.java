package io.github.rypofalem.armorstandeditor.protections;

import io.github.rypofalem.armorstandeditor.ArmorStandEditorPlugin;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LandsProtection implements Protection {
    private final boolean landsEnabled;
    private LandsIntegration lands;

    public LandsProtection() {
        landsEnabled = Bukkit.getPluginManager().isPluginEnabled("Lands");

        if (landsEnabled)
            lands = new LandsIntegration(ArmorStandEditorPlugin.instance());
    }

    @Override
    public boolean checkPermission(Block block, Player player) {
        if (!landsEnabled || player.hasPermission("asedit.ignoreProtection.lands")) return true;

        // Check if the player is trusted in the area or land, in case they're not in an area, they're in.
        Land land = lands.getLand(block.getLocation());
        Area area = land == null ? null : land.getArea(block.getLocation());
        LandPlayer lPlayer = lands.getLandPlayer(player.getUniqueId());
        return area == null ? land == null || land.isTrusted(lPlayer) : area.isTrusted(player.getUniqueId());
    }
}
