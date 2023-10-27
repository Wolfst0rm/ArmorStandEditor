/*
 * ArmorStandEditor: Bukkit plugin to allow editing armor stand attributes
 * Copyright (C) 2016-2023  RypoFalem
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.github.rypofalem.armorstandeditor.protections;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SkyblockProtection implements Protection {
    private final boolean skyblockEnabled;

    public SkyblockProtection() {
        //NOTE FROM AUTHOR: I know there are many plugins that have Skyblock. I am using SuperiorSkyBlock2 as an Example!
        //IF YOU WANT YOUR SKYBLOCK ADDED, PLEASE SUBMIT A FEATURE REQUEST!

        skyblockEnabled = Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2");
        if (!skyblockEnabled) return;
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
            return island.isMember(sp) || island.isCoop(sp) || sp.hasBypassModeEnabled();
        }
    }
}
