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

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;

import io.github.rypofalem.armorstandeditor.modes.AdjustmentMode;
import io.github.rypofalem.armorstandeditor.modes.Axis;
import io.github.rypofalem.armorstandeditor.modes.EditMode;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandEx implements CommandExecutor, TabCompleter {
    ArmorStandEditorPlugin plugin;
    TextColor commandColor = TextColor.fromHexString("#FFFF33");

    final String LISTMODE =       "/ase mode <" + Util.getEnumList(EditMode.class) + ">";
    final String LISTAXIS =       "/ase axis <" + Util.getEnumList(Axis.class) + ">";
    final String LISTADJUSTMENT = "/ase adj <" + Util.getEnumList(AdjustmentMode.class) + ">";
    final String LISTSLOT =       "/ase slot <1-9>";
    final String HELP =           "/ase help or /ase ?";
    final String VERSION =        "/ase version";
    final String UPDATE =         "/ase update";
    final String RELOADCONFIG =   "/ase reload";
    final String GIVECUSTOMMODEL ="/ase give";

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

            player.sendMessage(Component.text(LISTMODE).color(commandColor));
            player.sendMessage(Component.text(LISTAXIS).color(commandColor));
            player.sendMessage(Component.text(LISTSLOT).color(commandColor));
            player.sendMessage(Component.text(LISTADJUSTMENT).color(commandColor));
            player.sendMessage(Component.text(GIVECUSTOMMODEL).color(commandColor));
            player.sendMessage(Component.text(RELOADCONFIG).color(commandColor));
            player.sendMessage(Component.text(VERSION).color(commandColor));
            player.sendMessage(Component.text(UPDATE).color(commandColor));
            player.sendMessage(Component.text(HELP).color(commandColor));

            return true;
        }
        switch (args[0].toLowerCase()) {
            case "mode" -> commandMode(player, args);
            case "axis" -> commandAxis(player, args);
            case "adj" -> commandAdj(player, args);
            case "slot" -> commandSlot(player, args);
            case "help", "?" -> commandHelp(player);
            case "version" -> commandVersion(player);
            case "update" -> commandUpdate(player);
            case "give" -> commandGive(player);
            case "reload" -> commandReload(player);
            default -> {
                player.sendMessage(Component.text(LISTMODE).color(commandColor));
                player.sendMessage(Component.text(LISTAXIS).color(commandColor));
                player.sendMessage(Component.text(LISTSLOT).color(commandColor));
                player.sendMessage(Component.text(LISTADJUSTMENT).color(commandColor));
                player.sendMessage(Component.text(GIVECUSTOMMODEL).color(commandColor));
                player.sendMessage(Component.text(RELOADCONFIG).color(commandColor));
                player.sendMessage(Component.text(VERSION).color(commandColor));
                player.sendMessage(Component.text(UPDATE).color(commandColor));
                player.sendMessage(Component.text(HELP).color(commandColor));
            }
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
            player.sendMessage(Component.text(LISTSLOT).color(commandColor));
        }

        if (args.length > 1) {
            try {
                byte slot = (byte) (Byte.parseByte(args[1]) - 0b1);
                if (slot >= 0 && slot < 9) {
                    plugin.editorManager.getPlayerEditor(player.getUniqueId()).setCopySlot(slot);
                } else {
                    player.sendMessage(Component.text(LISTSLOT).color(commandColor));
                }

            } catch ( NumberFormatException nfe) {
                player.sendMessage(Component.text(LISTSLOT).color(commandColor));
            }
        }
    }

    private void commandAdj(Player player, String[] args) {
        if (args.length <= 1) {
            player.sendMessage(plugin.getLang().getMessage("noadjcom", "warn"));
            player.sendMessage(Component.text(LISTADJUSTMENT).color(commandColor));
        }

        if (args.length > 1) {
            for ( AdjustmentMode adj : AdjustmentMode.values()) {
                if (adj.toString().toLowerCase().contentEquals(args[1].toLowerCase())) {
                    plugin.editorManager.getPlayerEditor(player.getUniqueId()).setAdjMode(adj);
                    return;
                }
            }
            player.sendMessage(Component.text(LISTADJUSTMENT).color(commandColor));
        }
    }

    private void commandAxis( Player player,  String[] args) {
        if (args.length <= 1) {
            player.sendMessage(plugin.getLang().getMessage("noaxiscom", "warn"));
            player.sendMessage(Component.text(LISTAXIS).color(commandColor));
        }

        if (args.length > 1) {
            for ( Axis axis : Axis.values()) {
                if (axis.toString().toLowerCase().contentEquals(args[1].toLowerCase())) {
                    plugin.editorManager.getPlayerEditor(player.getUniqueId()).setAxis(axis);
                    return;
                }
            }
            player.sendMessage(Component.text(LISTAXIS).color(commandColor));
        }
    }

    private void commandMode( Player player,  String[] args) {
        if (args.length <= 1) {
            player.sendMessage(plugin.getLang().getMessage("nomodecom", "warn"));
            player.sendMessage(Component.text(LISTMODE).color(commandColor));
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

    private void commandHelp(Player player) {
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        player.sendMessage(Component.text(plugin.getLang().getMessage("help", "info", plugin.editTool.name())));
        player.sendMessage("");
        player.sendMessage(Component.text(plugin.getLang().getMessage("helptips", "info")));
        player.sendMessage("");
        player.sendMessage(Component.text(plugin.getLang().getMessage("helpurl", "")));
        player.sendMessage(Component.text(plugin.getLang().getMessage("helpdiscord", "")));
    }

    private void commandUpdate(Player player) {
        if (!(checkPermission(player, "update", true))) return;

        //Only Run if the Update Command Works
        if (plugin.getArmorStandEditorVersion().contains(".x")) {
            player.sendMessage(Component.text("[ArmorStandEditor] Update Checker will not work on Development Versions.").color(commandColor));
            player.sendMessage(Component.text("[ArmorStandEditor] Report all bugs to: https://github.com/Wolfieheart/ArmorStandEditor/issues").color(commandColor));
        } else {
            if (!Scheduler.isFolia() && plugin.getRunTheUpdateChecker()) {
                new UpdateChecker(plugin, UpdateCheckSource.SPIGOT, "" + ArmorStandEditorPlugin.SPIGOT_RESOURCE_ID + "").checkNow(player); //Runs Update Check
            } else if (Scheduler.isFolia()) {
                player.sendMessage(Component.text("[ArmorStandEditor] Update Checker is currently not work on on Folia.").color(commandColor));
                player.sendMessage(Component.text("[ArmorStandEditor] Report all bugs to: https://github.com/Wolfieheart/ArmorStandEditor/issues").color(commandColor));
            } else {
                player.sendMessage(Component.text("[ArmorStandEditor] Update Checker is not enabled on this server").color(commandColor));
            }
        }
    }

    private void commandVersion(Player player) {
        if (!(getPermissionUpdate(player))) return;
        String verString = plugin.getArmorStandEditorVersion();
        player.sendMessage(Component.text("[ArmorStandEditor] Version: " + verString).color(commandColor));
    }

    private void commandReload(Player player){
        if(!(getPermissionReload(player))) return;
        plugin.performReload();
        player.sendMessage(plugin.getLang().getMessage("reloaded", ""));
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
    private boolean getPermissionReload(Player player) {
        return checkPermission(player, "reload", true);
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
                    argList.add("?");
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

                //Reload Permission Check
                if (getPermissionReload(player)){
                    argList.add("reload");
                }

                return argList.stream().filter(a -> a.startsWith(args[0])).toList();
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