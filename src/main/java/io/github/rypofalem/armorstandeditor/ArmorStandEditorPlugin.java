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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.logging.Level;

public class ArmorStandEditorPlugin extends JavaPlugin {


    //Server Versioning - Spigot Detection and Derivatives
    boolean hasPaper;
    boolean hasPurpur;
    boolean hasFolia;
    boolean hasSpigot;

    //Plugin Loading Messages
    String asePluginVersion = "";
    String minecraftVersion = "";
    String minecraftVersionWarning = "";
    static final String SEPERATOR_LOG = "================================";

    //Scoreboard related settings
    public Scoreboard scoreboard;
    public Team team;
    String lockedTeamName = "ASE_LOCKED_TEAM";
    NamedTextColor colorOfLockedTeam;

    // ASE-R Configuration Options

    // -- Tool Data
    String toolMaterial;
    Material editToolMaterial;
    boolean requireToolName;
    String editToolName;
    boolean requireToolData;
    int editToolData;
    boolean requireToolLore;
    List<?> editToolLore;

    // -- Armor Stand Specifics
    double coarseRot;
    double fineRot;
    boolean armorStandVisibility;
    boolean itemFrameVisibility;

    // -- Other Features
    boolean itemFrameGlowing;


    @Override
    public void onEnable() {

        //First Log the Plugin Version
        asePluginVersion = getConfig().getString("version");

        getLogger().info("======= ArmorStandEditor =======");
        getLogger().log(Level.INFO, "Plugin Version: {0}", asePluginVersion);

        //Get Minecraft Server Software Version + Log to Console
        hasPaper = isPaper();   //io.papermc.paper.configuration.Configuration
        hasFolia = isFolia();   //io.papermc.paper.threadedregions.RegionizedServer -- Could be removed in the future. Not Depreciated...
        hasPurpur = isPurpur(); //org.purpurmc.purpur.event.PlayerAFKEvent
        hasSpigot = isSpigot(); //com.spigot.CustomTimingsHandler (per API Docs) -- Depreciated for now....

        // First Potential Disabling Point. Must be Spigot/Paper/Purpur or Folia to load ASE.
        logServerSoftwareVersionToConsole(hasPaper, hasFolia, hasPurpur, hasSpigot);

        // Minecraft Versioning. Final Potential Disable Point. Must be on 1.17/V1_17 or Higher to Load ASE.
        minecraftVersion = gatherMinecraftServerVersionInfo();
        getLogger().log(Level.INFO, "Minecraft Version: {0}", minecraftVersion);

        if (hasPurpur || hasPaper || hasFolia){
            if(minecraftVersion.compareTo("1.20") < 0){
                getLogger().log(Level.WARNING, "ArmorStandEditor is compatible with this version of Minecraft, but it is not the latest supported version.");
                getLogger().log(Level.WARNING, "Load Continuing, but some features may not work.");
            } else if (minecraftVersion.compareTo("1.17") < 0 ) {
                getLogger().log(Level.SEVERE, "ArmorStandEditor is not compatible with this version of Minecraft.");
                getLogger().log(Level.SEVERE, "Please Update to a version past 1.17. Loading Failed.");
                getLogger().log(Level.INFO, SEPERATOR_LOG);
                getServer().getPluginManager().disablePlugin(this);
            } else{
                getLogger().log(Level.INFO, "ArmorStandEditor is compatible with this version of Minecraft. Loading continuing.");
            }
        } else{
            if(minecraftVersion.compareTo("v1_20") < 0){
                getLogger().log(Level.WARNING, "ArmorStandEditor is compatible with this version of Minecraft, but it is not the latest supported version.");
                getLogger().log(Level.WARNING, "Load Continuing, but some features may not work.");
            } else if (minecraftVersion.compareTo("v1_17") < 0 ) {
                getLogger().log(Level.SEVERE, "ArmorStandEditor is not compatible with this version of Minecraft.");
                getLogger().log(Level.SEVERE, "Please Update to a version past 1.17. Loading Failed.");
                getLogger().log(Level.INFO, SEPERATOR_LOG);
                getServer().getPluginManager().disablePlugin(this);
            } else{
                getLogger().log(Level.INFO, "ArmorStandEditor is compatible with this version of Minecraft. Loading continuing.");
            }
        }
        getLogger().log(Level.INFO, SEPERATOR_LOG);

        //Scoreboards - For all other versions MINUS Folia
        colorOfLockedTeam = (NamedTextColor) getConfig().get("scoreboardColor");
        if(!hasFolia){
            scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
            registerDisabledSlotsScoreboard(scoreboard);
        } else{
            getLogger().log(Level.WARNING, "Scoreboards currently do not work with Folia. Disabled Slots Feature will not work. Please deny asedit.disableSlots");
        }

        //TODO: Language Support - Priority 2

        //Rotation Config Options
        coarseRot = getConfig().getDouble("coarseRotation");
        fineRot = getConfig().getDouble("fineRotation");
        
        //Tool Data -- Another failure point
        toolMaterial = getConfig().getString("toolMaterial");
        if(toolMaterial != null){
            editToolMaterial = Material.getMaterial(toolMaterial);
        } else{
            getLogger().log(Level.SEVERE, "nable to get Tool for Use with Plugin. Unable to continue");
            getLogger().log(Level.INFO, SEPERATOR_LOG);
            getServer().getPluginManager().disablePlugin(this);
        }

        //Do we require custom tool names? -- Not Required by Default/If Not found
        requireToolName = getConfig().getBoolean("requireToolName", false);
        if(requireToolName){
            editToolName = getConfig().getString("toolName");
            if(editToolName != null){
                if(hasPurpur || hasPaper || hasFolia){ // Paper etc.
                    editToolName = String.valueOf(Component.text(editToolName.replace("&", "§")));
                } else{ // Spigot
                    editToolName = ChatColor.translateAlternateColorCodes('&', editToolName);
                }

            }
        }

        //Does the tool require Lore or Damage Data?
        requireToolData = getConfig().getBoolean("requireToolData", false);
        if(requireToolData){
            editToolData = getConfig().getInt("toolData", Integer.MIN_VALUE);
        }

        //TODO: Custom Data Pack Model Support - Priority 4

        //Can Make ArmorStands and ItemFrames visible/invisible?
        armorStandVisibility = getConfig().getBoolean("armorStandVisibility", true);
        itemFrameVisibility = getConfig().getBoolean("itemFrameVisibility", true);

        //Allowed to make itemFrames Glow?
        itemFrameGlowing = getConfig().getBoolean("itemFrameGlowing", true);




        getServer().getPluginManager().enablePlugin(this);


    }

    private void registerDisabledSlotsScoreboard(Scoreboard scoreboard) {

        getLogger().log(Level.INFO, "Registering Disabled Slots Scoreboard with Glowing Effects");

        if(scoreboard.getTeam(lockedTeamName) == null){
            scoreboard.registerNewTeam(lockedTeamName);
            scoreboard.getTeam(lockedTeamName).color(colorOfLockedTeam);
        } else{
            getLogger().log(Level.INFO, "Scoreboard for {0} already exists. Continuing.", lockedTeamName);
        }
    }

    private void logServerSoftwareVersionToConsole(boolean hasPaper, boolean hasFolia, boolean hasPurpur, boolean hasSpigot) {
        if(hasPurpur || hasPaper || hasFolia){
            getLogger().log(Level.INFO, "Paper/Folia/Purpur: TRUE");
        } else if (hasSpigot) {
            getLogger().log(Level.INFO, "SpigotMC: TRUE");
        } else{
            getLogger().log(Level.SEVERE, "You appear to not be running Paper, Folia, Purpur or Spigot. Please use those to continue.");
            getLogger().log(Level.SEVERE, "ArmorStandEditor will now be disabled!");
            getLogger().log(Level.INFO, SEPERATOR_LOG);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private String gatherMinecraftServerVersionInfo() {
        if (hasPurpur || hasFolia || hasPaper){
            minecraftVersion = getServer().getMinecraftVersion();
        } else {
            minecraftVersion = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        }
        return "";
    }
    
    /**
     * @deprecated
     *
     * Spigot support is here for now, but might be removed in a future version
     */
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

    private static boolean isPurpur() {
        try {
            Class.forName("org.purpurmc.purpur.event.PlayerAFKEvent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean isFolia(){
        try{
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }



    @Override
    public void onDisable() {
    }

}
