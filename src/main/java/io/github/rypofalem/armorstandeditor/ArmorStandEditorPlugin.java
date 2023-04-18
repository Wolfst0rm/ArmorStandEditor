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

import com.jeff_media.updatechecker.*;

import io.github.rypofalem.armorstandeditor.language.Language;
import io.github.rypofalem.armorstandeditor.Metrics.*;


import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.*;
import java.util.logging.Level;


public class ArmorStandEditorPlugin extends JavaPlugin{

    //!!! DO NOT REMOVE THESE UNDER ANY CIRCUMSTANCES - Required for BStats and UpdateChecker !!!
    public static final int SPIGOT_RESOURCE_ID = 94503;  //Used for Update Checker
    private static final int PLUGIN_ID = 12668;		     //Used for BStats Metrics
    private static ArmorStandEditorPlugin instance;

    private NamespacedKey iconKey;
    private Language lang;
    
    //Server Version Detection: Paper or Spigot and Invalid NMS Version
    String nmsVersion;
    String languageFolderLocation = "lang/";

    public boolean hasSpigot = false;
    public boolean hasPaper = false;
    boolean isFolia = Scheduler.isFolia();

    String nmsVersionNotLatest = null;
    static final String SEPARATOR_FIELD = "================================";

    public PlayerEditorManager editorManager;

    //Output for Updates
    boolean opUpdateNotification = false;
    boolean runTheUpdateChecker = false;
    double updateCheckerInterval;

    //Edit Tool Information
    Material editTool;
    String toolType;
    int editToolData = Integer.MIN_VALUE;
    boolean requireToolData = false;
    boolean requireToolName = false;
    TextComponent editToolNameText = null;
    String editToolName = null;
    boolean requireToolLore = false;
    List<?> editToolLore = null;
    boolean allowCustomModelData = false;
    Integer customModelDataInt = Integer.MIN_VALUE;
    
    //GUI Settings
    boolean requireSneaking = false;
    boolean sendToActionBar = true;
    
    //Armor Stand Specific Settings
    double coarseRot;
    double fineRot;
    boolean glowItemFrames = false;
    boolean invisibleItemFrames = true;
    boolean armorStandVisibility = true;

    //Glow Entity Colors
    public Scoreboard scoreboard;
    public Team team;
    String lockedTeam = "ASLocked";

    private static ArmorStandEditorPlugin plugin;

    public ArmorStandEditorPlugin(){
        instance = this;
    }

    @Override
    public void onEnable() {

        if (!isFolia)
            scoreboard = Objects.requireNonNull(this.getServer().getScoreboardManager()).getMainScoreboard();

        //Get NMS Version
        nmsVersion = getNmsVersion();

        //Load Messages in Console
        getLogger().info("======= ArmorStandEditor =======");
        getLogger().info("Plugin Version: " + getArmorStandEditorVersion());

        // Check if the Minecraft version is supported
        if (nmsVersion.compareTo("v1_13") < 0) {
            getLogger().log(Level.WARNING,"Minecraft Version: {0}",nmsVersion);
            getLogger().warning("ArmorStandEditor is not compatible with this version of Minecraft. Please update to at least version 1.13. Loading failed.");
            getServer().getPluginManager().disablePlugin(this);
            getLogger().info(SEPARATOR_FIELD);
            return;
        }

        //Also Warn People to Update if using nmsVersion lower than latest
        if (nmsVersion.compareTo("v1_19") < 0) {
            getLogger().log(Level.WARNING,"Minecraft Version: {0}",nmsVersion);
            getLogger().warning("ArmorStandEditor is compatible with this version of Minecraft, but it is not the latest supported version.");
            getLogger().warning("Loading continuing, but please consider updating to the latest version.");
        } else {
            getLogger().log(Level.INFO, "Minecraft Version: {0}",nmsVersion);
            getLogger().info("ArmorStandEditor is compatible with this version of Minecraft. Loading continuing.");
        }

        //Spigot Check
        hasSpigot = getHasSpigot();
        hasPaper = getHasPaper();

        //If Paper and Spigot are both FALSE - Disable the plugin
        if (!hasPaper && !hasSpigot){
            getLogger().severe("This plugin requires either Paper, Spigot or one of its forks to run. This is not an error, please do not report this!");
            getServer().getPluginManager().disablePlugin(this);
            getLogger().info(SEPARATOR_FIELD);
            return;
        } else {
            if (hasSpigot) {
                getLogger().log(Level.INFO,"SpigotMC: {0}",hasSpigot);
            } else {
                getLogger().log(Level.INFO,"PaperMC: {0}",hasPaper);
            }
        }

        getServer().getPluginManager().enablePlugin(this);
        if (!isFolia) registerScoreboards(scoreboard);
        getLogger().info(SEPARATOR_FIELD);

        //saveResource doesn't accept File.separator on Windows, need to hardcode unix separator "/" instead
        updateConfig("", "config.yml");
        updateConfig(languageFolderLocation, "de_DE.yml");
        updateConfig(languageFolderLocation, "es_ES.yml");
        updateConfig(languageFolderLocation, "fr_FR.yml");
        updateConfig(languageFolderLocation, "ja_JP.yml");
        updateConfig(languageFolderLocation, "nl_NL.yml");
        updateConfig(languageFolderLocation, "pl_PL.yml");
        updateConfig(languageFolderLocation, "pt_BR.yml");
        updateConfig(languageFolderLocation, "ro_RO.yml");
        updateConfig(languageFolderLocation, "ru_RU.yml");
        updateConfig(languageFolderLocation, "test_NA.yml");
        updateConfig(languageFolderLocation, "uk_UA.yml");
        updateConfig(languageFolderLocation, "zh_CN.yml");

        //English is the default language and needs to be unaltered to so that there is always a backup message string
        saveResource("lang/en_US.yml", true);
        lang = new Language(getLanguageFileName(), this);

        //Rotation
        coarseRot = getCoarseRot();
        fineRot = getFineRot();

        //Set Tool to be used in game
        toolType = getToolType();
        if (toolType != null) {
            editTool = Material.getMaterial(toolType); //Ignore Warning
        } else {
            getLogger().severe("Unable to get Tool for Use with Plugin. Unable to continue!");
            getLogger().info(SEPARATOR_FIELD);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        //Do we require a custom tool name?
        requireToolName = getToolNameRequirement();
        if(requireToolName){
            editToolName = getEditToolName();
            if(editToolName != null) {
                editToolNameText = LegacyComponentSerializer.legacy('&').deserialize(editToolName);
                editToolName = editToolNameText.content();
            }
        }

        //Custom Model Data
        allowCustomModelData = getAllowCustomModelData();

        if(allowCustomModelData){
            customModelDataInt = getCustomModelDataInt();
        }

        //ArmorStandVisibility Node
        armorStandVisibility = getArmorStandVisibility();

        //Is there NBT Required for the tool
        requireToolData = getToolDataRequirement();

        if(requireToolData) {
            editToolData = getEditToolData();
        }

        requireToolLore = getToolLoreRequirement();

        if(requireToolLore) {
            editToolLore = getEditToolLore();
        }

        //Require Sneaking - Wolfst0rm/ArmorStandEditor#17
        requireSneaking = getSneakingRequirement();

        //Send Messages to Action Bar
        sendToActionBar = getMessageSendingLocation();

        //All ItemFrame Stuff
        glowItemFrames = getGlowItemFramesLegacy();
        invisibleItemFrames = getItemFrameVisibility();

        //Add ability to enable ot Disable the running of the Updater
        runTheUpdateChecker = getRunTheUpdateChecker();

        //Add Ability to check for UpdatePerms that Notify Ops - https://github.com/Wolfieheart/ArmorStandEditor/issues/86
        opUpdateNotification = getOpUpdateNotifications();
        updateCheckerInterval = getUpdateCheckerInterval();

        //Run UpdateChecker - Reports out to Console on Startup ONLY!
        if(!isFolia && runTheUpdateChecker) {

            if(opUpdateNotification){
                runUpdateCheckerWithOPNotifyOnJoinEnabled();
            } else {
                runUpdateCheckerConsoleUpdateCheck();
            }

        }

        //Get Metrics from bStats
        getMetrics();

        editorManager = new PlayerEditorManager(this);
        CommandEx execute = new CommandEx(this);

        //CommandExecution and TabCompletion
        Objects.requireNonNull(getCommand("ase")).setExecutor(execute);
        Objects.requireNonNull(getCommand("ase")).setTabCompleter(execute);

        getServer().getPluginManager().registerEvents(editorManager, this);

    }

    private void runUpdateCheckerConsoleUpdateCheck() {
        if (getArmorStandEditorVersion().contains(".x")) {
            getLogger().warning("Note from the development team: ");
            getLogger().warning("It appears that you are using the development version of ArmorStandEditor");
            getLogger().warning("This version can be unstable and is not recommended for Production Environments.");
            getLogger().warning("Please, report bugs to: https://github.com/Wolfieheart/ArmorStandEditor . ");
            getLogger().warning("This warning is intended to be displayed when using a Dev build and is NOT A BUG!");
            getLogger().info("Update Checker does not work on Development Builds.");
        } else {
            new UpdateChecker(this, UpdateCheckSource.SPIGET, "" + SPIGOT_RESOURCE_ID + "")
                    .setDownloadLink("https://www.spigotmc.org/resources/armorstandeditor-reborn.94503/")
                    .setChangelogLink("https://www.spigotmc.org/resources/armorstandeditor-reborn.94503/history")
                    .setColoredConsoleOutput(true)
                    .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion().addServerVersion())
                    .checkEveryXHours(updateCheckerInterval)
                    .checkNow();
        }
    }

    private void runUpdateCheckerWithOPNotifyOnJoinEnabled() { 
        if (getArmorStandEditorVersion().contains(".x")) {
            getLogger().warning("Note from the development team: ");
            getLogger().warning("It appears that you are using the development version of ArmorStandEditor");
            getLogger().warning("This version can be unstable and is not recommended for Production Environments.");
            getLogger().warning("Please, report bugs to: https://github.com/Wolfieheart/ArmorStandEditor . ");
            getLogger().warning("This warning is intended to be displayed when using a Dev build and is NOT A BUG!");
            getLogger().info("Update Checker does not work on Development Builds.");
        } else {
            new UpdateChecker(this, UpdateCheckSource.SPIGET, "" + SPIGOT_RESOURCE_ID + "")
                    .setDownloadLink("https://www.spigotmc.org/resources/armorstandeditor-reborn.94503/")
                    .setChangelogLink("https://www.spigotmc.org/resources/armorstandeditor-reborn.94503/history")
                    .setColoredConsoleOutput(true)
                    .setNotifyOpsOnJoin(true)
                    .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion().addServerVersion())
                    .checkEveryXHours(updateCheckerInterval)
                    .checkNow();
        }
    }

    //Implement Glow Effects for Wolfstorm/ArmorStandEditor-Issues#5 - Add Disable Slots with Different Glow than Default
    private void registerScoreboards(Scoreboard scoreboard) {
        getLogger().info("Registering Scoreboards required for Glowing Effects");

        //Fix for Scoreboard Issue reported by Starnos - Wolfst0rm/ArmorStandEditor-Issues/issues/18
        if (scoreboard.getTeam(lockedTeam) == null) {
            scoreboard.registerNewTeam(lockedTeam);
            Objects.requireNonNull(scoreboard.getTeam(lockedTeam)).color(NamedTextColor.RED);
        } else {
            getLogger().info("Scoreboard for ASLocked Already exists. Continuing to load");
        }
    }

    private void unregisterScoreboards(Scoreboard scoreboard) {
        getLogger().info("Removing Scoreboards required for Glowing Effects");

        team = scoreboard.getTeam(lockedTeam);
        if(team != null) { //Basic Sanity Check to ensure that the team is there
            team.unregister();
        } else{
            getLogger().severe("Team Already Appears to be removed. Please do not do this manually!");
        }
    }


    public void performReload() {

        //Unregister Scoreboard before before performing the reload
        if (!isFolia) {
            scoreboard = Objects.requireNonNull(this.getServer().getScoreboardManager()).getMainScoreboard();
            unregisterScoreboards(scoreboard);
        }

        //Re-Register Scoreboards
        if (!isFolia) registerScoreboards(scoreboard);

        //Reload Config File
        reloadConfig();

        //Set Language
        lang = new Language(getLanguageFileName(), this);

        //Rotation
        coarseRot = getCoarseRot();
        fineRot = getFineRot();

        //Set Tool to be used in game
        toolType = getToolType();
        if (toolType != null) {
            editTool = Material.getMaterial(toolType); //Ignore Warning
        }

        //Do we require a custom tool name?
        requireToolName = getToolNameRequirement();
        if(requireToolName){
            editToolName = getEditToolName();
            if(editToolName != null) {
                editToolNameText = LegacyComponentSerializer.legacy('&').deserialize(editToolName);
                editToolName = editToolNameText.content();
            }
        }

        //Custom Model Data
        allowCustomModelData = getAllowCustomModelData();

        if(allowCustomModelData){
            customModelDataInt = getCustomModelDataInt();
        }

        //ArmorStandVisibility Node
        armorStandVisibility = getArmorStandVisibility();

        //Is there NBT Required for the tool
        requireToolData = getToolDataRequirement();

        if(requireToolData) {
            editToolData = getEditToolData();
        }

        requireToolLore = getToolLoreRequirement();

        if(requireToolLore) {
            editToolLore = getEditToolLore();
        }

        //Require Sneaking - Wolfst0rm/ArmorStandEditor#17
        requireSneaking = getSneakingRequirement();

        //Send Messages to Action Bar
        sendToActionBar = getMessageSendingLocation();

        //All ItemFrame Stuff
        glowItemFrames = getGlowItemFramesLegacy();
        invisibleItemFrames = getItemFrameVisibility();

        //Add ability to enable ot Disable the running of the Updater
        runTheUpdateChecker = getRunTheUpdateChecker();

        //Add Ability to check for UpdatePerms that Notify Ops - https://github.com/Wolfieheart/ArmorStandEditor/issues/86
        opUpdateNotification = getOpUpdateNotifications();
        updateCheckerInterval = getUpdateCheckerInterval();

        //Run UpdateChecker - Reports out to Console on Startup ONLY!
        if(!isFolia && runTheUpdateChecker) {

            if(opUpdateNotification){
                runUpdateCheckerWithOPNotifyOnJoinEnabled();
            } else {
                runUpdateCheckerConsoleUpdateCheck();
            }

        }

    }

    private void updateConfig(String folder, String config) {
        if(!new File(getDataFolder() + File.separator + folder + config).exists()){
            saveResource(folder  + config, false);
        }
    }

    @Override
    public void onDisable(){
        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            if(player.getOpenInventory().getTopInventory().getHolder() == editorManager.getMenuHolder()) player.closeInventory();
        }

        if (!isFolia) {
            scoreboard = Objects.requireNonNull(this.getServer().getScoreboardManager()).getMainScoreboard();
            unregisterScoreboards(scoreboard);
        }
    }

    public String getNmsVersion(){
        return this.getServer().getClass().getPackage().getName().replace(".",",").split(",")[3];
    }

    public boolean getHasSpigot(){
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            nmsVersionNotLatest = "SpigotMC ASAP.";
            return true;
        } catch (ClassNotFoundException e){
            nmsVersionNotLatest = "";
            return false;
        }
    }

    public boolean getHasPaper(){
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            nmsVersionNotLatest = "SpigotMC ASAP.";
            return true;
        } catch (ClassNotFoundException e){
            nmsVersionNotLatest = "";
            return false;
        }
    }

    public Language getLang(){ return lang; }

    //Config Options as get - Ordered
    public String getArmorStandEditorVersion(){ return getConfig().getString("version"); }
    public String getLanguageFileName() { return getConfig().getString("lang"); }

    public boolean getRunTheUpdateChecker() { return getConfig().getBoolean("runTheUpdateChecker"); }
    private double getUpdateCheckerInterval() { return getConfig().getDouble("check-interval"); }
    private boolean getOpUpdateNotifications() { return getConfig().getBoolean("opUpdateNotification"); }

    private String getToolType() { return getConfig().getString("tool"); }
    public Material getEditTool() { return this.editTool; }

    private boolean getToolDataRequirement() { return getConfig().getBoolean("requireToolData", false); }
    private Integer getEditToolData() { return getConfig().getInt("toolData", Integer.MIN_VALUE); }

    private boolean getToolNameRequirement() {  return getConfig().getBoolean("requireToolName", false);   }
    private String getEditToolName() {  return getConfig().getString("toolName", null);   }

    private boolean getToolLoreRequirement() { return getConfig().getBoolean("requireToolLore", false); }
    private List<?> getEditToolLore() { return getConfig().getList("toolLore", null); }

    public boolean getAllowCustomModelData() { return getConfig().getBoolean("allowCustomModelData"); }
    public Integer getCustomModelDataInt() { return getConfig().getInt("customModelDataInt"); }

    public double getCoarseRot(){ return getConfig().getDouble("coarse"); }
    private double getFineRot() { return getConfig().getDouble("fine"); }

    public boolean getArmorStandVisibility(){ return getConfig().getBoolean("armorStandVisibility"); }
    public boolean getItemFrameVisibility(){ return getConfig().getBoolean("invisibleItemFrames"); }

    public boolean getSneakingRequirement(){ return getConfig().getBoolean("requireSneaking", false); }
    public boolean getMessageSendingLocation(){ return getConfig().getBoolean("sendMessagesToActionBar", true); }

    private boolean getGlowItemFramesLegacy() { return getConfig().getBoolean("glowingItemFrame", true); }


    public boolean isEditTool(ItemStack itemStk){
        if (itemStk == null) { return false; }
        if (editTool != itemStk.getType()) { return false; }

        ItemMeta itemMeta = itemStk.getItemMeta();
        if(itemMeta == null) return false;

        //FIX: Depreciated Stack for getDurability
        if (requireToolData){
            Damageable d1 = (Damageable) itemMeta; //Get the Damageable Options for itemStk
            if (d1 != null) { //We do this to prevent NullPointers
                if (d1.getDamage() != (short) editToolData) { return false; }
            }
        }

        if(requireToolName && editToolName != null){
            if(!itemStk.hasItemMeta()) { return false; }

            //Get the name of the Edit Tool - If Null, return false
            String itemName = String.valueOf(itemMeta.displayName());

            //If the name of the Edit Tool is not the Name specified in Config then Return false
            if(!itemName.equals(editToolName)) { return false; }

        }

        if(requireToolLore && editToolLore != null){

            //If the ItemStack does not have Metadata then we return false
            if(!itemStk.hasItemMeta()) { return false; }

            //Get the lore of the Item and if it is null - Return False
            List<?> itemLore = itemMeta.lore();

            //If the Item does not have Lore - Return False
            boolean hasTheItemLore = itemMeta.hasLore();
            if (!hasTheItemLore)  { return false; }

            //Return False if itemLore on the item does not match what we expect in the config.
            if(!itemLore.equals(editToolLore)) { return false; }

        }

        if (allowCustomModelData && customModelDataInt != null) {
            //If the ItemStack does not have Metadata then we return false
            if(!itemStk.hasItemMeta()) { return false; }
            Integer itemCustomModel = itemMeta.getCustomModelData();
            return itemCustomModel.equals(customModelDataInt);
        }
        return true;
    }

    public static ArmorStandEditorPlugin instance(){
        return instance;
    }

    //Metrics/bStats Support
    private void getMetrics(){

        Metrics metrics = new Metrics(this, PLUGIN_ID);

        //RequireToolLore Metric
        metrics.addCustomChart(new SimplePie("tool_lore_enabled", () -> getConfig().getString("requireToolLore")));

        //RequireToolData
        metrics.addCustomChart(new SimplePie("tool_data_enabled", () -> getConfig().getString("requireToolData")));

        //Send Messages to ActionBar
        metrics.addCustomChart(new SimplePie("action_bar_messages", () -> getConfig().getString("sendMessagesToActionBar")));

        //Check for Sneaking
        metrics.addCustomChart(new SimplePie("require_sneaking", () -> getConfig().getString("requireSneaking")));

        //Language is used
        metrics.addCustomChart(new DrilldownPie("language_used", () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            Map<String, Integer> entry = new HashMap<>();

            String languageUsed = getConfig().getString("lang");
            assert languageUsed != null;

            if (languageUsed.startsWith("nl")) {
                map.put("Dutch", entry);
            } else if (languageUsed.startsWith("de")) {
                map.put("German", entry);
            } else if (languageUsed.startsWith("en")) {
                map.put("English", entry);
            } else if (languageUsed.startsWith("es")) {
                map.put("Spanish", entry);
            } else if (languageUsed.startsWith("fr")) {
                map.put("French", entry);
            } else if (languageUsed.startsWith("ja")) {
                map.put("Japanese", entry);
            } else if (languageUsed.startsWith("pl")) {
                map.put("Polish", entry);
            }else if(languageUsed.startsWith("ru")){ //See PR# 41 by KPidS
                map.put("Russian", entry);
            }else if(languageUsed.startsWith("ro")){
                map.put("Romanian", entry);
            } else if(languageUsed.startsWith("uk")){
                map.put("Ukrainian", entry);
            } else if(languageUsed.startsWith("zh")) {
                map.put("Chinese", entry);
            } else if(languageUsed.startsWith("pt")) {
                map.put("Brazilian", entry);
            } else{
                map.put("Other", entry);
            }
            return map;
        }));

        //ArmorStandInvis Config
        metrics.addCustomChart(new SimplePie("armor_stand_invisibility_usage", () -> getConfig().getString("armorStandVisibility")));

        //ArmorStandInvis Config
        metrics.addCustomChart(new SimplePie("itemframe_invisibility_used", () -> getConfig().getString("invisibleItemFrames")));

        //Add tracking to see who is using Custom Naming in BStats
        metrics.addCustomChart(new SimplePie("custom_toolname_enabled", () -> getConfig().getString("requireToolName")));

        metrics.addCustomChart(new SimplePie("using_the_update_checker", () -> getConfig().getString("runTheUpdateChecker")));
        metrics.addCustomChart(new SimplePie("op_updates", () -> getConfig().getString("opUpdateNotification")));


    }

    public NamespacedKey getIconKey() {
        if(iconKey == null) iconKey = new NamespacedKey(this, "command_icon");
        return iconKey;
    }
}
