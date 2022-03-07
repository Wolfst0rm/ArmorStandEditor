package io.github.rypofalem.armorstandeditor.protections;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GriefPreventionProtection {

    private boolean gpEnabled;
    private GriefPrevention griefPrevention = null;
    private Material blockType = null;

    public GriefPreventionProtection(){
        gpEnabled = Bukkit.getPluginManager().isPluginEnabled("GriefPrevention");

        if(!gpEnabled) return;
        griefPrevention = (GriefPrevention) Bukkit.getPluginManager().getPlugin("GriefPrevention");
    }

    public boolean checkPermission(Block block, Player player){
        if(!gpEnabled) return true;

        Claim landClaim = griefPrevention.dataStore.getClaimAt(block.getLocation(), false, null);
        blockType = block.getType();

        if(landClaim != null && landClaim.allowBuild(player, blockType) != null){
            player.sendMessage(ChatColor.RED + landClaim.allowBuild(player, blockType));
            return false;
        }
        return true;
    }
}
