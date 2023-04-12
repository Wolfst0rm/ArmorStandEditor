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

import com.plotsquared.bukkit.BukkitPlatform;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.location.Location;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlotSquaredProtection implements Protection  {

    private final boolean psEnabled;
    private BukkitPlatform psPlatform = null;

    public PlotSquaredProtection(){
        psEnabled = Bukkit.getPluginManager().isPluginEnabled("PlotSquared");

        if (!psEnabled) return;
        psPlatform = (BukkitPlatform) Bukkit.getPluginManager().getPlugin("PlotSquared");
    }

    public boolean checkPermission(Block block, Player player){
        if(!psEnabled) return true;
        if(player.isOp()) return true;
        if(player.hasPermission("asedit.ignoreProtection.plotSquared")) return true;

        Location location = Location.at(block.getWorld().getName(),
                block.getLocation().getBlockX(),
                block.getLocation().getBlockY(),
                block.getLocation().getBlockZ());

        PlotArea area = psPlatform.plotAreaManager().getPlotArea(location);
        if(area == null) return true;
        Plot plot = area.getPlot(location);
        return plot == null || plot.isAdded(player.getUniqueId());
    }
}
