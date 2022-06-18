package io.github.rypofalem.armorstandeditor.protections;

import com.plotsquared.bukkit.BukkitPlatform;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.location.Location;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlotSquaredProtection {

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
