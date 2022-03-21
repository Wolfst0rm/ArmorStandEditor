package io.github.rypofalem.armorstandeditor.protections;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.executors.TownyActionEventExecutor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

//FIX for https://github.com/Wolfieheart/ArmorStandEditor-Issues/issues/15
public class TownyProtection {
    private final boolean tEnabled;


    public TownyProtection(){
        tEnabled = Bukkit.getPluginManager().isPluginEnabled("Towny");
        if (!tEnabled) return;
    }

    public boolean checkPermission(Block block, Player player){
        if(!tEnabled) return true;
        if(player.isOp()) return true;

        Location playerLoc = player.getLocation();

        if (TownyAPI.getInstance().isWilderness(playerLoc)) return false;
        if (!TownyActionEventExecutor.canDestroy(player, block.getLocation(), Material.ARMOR_STAND)) return false;

        return true;
    }
}

