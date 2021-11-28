/*
 * ArmorStandEditor: Bukkit plugin to allow editing armor stand attributes
 * Copyright (C) 2016  RypoFalem
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

import io.github.rypofalem.armorstandeditor.language.Language;
import io.github.rypofalem.armorstandeditor.Metrics.DrilldownPie;
import io.github.rypofalem.armorstandeditor.Metrics.SimplePie;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ArmorStandEditorPlugin extends JavaPlugin{
	private NamespacedKey iconKey;
	private static ArmorStandEditorPlugin instance;
	private CommandEx execute;
	private Language lang;

	//Server Version Detection: Paper or Spigot and Invalid NMS Version
	public boolean hasSpigot = false;
	public boolean hasPaper = false;
	private String nmsVersion = null;
	private String nmsVersionNotLatest = null;
	PluginDescriptionFile pdfFile = this.getDescription();

	public PlayerEditorManager editorManager;

	Material editTool;
	String toolType;
	boolean requireToolData = false;
	boolean sendToActionBar = true;
	int editToolData = Integer.MIN_VALUE;
	boolean requireToolLore = false;
	String editToolLore = null;
	boolean debug = false; //weather or not to broadcast messages via print(String message)
	double coarseRot;
	double fineRot;
	boolean glowItemFrames;

	//Glow Entity Colors
	public Scoreboard scoreboard;
	public Team team;

	private static ArmorStandEditorPlugin plugin;

	private static final int PLUGIN_ID = 12668;

	public ArmorStandEditorPlugin(){
		instance = this;
	}

	@Override
	public void onEnable(){
		scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
		registerScoreboards();

		//Get NMS Version
		nmsVersion = getServer().getClass().getPackage().getName().replace(".",",").split(",")[3];

		//Load Messages in Console
		getLogger().info("======= ArmorStandEditor =======");
		getLogger().info("Plugin Version: " + pdfFile.getVersion());

		//Spigot Check
		try {
			Class.forName("org.spigotmc.SpigotConfig");
			hasSpigot = true;
			nmsVersionNotLatest = "SpigotMC ASAP";
		} catch (ClassNotFoundException e){
			hasSpigot = false;
		}
		getLogger().info("SpigotMC: " + hasSpigot);

		//Paper Check
		try{
			Class.forName("com.destroystokyo.paper.PaperConfig");
			hasPaper = true;
			nmsVersionNotLatest = "Paper ASAP";
		} catch (ClassNotFoundException e){
			hasPaper = false;
		}
		getLogger().info("PaperMC: " + hasPaper);

		//If Paper and Spigot are both FALSE - Disable the plugin
		if (!hasPaper && !hasSpigot){
			getLogger().severe("This plugin requires either Paper, Spigot or one of its forks to run");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		//Minimum Version Check - No Lower than 1.13-API. Will be tuned out in the future
		if (    nmsVersion.startsWith("v1_8")  ||
				nmsVersion.startsWith("v1_9")  ||
				nmsVersion.startsWith("v1_10") ||
				nmsVersion.startsWith("v1_11") ||
				nmsVersion.startsWith("v1_12")){
			getLogger().warning("Minecraft Version: " + nmsVersion + " is not supported. Loading Plugin Failed.");
			getLogger().info("================================");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		//Also Warn People to Update if using nmsVersion lower than latest
		//TODO: Add 1.17 to Non Latest ONCE 1.18 Spigot is Available
		if (    nmsVersion.startsWith("v1_13") ||
				nmsVersion.startsWith("v1_14") ||
				nmsVersion.startsWith("v1_15") ||
				nmsVersion.startsWith("v1_16")){
			getLogger().warning("Minecraft Version: " + nmsVersion + " is supported, but not latest.");
			getLogger().warning("ArmorStandEditor will still work, but please update to the latest Version of " + nmsVersionNotLatest + ". Loading continuing.");
		} else {
			getLogger().info("Minecraft Version: " + nmsVersion + " is supported. Loading continuing.");
		}
		getServer().getPluginManager().enablePlugin(this);
		getLogger().info("================================");

		//saveResource doesn't accept File.separator on windows, need to hardcode unix separator "/" instead
		updateConfig("", "config.yml");
		updateConfig("lang/", "test_NA.yml");
		updateConfig("lang/", "nl_NL.yml");
		updateConfig("lang/", "uk_UA.yml");
		updateConfig("lang/", "zh_CN.yml");
		updateConfig("lang/", "fr_FR.yml");
		updateConfig("lang/", "ro_RO.yml");
		updateConfig("lang/", "ja_JP.yml");
		updateConfig("lang/", "de_DE.yml");
		updateConfig("lang/", "es_ES.yml");
		//English is the default language and needs to be unaltered to so that there is always a backup message string
		saveResource("lang/en_US.yml", true);
		lang = new Language(getConfig().getString("lang"), this);

		//Rotation
		coarseRot = getConfig().getDouble("coarse");
		fineRot = getConfig().getDouble("fine");

		//Set Tool to be used in game
		toolType = getConfig().getString("tool");
		if (toolType != null) {
			editTool = Material.getMaterial(toolType); //Ignore Warning
		} else {
			 getLogger().severe("Unable to get Tool for Use with Plugin. Unable to continue!");
			 getLogger().info("================================");
			 getServer().getPluginManager().disablePlugin(this);
			 return;
		}

		//Is there NBT Required for the tool
		requireToolData = getConfig().getBoolean("requireToolData", false);
		if(requireToolData) editToolData = getConfig().getInt("toolData", Integer.MIN_VALUE);
		requireToolLore = getConfig().getBoolean("requireToolLore", false);
		if(requireToolLore) editToolLore= getConfig().getString("toolLore", null);

		//Optional Information
		debug = getConfig().getBoolean("debug", true);
		sendToActionBar = getConfig().getBoolean("sendMessagesToActionBar", true);
		glowItemFrames = getConfig().getBoolean("glowingItemFrame", true);

		//Get Metrics from bStats
		getMetrics();

		editorManager = new PlayerEditorManager(this);
		execute = new CommandEx(this);
		getCommand("ase").setExecutor(execute); //Ignore the warning with this. GetCommand is Nullable. Will be fixed in the future
		getServer().getPluginManager().registerEvents(editorManager, this);


	}

	//Implement Glow Effects for Wolfstorm/ArmorStandEditor-Issues#5 - Add Disable Slots with Different Glow than Default
	private void registerScoreboards() {
		getLogger().info("Registering Scoreboards required for Glowing Effects");
		scoreboard.registerNewTeam("ASLocked");
		scoreboard.getTeam("ASLocked").setColor(ChatColor.RED);

	}

	private void unregisterScoreboards() {
		getLogger().info("Removing Scoreboards required for Glowing Effects");

		team = scoreboard.getTeam("ASLocked");
		if(team != null) { //Basic Sanity Check to ensure that the team is there
			team.unregister();
		} else{
			getLogger().severe("Team Already Appears to be removed. Please do not do this manually!");
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

		scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
		unregisterScoreboards();
	}

	public void log(String message){
		this.getServer().getLogger().info("ArmorStandEditor: " + message);
	}

	public void print(String message){
		if(debug){
			this.getServer().broadcastMessage(message);
			log(message);
		}
	}

	public static ArmorStandEditorPlugin instance(){
		return instance;
	}

	public Language getLang(){
		return lang;
	}

	public boolean isEditTool(ItemStack itemStk){
		if (itemStk == null) { return false; }
		if (editTool != itemStk.getType()) { return false; }

		//FIX: Depreciated Stack for getDurability
		//		if(requireToolData && item.getDurability() != (short)editToolData) return false;
		if (requireToolData){
			Damageable d1 = (Damageable) itemStk.getItemMeta(); //Get the Damageable Options for itemStk
			if (d1 != null) { //We do this to prevent NullPointers
				if (d1.getDamage() != (short) editToolData) { return false; }
			}
		}

		if(requireToolLore && editToolLore != null){

			//If the ItemStack does not have Meta Data then we return false
			if(!itemStk.hasItemMeta()) { return false; }

			//Get the lore of the Item and if it is null - Return False
			List<String> itemLore = itemStk.getItemMeta().getLore(); //Ignore warnings this gives. Will be fixed in the future
			if (itemLore == null){ return false; }

			//If the Item does not have Lore - Return False
			boolean hasTheItemLore = itemStk.getItemMeta().hasLore();
			if (!hasTheItemLore)  { return false; }

			//Item the first thing in the ItemLore List does not Equal the Config Value "editToolLore" - return false
			if (!itemLore.get(0).equals(editToolLore))  { return false; } //Does not need simplified - IntelliJ likes to complain here

		}

		return true;
	}

	//Metrics/bStats Support
	private void getMetrics(){

		Metrics metrics = new Metrics(this, PLUGIN_ID);

		//RequireToolLore Metric
		metrics.addCustomChart(new SimplePie("tool_lore_enabled", () -> {
			return getConfig().getString("requireToolLore");
		}));

		//RequireToolData
		metrics.addCustomChart(new SimplePie("tool_data_enabled", () ->{
			return getConfig().getString("requireToolData");
		}));

		//Send Messages to ActionBar
		metrics.addCustomChart(new SimplePie("action_bar_messages", () -> {
			return getConfig().getString("sendMessagesToActionBar");
		}));

		//Debug Mode Enabled?
		metrics.addCustomChart(new SimplePie("uses_debug_mode", () -> {
			return getConfig().getString("debug");
		}));

		//Language is used
		metrics.addCustomChart(new DrilldownPie("language_used", () -> {
			Map<String, Map<String, Integer>> map = new HashMap<>();
			Map<String, Integer> entry = new HashMap<>();

			String languageUsed = getConfig().getString("lang");
			entry.put(languageUsed, 1);

			assert languageUsed != null;
			if(languageUsed.startsWith("nl")){
				map.put("Dutch", entry);
			} else if(languageUsed.startsWith("de")){
				map.put("German", entry);
			} else if(languageUsed.startsWith("es")){
				map.put("Spanish", entry);
			} else if(languageUsed.startsWith("fr")){
				map.put("French", entry);
			} else if(languageUsed.startsWith("ja")){
				map.put("Japanese", entry);
			} else if(languageUsed.startsWith("pl")){
				map.put("Polish", entry);
			} else if(languageUsed.startsWith("ro")){
				map.put("Romanian", entry);
			} else if(languageUsed.startsWith("uk")){
				map.put("Ukrainian", entry);
			} else if(languageUsed.startsWith("zh")){
				map.put("Chinese", entry);
			} else{
				map.put("Other", entry);
			}

			return map;
		}));

	}

	public NamespacedKey getIconKey() {
		if(iconKey == null) iconKey = new NamespacedKey(this, "command_icon");
		return iconKey;
	}
}
