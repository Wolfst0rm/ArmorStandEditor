package io.github.rypofalem.armorstandeditor.protections;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;


import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class WorldGuardProtection {
    private boolean wgEnabled;
    private RegionQuery regionQry = null;

    public WorldGuardProtection(){
        wgEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");

        if(!wgEnabled) return;
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        regionQry = regionContainer.createQuery();
    }

    public boolean checkPermission(Block block, Player player){
        if (!wgEnabled) {
            return true;
        }

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        return regionQry.testState(BukkitAdapter.adapt(block.getLocation()), localPlayer, Flags.BUILD);
    }
}
