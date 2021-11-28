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

import io.github.rypofalem.armorstandeditor.modes.AdjustmentMode;
import io.github.rypofalem.armorstandeditor.modes.Axis;
import io.github.rypofalem.armorstandeditor.modes.EditMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandEx implements CommandExecutor {
	ArmorStandEditorPlugin plugin;
	final String LISTMODE = ChatColor.YELLOW + "/ase mode <" + Util.getEnumList(EditMode.class) + ">";
	final String LISTAXIS = ChatColor.YELLOW + "/ase axis <" + Util.getEnumList(Axis.class) + ">";
	final String LISTADJUSTMENT = ChatColor.YELLOW + "/ase adj <" + Util.getEnumList(AdjustmentMode.class) + ">";
	final String LISTSLOT = ChatColor.YELLOW + "/ase slot <1-9>";
	final String RELOAD = ChatColor.YELLOW + "/ase reload";
	final String HELP = ChatColor.YELLOW + "/ase help";
	final String VERSION = ChatColor.YELLOW + "/ase version";

	/*//Reload Stuff
	Material editTool;
	boolean requireToolData = false;
	boolean sendToActionBar = true;
	int editToolData = Integer.MIN_VALUE;
	boolean requireToolLore = false;
	String editToolLore = null;
	boolean debug = false; //weather or not to broadcast messages via print(String message)
	double coarseRot;
	double fineRot;
	boolean glowItemFrames;
	String toolType = null;
	LocalDateTime now = LocalDateTime.now();*/

	public CommandEx( ArmorStandEditorPlugin armorStandEditorPlugin) {
		this.plugin = armorStandEditorPlugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player
				&& checkPermission((Player) sender, "basic", true))) {
			sender.sendMessage(plugin.getLang().getMessage("noperm", "warn"));
			return true;
		}

		Player player = (Player) sender;
		if (args.length == 0) {
			player.sendMessage(LISTMODE);
			player.sendMessage(LISTAXIS);
			player.sendMessage(LISTSLOT);
			player.sendMessage(LISTADJUSTMENT);
			return true;
		}
		switch (args[0].toLowerCase()) {
			case "mode":
				commandMode(player, args);
				break;
			case "axis": commandAxis(player, args);
				break;
			case "adj": commandAdj(player, args);
				break;
			case "slot": commandSlot(player, args);
				break;
			/*case "reload": commandReload(player, args);
			    break;*/
			case "help":
			case "?": commandHelp(player);
				break;
			case "version": commandVersion(player);
				break;
			default:
				sender.sendMessage(LISTMODE);
				sender.sendMessage(LISTAXIS);
				sender.sendMessage(LISTSLOT);
				sender.sendMessage(LISTADJUSTMENT);
				//sender.sendMessage(RELOAD);
				sender.sendMessage(VERSION);
				sender.sendMessage(HELP);
		}
		return true;
	}

	private void commandVersion(Player player) {
		if (!(checkPermission(player, "basic", true))) return;
		String verString = plugin.pdfFile.getVersion();
		player.sendMessage(ChatColor.YELLOW + "[ArmorStandEditor] Version: " + verString);
	}

	//Reload Command Now Expanded Upon.
/*	private void commandReload(Player player, String[] args){
		if(!(checkPermission(player, "reload", true))) return; //Basic sanity Check for Reload Permission!
		if(args.length < 1 ){
			// Check the Length of Args. If > 0 then pass noReload
			player.sendMessage(plugin.getLang().getMessage("noreloadcom", "warn"));
			player.sendMessage(RELOAD);
		} else {
			// else if = 0 then get do one final check on the permission

			DateTimeFormatter format = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm:ss");

			if (checkPermission(player, "reload", true)) {
				// if permission true then run Reload and Load all the Values, Message that it has been reloaded successfully. Log to Console, Reload on DateTime by Player
				this.loadConfig();
				plugin.saveConfig();
				plugin.reloadConfig();
				player.sendMessage(plugin.getLang().getMessage("reloaded", "info"));
				plugin.log("Configuration File has reloaded on "+ now.format(format) +  " by " + player.getName() + "");
			}
		}
	}

	//Potential to add Validation In Here SOMEHOW? TO Validate that the file is good in that regard.
	private void loadConfig(){
		//Get all the Changes - Not accepting changes without a FULL RELOAD
		coarseRot = plugin.getConfig().getDouble("coarse");
		fineRot = plugin.getConfig().getDouble("fine");

		//Set Tool to be used in game
		toolType = plugin.getConfig().getString("tool");
		if (toolType != null) {
			editTool = Material.getMaterial(toolType); //Ignore Warning
		} else {
			plugin.getLogger().severe("Unable to get Tool for Use with Plugin. Unable to continue!");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}

		requireToolData = plugin.getConfig().getBoolean("requireToolData", false);
		if(requireToolData) editToolData = plugin.getConfig().getInt("toolData", Integer.MIN_VALUE);
		requireToolLore = plugin.getConfig().getBoolean("requireToolLore", false);
		if(requireToolLore) editToolLore= plugin.getConfig().getString("toolLore", null);

		debug = plugin.getConfig().getBoolean("debug", true);
		sendToActionBar = plugin.getConfig().getBoolean("sendMessagesToActionBar", true);
		glowItemFrames = plugin.getConfig().getBoolean("glowingItemFrame", true);

	}*/


	private void commandSlot(Player player, String[] args) {

		if (args.length <= 1) {
			player.sendMessage(plugin.getLang().getMessage("noslotnumcom", "warn"));
			player.sendMessage(LISTSLOT);
		}

		if (args.length > 1) {
			try {
				byte slot = (byte) (Byte.parseByte(args[1]) - 0b1);
				if (slot >= 0 && slot < 9) {
					plugin.editorManager.getPlayerEditor(player.getUniqueId()).setCopySlot(slot);
				} else {
					player.sendMessage(LISTSLOT);
				}

			} catch ( NumberFormatException nfe) {
				player.sendMessage(LISTSLOT);
			}
		}
	}

	private void commandAdj(Player player, String[] args) {
		if (args.length <= 1) {
			player.sendMessage(plugin.getLang().getMessage("noadjcom", "warn"));
			player.sendMessage(LISTADJUSTMENT);
		}

		if (args.length > 1) {
			for ( AdjustmentMode adj : AdjustmentMode.values()) {
				if (adj.toString().toLowerCase().contentEquals(args[1].toLowerCase())) {
					plugin.editorManager.getPlayerEditor(player.getUniqueId()).setAdjMode(adj);
					return;
				}
			}
			player.sendMessage(LISTADJUSTMENT);
		}
	}

	private void commandAxis( Player player,  String[] args) {
		if (args.length <= 1) {
			player.sendMessage(plugin.getLang().getMessage("noaxiscom", "warn"));
			player.sendMessage(LISTAXIS);
		}

		if (args.length > 1) {
			for ( Axis axis : Axis.values()) {
				if (axis.toString().toLowerCase().contentEquals(args[1].toLowerCase())) {
					plugin.editorManager.getPlayerEditor(player.getUniqueId()).setAxis(axis);
					return;
				}
			}
			player.sendMessage(LISTAXIS);
		}
	}

	private void commandMode( Player player,  String[] args) {
		if (args.length <= 1) {
			player.sendMessage(plugin.getLang().getMessage("nomodecom", "warn"));
			player.sendMessage(LISTMODE);
		}

		if (args.length > 1) {
			for ( EditMode mode : EditMode.values()) {
				if (mode.toString().toLowerCase().contentEquals(args[1].toLowerCase())) {
					if (args[1].equals("invisible") && !checkPermission(player, "invisible", true)) return;
					if (args[1].equals("itemframe") && !checkPermission(player, "invisible", true)) return;
					plugin.editorManager.getPlayerEditor(player.getUniqueId()).setMode(mode);
					return;
				}
			}
		}
	}

	private void commandHelp( Player player) {
		player.closeInventory();
		player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
		player.sendMessage(plugin.getLang().getMessage("help", "info", plugin.editTool.name()));
		player.sendMessage("");
		player.sendMessage(plugin.getLang().getMessage("helptips", "info"));
		player.sendMessage("");
		player.sendRawMessage(plugin.getLang().getMessage("helpurl", ""));
	}

	private boolean checkPermission( Player player, String permName,  boolean sendMessageOnInvalidation) {
		if (permName.toLowerCase().equals("paste")) {
			permName = "copy";
		}
		if (player.hasPermission("asedit." + permName.toLowerCase())) {
			return true;
		} else {
			if (sendMessageOnInvalidation) {
				player.sendMessage(plugin.getLang().getMessage("noperm", "warn"));
			}
			return false;
		}
	}
}