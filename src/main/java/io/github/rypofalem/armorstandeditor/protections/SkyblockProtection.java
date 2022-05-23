package io.github.rypofalem.armorstandeditor.protections;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SkyblockProtection {
    private final boolean skyblockEnabled;

    public SkyblockProtection(){
        //NOTE FROM AUTHOR: I know there are many plugins that have Skyblock. I am using SuperiorSkyBlock2 as an Example!
        //IF YOU WANT YOUR SKYBLOCK ADDED, PLEASE SUBMIT A FEATURE REQUEST!

        skyblockEnabled = Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2");
        if (!skyblockEnabled) return;
    }

    public boolean checkPermission(Player player){
        if(!skyblockEnabled) return true;
        if(player.isOp()) return true;
        if(player.hasPermission("asedit.ignoreProtection.skyblock") || SuperiorSkyblockAPI.getPlayer(player).hasBypassModeEnabled()) return true; //Add Additional Permission

        SuperiorPlayer sp = SuperiorSkyblockAPI.getPlayer(player);

        //GET ISLAND FOR A GIVEN LOCATION
        Island island = SuperiorSkyblockAPI.getIslandAt(sp.getLocation());

        if(!island.isMember(sp) && !island.isCoop(sp)){
            return false;
        } else {
            return true;
        }
    }
}
