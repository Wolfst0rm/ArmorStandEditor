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

import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlotSquaredProtection implements Protection {

    private final boolean psEnabled;
    private PlotAPI plotAPI;

    public PlotSquaredProtection() {
        psEnabled = Bukkit.getPluginManager().isPluginEnabled("PlotSquared");

        if (!psEnabled) return;
    }

    public boolean checkPermission(Block block, Player player) {
        if (!psEnabled) return true;
        if (player.isOp()) return true;
        if (player.hasPermission("asedit.ignoreProtection.plotSquared")) return true;
        if (plotAPI == null) plotAPI = new PlotAPI();

        //Get the Location of the Plot
        Location plotLocation = Location.at(block.getWorld().getName(), BlockVector3.at(block.getX(), block.getY(), block.getZ()));

        //Get the Area of the PLot
        PlotArea area = plotLocation.getPlotArea();

        //If the Area is not a Plot, then we assume its a road, we return if a player can build on roads or not
        if (area == null) return true;

        //Get the Plot
        Plot plot = area.getPlot(plotLocation);

        //Rerun the Area check
        if (plot == null)
            return player.hasPermission("plots.admin.build.road");

        //Get the Player
        PlotPlayer<?> plotPlayer = plotAPI.wrapPlayer(player.getUniqueId());

        if (plotPlayer == null) return true;

        //Get the UUID of the PlotPlayer
        UUID uuid = plotPlayer.getUUID();

        //Return if they are added to the plot or if they are OP and have the Permission to build anywhere
        return plot.isAdded(uuid) || plotPlayer.hasPermission("plots.admin.build.other");

    }
}
