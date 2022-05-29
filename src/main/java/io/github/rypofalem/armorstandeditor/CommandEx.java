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

import de.jeff_media.updatechecker.UpdateChecker;
import io.github.rypofalem.armorstandeditor.modes.AdjustmentMode;
import io.github.rypofalem.armorstandeditor.modes.Axis;
import io.github.rypofalem.armorstandeditor.modes.EditMode;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandEx implements CommandExecutor {
    ArmorStandEditorPlugin plugin;
    final String LISTMODE = ChatColor.YELLOW + "/ase mode <" + Util.getEnumList(EditMode.class) + ">";
    final String LISTAXIS = ChatColor.YELLOW + "/ase axis <" + Util.getEnumList(Axis.class) + ">";
    final String LISTADJUSTMENT = ChatColor.YELLOW + "/ase adj <" + Util.getEnumList(AdjustmentMode.class) + ">";
    final String LISTSLOT = ChatColor.YELLOW + "/ase slot <1-9>";
    final String HELP = ChatColor.YELLOW + "/ase help";
    final String VERSION = ChatColor.YELLOW + "/ase version";
    final String UPDATE = ChatColor.YELLOW + "/ase update";
    final String GIVECUSTOMMODEL = ChatColor.YELLOW + "/ase give";
    String debugPlayerDisplayName;

    public CommandEx( ArmorStandEditorPlugin armorStandEditorPlugin) {
        this.plugin = armorStandEditorPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player
                && checkPermission((Player) sender, "basic", true))) {
            sender.sendMessage(plugin.getLang().getMessage("noperm", "warn"));
            plugin.log("CommandSender is not an instance of Player");
            return true;
        }

        Player player = (Player) sender;
        debugPlayerDisplayName = player.getDisplayName();
        if (args.length == 0) {
            plugin.log("Sending List of Commands to: " + debugPlayerDisplayName);
            player.sendMessage(LISTMODE);
            player.sendMessage(LISTAXIS);
            player.sendMessage(LISTSLOT);
            player.sendMessage(LISTADJUSTMENT);
            player.sendMessage(VERSION);
            player.sendMessage(UPDATE);
            player.sendMessage(HELP);
            player.sendMessage(GIVECUSTOMMODEL);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "mode": commandMode(player, args);
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
            case "update": commandUpdate(player);
                break;
            case "give": commandGive(player);
                break;
            default:
                plugin.log("Sending List of Commands to: " + debugPlayerDisplayName);
                sender.sendMessage(LISTMODE);
                sender.sendMessage(LISTAXIS);
                sender.sendMessage(LISTSLOT);
                sender.sendMessage(LISTADJUSTMENT);
                sender.sendMessage(VERSION);
                sender.sendMessage(UPDATE);
                sender.sendMessage(HELP);
                sender.sendMessage(GIVECUSTOMMODEL);
        }
        return true;
    }

    // Implemented to fix:
    // https://github.com/Wolfieheart/ArmorStandEditor-Issues/issues/35 &
    // https://github.com/Wolfieheart/ArmorStandEditor-Issues/issues/30 - See Remarks OTHER
    private void commandGive(Player player) {
        if (plugin.getAllowCustomModelData() && checkPermission(player, "give", true)) {
            plugin.log("Does Plugin Allow CustomModelData?: " + plugin.getAllowCustomModelData());
            plugin.log("CustomModelData Int is: " + plugin.getCustomModelDataInt());
            ItemStack stack = new ItemStack(plugin.getEditTool()); //Only Support EditTool at the MOMENT
            ItemMeta meta = stack.getItemMeta();
            meta.setCustomModelData(plugin.getCustomModelDataInt());
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            stack.setItemMeta(meta);
            player.getInventory().addItem(stack);
            player.sendMessage(plugin.getLang().getMessage("give", "info"));
        } else{
            player.sendMessage(plugin.getLang().getMessage("nogive", "warn"));
            player.sendMessage(GIVECUSTOMMODEL);
        }
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
                    plugin.log("Copying ArmorStand to slot: " + slot);
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
                    plugin.log("Adjustment mode set to: " + adj);
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
                    plugin.log("Axis set to: " + axis);
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
                    plugin.log("Argument 1 is: " + args[1].toLowerCase());
                    if (args[1].equals("invisible") && !checkPermission(player, "armorstand.invisible", true)) return;
                    if (args[1].equals("itemframe") && !checkPermission(player, "itemframe.invisible", true)) return;
                    plugin.editorManager.getPlayerEditor(player.getUniqueId()).setMode(mode);
                    plugin.log("Mode set to '" + mode + "' for player '" + debugPlayerDisplayName + "'");
                    return;
                }
            }
        }
    }

    private void commandHelp( Player player) {
        plugin.log("Player '" + debugPlayerDisplayName + "' has ran the help command, closing Inventory");
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        player.sendMessage(plugin.getLang().getMessage("help", "info", plugin.editTool.name()));
        player.sendMessage("");
        player.sendMessage(plugin.getLang().getMessage("helptips", "info"));
        player.sendMessage("");
        player.sendRawMessage(plugin.getLang().getMessage("helpurl", ""));
    }

    private void commandUpdate(Player player) {
        if(!(checkPermission(player, "update", true))) return;
        plugin.log("Running the Built in UpdateChecker - Activated by '" + debugPlayerDisplayName + "'");
        UpdateChecker.getInstance().checkNow(player);
    }

    private void commandVersion(Player player) {
        if (!(checkPermission(player, "update", true))) return;
        String verString = plugin.pdfFile.getVersion();
        plugin.log("Output of VerString: " + verString);
        player.sendMessage(ChatColor.YELLOW + "[ArmorStandEditor] Version: " + verString);
        UpdateChecker.getInstance().checkNow(player);
    }



    private boolean checkPermission( Player player, String permName,  boolean sendMessageOnInvalidation) {
        if (permName.equalsIgnoreCase("paste")) {
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