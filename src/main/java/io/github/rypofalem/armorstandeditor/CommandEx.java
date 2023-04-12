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

//UpdateChecker
import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;

//Plugin Self
import io.github.rypofalem.armorstandeditor.modes.AdjustmentMode;
import io.github.rypofalem.armorstandeditor.modes.Axis;
import io.github.rypofalem.armorstandeditor.modes.EditMode;

//Bukkit
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

//Java
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandEx implements CommandExecutor, TabCompleter {
    ArmorStandEditorPlugin plugin;
    final String LISTMODE = ChatColor.YELLOW + "/ase mode <" + Util.getEnumList(EditMode.class) + ">";
    final String LISTAXIS = ChatColor.YELLOW + "/ase axis <" + Util.getEnumList(Axis.class) + ">";
    final String LISTADJUSTMENT = ChatColor.YELLOW + "/ase adj <" + Util.getEnumList(AdjustmentMode.class) + ">";
    final String LISTSLOT = ChatColor.YELLOW + "/ase slot <1-9>";
    final String HELP = ChatColor.YELLOW + "/ase help";
    final String VERSION = ChatColor.YELLOW + "/ase version";
    final String UPDATE = ChatColor.YELLOW + "/ase update";
    final String GIVECUSTOMMODEL = ChatColor.YELLOW + "/ase give";

    public CommandEx( ArmorStandEditorPlugin armorStandEditorPlugin) {
        this.plugin = armorStandEditorPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player
                && getPermissionBasic( (Player) sender))) {
            sender.sendMessage(plugin.getLang().getMessage("noperm", "warn"));
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
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
            ItemStack stack = new ItemStack(plugin.getEditTool()); //Only Support EditTool at the MOMENT
            ItemMeta meta = stack.getItemMeta();
            Objects.requireNonNull(meta).setCustomModelData(plugin.getCustomModelDataInt());
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            stack.setItemMeta(meta);
            player.getInventory().addItem(stack);
            player.sendMessage(plugin.getLang().getMessage("give", "info"));
        } else{
            player.sendMessage(plugin.getLang().getMessage("nogive", "warn"));
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

    private void commandUpdate(Player player) {
        if (!(checkPermission(player, "update", true))) return;

        //Only Run if the Update Command Works
        if (plugin.getArmorStandEditorVersion().contains(".x")) {
            player.sendMessage(ChatColor.YELLOW + "[ArmorStandEditor] Update Checker will not work on Development Versions.");
            player.sendMessage(ChatColor.YELLOW + "[ArmorStandEditor] Report all bugs to: https://github.com/Wolfieheart/ArmorStandEditor/issues");
        } else {
            if (!Scheduler.isFolia() && plugin.getRunTheUpdateChecker()) {
                new UpdateChecker(plugin, UpdateCheckSource.SPIGOT, "" + ArmorStandEditorPlugin.SPIGOT_RESOURCE_ID + "").checkNow(player); //Runs Update Check
            } else if (Scheduler.isFolia()) {
                player.sendMessage(ChatColor.YELLOW + "[ArmorStandEditor] Update Checker does not currently work on Folia.");
                player.sendMessage(ChatColor.YELLOW + "[ArmorStandEditor] Report all bugs to: https://github.com/Wolfieheart/ArmorStandEditor/issues");
            } else {
                player.sendMessage(ChatColor.YELLOW + "[ArmorStandEditor] Update Checker is not enabled on this server");
            }
        }
    }

    private void commandVersion(Player player) {
        if (!(getPermissionUpdate(player))) return;
        String verString = plugin.pdfFile.getVersion();
        player.sendMessage(ChatColor.YELLOW + "[ArmorStandEditor] Version: " + verString);
    }


    private boolean checkPermission(Player player, String permName,  boolean sendMessageOnInvalidation) {
        if (permName.equalsIgnoreCase("paste")) {
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

    private boolean getPermissionBasic(Player player) {
        return checkPermission(player, "basic", true);
    }

    private boolean getPermissionUpdate(Player player){
        return checkPermission(player, "update", true);
    }

    private boolean getPermissionGive(Player player){
        return checkPermission(player, "give", true);
    }

    //REFACTOR COMPLETION
    @Override
    @SuppressWarnings({"deprecated"})
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ase") || command.getName().equalsIgnoreCase("armorstandeditor") || command.getName().equalsIgnoreCase("asedit")) {
            List<String> argList = new ArrayList<>();

            //Needed for Permission Checks
            Player player = (Player) sender;

            if (args.length == 1 && getPermissionBasic(player)) {



                //Basic Permission Check
                if (getPermissionBasic(player)) {
                    argList.add("mode");
                    argList.add("axis");
                    argList.add("adj");
                    argList.add("slot");
                    argList.add("help");
                }

                //Update Permission Check
                if (getPermissionUpdate(player)) {
                    argList.add("update");
                    argList.add("version");
                }

                //Give Permission Check
                if (getPermissionGive(player)) {
                    argList.add("give");
                }

                return argList.stream().filter(a -> a.startsWith(args[0])).collect(Collectors.toList());
            }

            //Options for Mode
            if (args.length == 2 && args[0].equalsIgnoreCase("mode")){
                argList.add("None");
                argList.add("Invisible");
                argList.add("ShowArms");
                argList.add("Gravity");
                argList.add("BasePlate");
                argList.add("Size");
                argList.add("Copy");
                argList.add("Paste");
                argList.add("Head");
                argList.add("Body");
                argList.add("LeftArm");
                argList.add("RightArm");
                argList.add("LeftLeg");
                argList.add("RightLeg");
                argList.add("Placement");
                argList.add("DisableSlots");
                argList.add("Rotate");
                argList.add("Equipment");
                argList.add("Reset");
                argList.add("ItemFrame");
                argList.add("ItemFrameGlow");

                return argList; //New List
            }

            if(args.length == 2 && args[0].equalsIgnoreCase("axis")){
                argList.add("X");
                argList.add("Y");
                argList.add("Z");
                return argList; //New List
            }

            if(args.length == 2 && args[0].equalsIgnoreCase("slot")) {
                argList.add("0");
                argList.add("1");
                argList.add("2");
                argList.add("3");
                argList.add("4");
                argList.add("5");
                argList.add("6");
                argList.add("7");
                argList.add("8");
                argList.add("9");
                return argList; //New List
            }

            if(args.length == 2 && args[0].equalsIgnoreCase("adj")) {
                argList.add("Coarse");
                argList.add("Fine");
                return argList; //New List
            }

            return argList; //Empty List
        }

        return null; //Default
    }
}