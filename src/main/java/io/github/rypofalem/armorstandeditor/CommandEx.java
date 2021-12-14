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
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEx implements CommandExecutor {
	ArmorStandEditorPlugin plugin;
	final String LISTMODE = ChatColor.YELLOW + "/ase mode <" + Util.getEnumList(EditMode.class) + ">";
	final String LISTAXIS = ChatColor.YELLOW + "/ase axis <" + Util.getEnumList(Axis.class) + ">";
	final String LISTADJUSTMENT = ChatColor.YELLOW + "/ase adj <" + Util.getEnumList(AdjustmentMode.class) + ">";
	final String LISTSLOT = ChatColor.YELLOW + "/ase slot <1-9>";
	final String HELP = ChatColor.YELLOW + "/ase help";
	final String VERSION = ChatColor.YELLOW + "/ase version";

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
				sender.sendMessage(VERSION);
				sender.sendMessage(HELP);
		}
		return true;
	}

	private void commandVersion(Player player) {
		if (!(checkPermission(player, "basic", true))) return;
		String verString = plugin.pdfFile.getVersion();
		plugin.print("Output of VerString: " + verString);
		player.sendMessage(ChatColor.YELLOW + "[ArmorStandEditor] Version: " + verString);
	}


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
					if (args[1].equals("invisible") && !checkPermission(player, "armorstand.invisible", true)) return;
					if (args[1].equals("itemframe") && !checkPermission(player, "itemframe.invisible", true)) return;
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
			plugin.print("Player '"+ player.getDisplayName() +" has Permission: asedit." + permName.toLowerCase());
			return true;
		} else {
			if (sendMessageOnInvalidation) {
				plugin.print("Player '"+ player.getDisplayName() +" does not have Permission: asedit." + permName.toLowerCase());
				player.sendMessage(plugin.getLang().getMessage("noperm", "warn"));
			}
			return false;
		}
	}
}