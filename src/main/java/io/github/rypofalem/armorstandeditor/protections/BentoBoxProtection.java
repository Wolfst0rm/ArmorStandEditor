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

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;
import world.bentobox.bentobox.managers.AddonsManager;
import world.bentobox.bentobox.managers.IslandsManager;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Optional;

public class BentoBoxProtection implements Protection {

    private final boolean bentoEnabled;


    public BentoBoxProtection() {
        bentoEnabled = Bukkit.getPluginManager().isPluginEnabled("BentoBox");
    }

    @Override
    public boolean checkPermission(Block block, Player player) {
        if (!bentoEnabled || player.isOp() ||
            player.hasPermission("asedit.ignoreProtection.bentobox") ||
            player.hasPermission("bentobox.admin")) return true;

        //Get the Bento Instance
        BentoBox myBento = BentoBox.getInstance();
        if (myBento == null) return true;

        //Get the Various Managers for Bentobox
        IslandsManager islandsManager = myBento.getIslandsManager();
        AddonsManager addonsManager = myBento.getAddonsManager();

        //Check first if BSkyblock is enabled or if the Player is Owner of that Island
        if (addonsManager.getAddonByName("BSkyblock").isEmpty()) return true;

        //Get the Location of the ArmorStand
        Optional<Island> islandOptional = islandsManager.getIslandAt(block.getLocation());

        //If there are no Islands Present
        if (islandOptional.isEmpty()) return true;

        //Do not run this check if the player is the owner of the island
        if (islandsManager.isOwner(player.getWorld(), player.getUniqueId())) return true;

        //Get the Island from the Island Optional
        Island theIsland = islandOptional.get();

        //Return if that User isAllowed to break blocks on that Land
        return theIsland.isAllowed(User.getInstance(player), Flags.BREAK_BLOCKS);
    }
}
