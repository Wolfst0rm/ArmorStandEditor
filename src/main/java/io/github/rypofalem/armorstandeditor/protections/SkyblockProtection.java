package io.github.rypofalem.armorstandeditor.protections;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SkyblockProtection implements Protection  {
    private final boolean skyblockEnabled;

    public SkyblockProtection(){
        //NOTE FROM AUTHOR: I know there are many plugins that have Skyblock. I am using SuperiorSkyBlock2 as an Example!
        //IF YOU WANT YOUR SKYBLOCK ADDED, PLEASE SUBMIT A FEATURE REQUEST!

        skyblockEnabled = Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2");
    }

    public boolean checkPermission(Block block, Player player) {
        if (!skyblockEnabled) return true;
        if (player.isOp()) return true;
        if (player.hasPermission("asedit.ignoreProtection.skyblock")) return true; //Add Additional Permission

        SuperiorPlayer sp = SuperiorSkyblockAPI.getPlayer(player);

        //GET ISLAND FOR A GIVEN LOCATION
        Island island = SuperiorSkyblockAPI.getIslandAt(sp.getLocation());
        if (island == null) {
            return true;
        } else {
            if (!island.isMember(sp) && !island.isCoop(sp) && !sp.hasBypassModeEnabled()) {
                return false;
            } else {
                return true;
            }
        }
    }
}
