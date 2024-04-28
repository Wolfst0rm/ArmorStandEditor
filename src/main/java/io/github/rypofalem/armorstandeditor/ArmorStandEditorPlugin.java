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

package io.github.rypofalem.armorstandeditor;

import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ArmorStandEditorPlugin extends JavaPlugin {

    PluginDescriptionFile pdfFile = getDescription();

    //Server Versioning - Spigot Detection and Derivatives
    boolean hasPaper;
    boolean hasFolia;
    boolean hasSpigot;
    String minecraftVersion = "";

    //Scoreboards
    Scoreboard scoreboard;
    final String ASE_LOCKED_TEAM = "ASE_LOCKED";
    Team team;

    //Config related Items
    NamedTextColor color;


    @Override
    public void onEnable() {

        //Load Messages in Console
        getLogger().info("======= ArmorStandEditor =======");
        getLogger().info("Plugin Version: " + pdfFile.getVersion()); //Can be replaced with getConfig().getString("version"));

        //Get Minecraft Server Software Version
        hasPaper = isPaper(); //io.papermc.paper.configuration.Configuration
        hasFolia = isFolia(); //io.papermc.paper.threadedregions.RegionizedServer
        hasSpigot = isSpigot(); //com.spigot.CustomTimingsHandler (per API Docs)
        logServerSoftware();

        //Log Minecraft Version and Any Warnings to go with it
        getLogger().info("Minecraft Version: " + minecraftVersion);
        logSpecificWarningsMinecraftVersion();
        getServer().getPluginManager().enablePlugin(this);

        this.loadConfig();
        loadASEScoreboards(color);

    }

    private void loadConfig() {

    }

    private void loadASEScoreboards(NamedTextColor color) {
        if(!hasFolia){
            scoreboard = getServer().getScoreboardManager().getMainScoreboard();
            getLogger().info("Registering ASE Locked Scoreboard - Required for Glowing Color Support");

            if(scoreboard.getTeam(ASE_LOCKED_TEAM) == null){
                scoreboard.registerNewTeam(ASE_LOCKED_TEAM);
                scoreboard.getTeam(ASE_LOCKED_TEAM).color(color);
            } else{
                getLogger().info("Scoreboard Already Exists. Continuing Load.");
            }
        } else{
            getLogger().warning("Scoreboards do not work with Folia currently.");
        }
    }

    private void unloadASEScoreboards(){
        getLogger().info("Removing Scoreboards required for Glowing Effects");

        team = scoreboard.getTeam(ASE_LOCKED_TEAM);
        if (team != null) { //Basic Sanity Check to ensure that the team is there
            team.unregister();
        } else {
            getLogger().severe("Team Already Appears to be removed. Please do not do this manually!");
        }
    }

    private void logServerSoftware() {
        if(hasPaper || hasFolia){
            minecraftVersion = getServer().getMinecraftVersion(); // Returns 1.MAJOR.MINOR
            getLogger().info("Paper/Folia: True");
        } else if (hasSpigot){
            minecraftVersion = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]; // Returns v1_20_R04
            getLogger().info("Spigot: True");
        } else {
            getLogger().warning("ArmorStandEditor requires either Paper, Spigot or one of its forks to run. This is not an error, please do not report this! ");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void logSpecificWarningsMinecraftVersion() {
        if(hasPaper || hasFolia){
            if(minecraftVersion.contains("1.17") || minecraftVersion.contains("1.18") || minecraftVersion.contains("1.19")){
                getLogger().warning("ArmorStandEditor is compatible with this version of Minecraft, but it is not the latest supported version.");
                getLogger().warning("Loading continuing, but please consider updating to the latest version.");
            } else if (minecraftVersion.contains("1.20")) {
                //Do Nothing
            } else {
                getLogger().warning("ArmorStandEditor is not compatible with this version of Minecraft. Please update to at least version 1.17. Loading failed.");
                getServer().getPluginManager().disablePlugin(this);
            }
        } else {
            if(minecraftVersion.contains("v1_20")){
                //Do Nothing
            } else if (minecraftVersion.contains("v1_19") || minecraftVersion.contains("v1_18") || minecraftVersion.contains("v1_17") ){
                getLogger().warning("ArmorStandEditor is compatible with this version of Minecraft, but it is not the latest supported version.");
                getLogger().warning("Loading continuing, but please consider updating to the latest version.");
            } else{
                getLogger().warning("ArmorStandEditor is not compatible with this version of Minecraft. Please update to at least version 1.17. Loading failed.");
                getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    @Deprecated
    // "Spigot will eventually be no longer supported by ASE
    private static boolean isSpigot() {
        try{
            Class.forName("com.spigotmc.CustomTimingsHandler");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    private static boolean isPaper(){
        try{
            Class.forName("io.papermc.paper.configuration.Configuration");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    @Override
    public void onDisable() {

        unloadASEScoreboards();
    }

}
