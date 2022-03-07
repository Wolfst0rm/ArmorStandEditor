/*
 * ArmorStandEditor: Bukkit plugin to allow editing armor stand attributes
 * Copyright (C) 2016-2022  RypoFalem
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

import de.jeff_media.updatechecker.*;

import io.github.rypofalem.armorstandeditor.Metrics.*;

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

import java.io.*;
import java.time.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.io.File;

public class ArmorStandEditorPlugin extends JavaPlugin{

	private static final int SPIGOT_RESOURCE_ID = 94503; //Used for Update Checker
	private NamespacedKey iconKey;
	private static ArmorStandEditorPlugin instance;
	private CommandEx execute;
	private Language lang;

	//Server Version Detection: Paper or Spigot and Invalid NMS Version
	public boolean hasSpigot = false;
	public boolean hasPaper = false;
	String nmsVersion = null;
	String nmsVersionNotLatest = null;
	PluginDescriptionFile pdfFile = this.getDescription();
	static final String SEPARATOR_FIELD = "================================";

	public PlayerEditorManager editorManager;

	Material editTool;
	String toolType;
	boolean requireToolData = false;
	boolean sendToActionBar = true;
	int editToolData = Integer.MIN_VALUE;
	boolean requireSneaking = false;
	boolean requireToolLore = false;
	String editToolLore = null;
	boolean debug = false; //weather or not to broadcast messages via print(String message)
	double coarseRot;
	double fineRot;
	boolean glowItemFrames = false;
	boolean invisibleItemFrames = true;
	boolean armorStandVisibility = true;

	//Glow Entity Colors
	public Scoreboard scoreboard;
	public Team team;
	String lockedTeam = "ASLocked";

	//Better Debug Output
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	Date date = Calendar.getInstance().getTime();
	Instant instant = Instant.now();
	DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT ).withLocale( Locale.UK ).withZone( ZoneId.systemDefault() );
	String dateAsString = dateFormat.format(date);
	String timeAsString = formatter.format(instant);
	final String debugOutputFileName = getDataFolder() + File.separator + "DEBUG-" + dateAsString +  ".log";
	FileOutputStream fos = null;
	File f = new File(debugOutputFileName);

	private static ArmorStandEditorPlugin plugin;

	private static final int PLUGIN_ID = 12668;

	public ArmorStandEditorPlugin(){
		instance = this;
	}

	@Override
	public void onEnable(){

		//Run UpdateChecker
		runUpdateChecker();

		scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();

		//Get NMS Version
		nmsVersion = getNmsVersion();
		print("Net.Minecraft.Server version is: " + nmsVersion);

		//Load Messages in Console
		getLogger().info("======= ArmorStandEditor =======");
		getLogger().info("Plugin Version: " + pdfFile.getVersion());

		//Spigot Check
		hasSpigot = getHasSpigot();
		getLogger().info("SpigotMC: " + hasSpigot);

		//Paper Check
		hasPaper = getHasPaper();
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
				nmsVersion.startsWith("v1_12") ||
				nmsVersion.startsWith("v1_13")
				){
			getLogger().warning("Minecraft Version: " + nmsVersion + " is not supported. Loading Plugin Failed.");
			getLogger().info(SEPARATOR_FIELD);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		//Also Warn People to Update if using nmsVersion lower than latest
		if (    nmsVersion.startsWith("v1_14") ||
				nmsVersion.startsWith("v1_15") ||
				nmsVersion.startsWith("v1_16") ||
				nmsVersion.startsWith("v1_17")){
			getLogger().warning("Minecraft Version: " + nmsVersion + " is supported, but not latest.");
			getLogger().warning("ArmorStandEditor will still work, but please update to the latest Version of " + nmsVersionNotLatest + ". Loading continuing.");
			getLogger().info("Minecraft Version: " + nmsVersion + " is supported. Loading continuing.");
		}
		getServer().getPluginManager().enablePlugin(this);
		registerScoreboards(scoreboard);
		getLogger().info(SEPARATOR_FIELD);

		//Is Debug Enabled
		debug = getConfig().getBoolean("debug", false);
		print("Debug Mode Enabled? Well if you can read this its true");

		if(debug){
			createDebugFile();
		}

		//saveResource doesn't accept File.separator on Windows, need to hardcode unix separator "/" instead
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
		print("Language in use: " + getConfig().getString("lang"));

		//Rotation
		coarseRot = getConfig().getDouble("coarse");
		fineRot = getConfig().getDouble("fine");

		//Set Tool to be used in game
		toolType = getConfig().getString("tool");
		if (toolType != null) {
			print("Edit Tool used to interact with Plugin is: " + toolType);
			editTool = Material.getMaterial(toolType); //Ignore Warning
		} else {
			 getLogger().severe("Unable to get Tool for Use with Plugin. Unable to continue!");
			 getLogger().info(SEPARATOR_FIELD);
			 getServer().getPluginManager().disablePlugin(this);
			 return;
		}

		//ArmorStandVisibility Node
		armorStandVisibility = getConfig().getBoolean("armorStandVisibility", true);
		print("ArmorStands allowed to be made visible/invisible?: " + armorStandVisibility);

		//Is there NBT Required for the tool
		requireToolData = getConfig().getBoolean("requireToolData", false);
		print("NBT Data Required: " + requireToolData);

		if(requireToolData) {
			editToolData = getConfig().getInt("toolData", Integer.MIN_VALUE);
			print("Tool Data is: " + editToolData);
		}

		requireToolLore = getConfig().getBoolean("requireToolLore", false);
		print("Lore Required?: " + requireToolLore);

		if(requireToolLore) {
			editToolLore = getConfig().getString("toolLore", null);
			print("Lore needs to be: " + editToolLore);
		}

		//Require Sneaking - Wolfst0rm/ArmorStandEditor#17
		requireSneaking = getConfig().getBoolean("requireSneaking",false);
		print("Sneaking required to activate the UI: " + requireSneaking);


		//Send Messages to Action Bar
		sendToActionBar = getConfig().getBoolean("sendMessagesToActionBar", true);
		print("Messages being sent to action bar?: " + sendToActionBar);

		//All ItemFrame Stuff
		glowItemFrames = getConfig().getBoolean("glowingItemFrame", true);
		print("Are glowing Item Frames enabled for 1.17 Users?: " + glowItemFrames);

		invisibleItemFrames = getConfig().getBoolean("invisibleItemFrames", true);
		print("Can users turn ItemFrames invisible?: " + invisibleItemFrames);

		//Get Metrics from bStats
		getMetrics();

		editorManager = new PlayerEditorManager(this);
		execute = new CommandEx(this);
		getCommand("ase").setExecutor(execute); //Ignore the warning with this. GetCommand is Nullable. Will be fixed in the future
		getServer().getPluginManager().registerEvents(editorManager, this);


	}

	private void runUpdateChecker() {
		UpdateChecker.init(this, SPIGOT_RESOURCE_ID)
				.setDownloadLink("https://www.spigotmc.org/resources/armorstandeditor-reborn.94503/")
				.setChangelogLink("https://www.spigotmc.org/resources/armorstandeditor-reborn.94503/history")
				.setNotifyOpsOnJoin(true)
				.setNotifyByPermissionOnJoin("asedit.update")
				.setColoredConsoleOutput(true)
				.setUserAgent(new UserAgentBuilder().addPluginNameAndVersion().addServerVersion())
				.checkEveryXHours(72) //Warn people every 72 hours
				.checkNow();
	}

	//Implement Glow Effects for Wolfstorm/ArmorStandEditor-Issues#5 - Add Disable Slots with Different Glow than Default
	private void registerScoreboards(Scoreboard scoreboard) {
		getLogger().info("Registering Scoreboards required for Glowing Effects");

		//Fix for Scoreboard Issue reported by Starnos - Wolfst0rm/ArmorStandEditor-Issues/issues/18
		if (scoreboard.getTeam(lockedTeam) == null) {
			print("Team '" + lockedTeam + "' does not exist, proceeding to create new team");
			scoreboard.registerNewTeam(lockedTeam);

			print("Setting Team '" + lockedTeam + "' color to RED");
			scoreboard.getTeam(lockedTeam).setColor(ChatColor.RED);
		} else {
			getLogger().info("Scoreboard for ASLocked Already exists. Continuing to load");
		}
	}

	private void unregisterScoreboards(Scoreboard scoreboard) {
		getLogger().info("Removing Scoreboards required for Glowing Effects");

		team = scoreboard.getTeam(lockedTeam);
		if(team != null) { //Basic Sanity Check to ensure that the team is there
			team.unregister();
			print("Team '" + lockedTeam + "' successfully removed.");
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
		unregisterScoreboards(scoreboard);
	}

	public void createDebugFile(){
			try {
				if (!f.exists() && f.createNewFile()) {
					Files.setAttribute(f.toPath(), "dos:hidden", false);
				}
			} catch (IOException e) {
				this.getServer().getLogger().warning(e.getMessage());
			}
	}

	public void log(String message){
		//Output to Server Console - Safer than doing a Broadcast to everyone on the Server
		String timeMsgSep = ": ";
		this.getServer().getLogger().info("ArmorStandEditor: " + message);

		try{
			fos = new FileOutputStream(f, true);

			//Write the Content as Bytes
			fos.write(timeAsString.getBytes());
			fos.write(timeMsgSep.getBytes());
			fos.write(message.getBytes());
			fos.write(10);
			fos.flush();
		}catch(IOException e){
			this.getServer().getLogger().warning(e.getMessage());
		}finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					this.getServer().getLogger().warning(e.getMessage());
				}
			}
		}
	}

	public String getNmsVersion(){
		return this.getServer().getClass().getPackage().getName().replace(".",",").split(",")[3];
	}

	public boolean getHasSpigot(){
		try {
			Class.forName("org.spigotmc.SpigotConfig");
			print("SpigotMC Detected.");
			nmsVersionNotLatest = "SpigotMC ASAP.";
			return true;
		} catch (ClassNotFoundException e){
			nmsVersionNotLatest = "";
			return false;
		}
	}

	public boolean getArmorStandVisibility(){
		return getConfig().getBoolean("armorStandVisibility");
	}

	public boolean getItemFrameVisibility(){
		return getConfig().getBoolean("invisibleItemFrames");
	}

	public boolean getHasPaper(){
		try {
			Class.forName("com.destroystokyo.paper.PaperConfig");
			print("PaperMC Detected.");
			nmsVersionNotLatest = "SpigotMC ASAP.";
			return true;
		} catch (ClassNotFoundException e){
			nmsVersionNotLatest = "";
			return false;
		}
	}
	/*
	*	For Internal Debugging -
	*
	*   set debug: true in Config.yml
	*   NOTE: NOT RECOMMENDED FOR PROD! INTERNAL TESTING ONLY!
	*
	* 	To be refactored - Apart Log File.
	*/
	public void print(String message){
		if(debug){
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

			//If the ItemStack does not have Metadata then we return false
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
		metrics.addCustomChart(new SimplePie("tool_lore_enabled", () -> getConfig().getString("requireToolLore")));

		//RequireToolData
		metrics.addCustomChart(new SimplePie("tool_data_enabled", () -> getConfig().getString("requireToolData")));

		//Send Messages to ActionBar
		metrics.addCustomChart(new SimplePie("action_bar_messages", () -> getConfig().getString("sendMessagesToActionBar")));

		//Debug Mode Enabled?
		metrics.addCustomChart(new SimplePie("uses_debug_mode", () -> getConfig().getString("debug")));

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
