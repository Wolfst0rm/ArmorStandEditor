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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import io.github.rypofalem.armorstandeditor.modes.AdjustmentMode;
import io.github.rypofalem.armorstandeditor.modes.Axis;
import io.github.rypofalem.armorstandeditor.modes.EditMode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class CommandEx implements CommandExecutor, TabCompleter {
    ArmorStandEditorPlugin plugin;
    final String LISTMODE = ChatColor.YELLOW + "/ase mode <" + Util.getEnumList(EditMode.class) + ">";
    final String LISTAXIS = ChatColor.YELLOW + "/ase axis <" + Util.getEnumList(Axis.class) + ">";
    final String LISTADJUSTMENT = ChatColor.YELLOW + "/ase adj <" + Util.getEnumList(AdjustmentMode.class) + ">";
    final String LISTSLOT = ChatColor.YELLOW + "/ase slot <1-9>";
    final String HELP = ChatColor.YELLOW + "/ase help or /ase ?";
    final String VERSION = ChatColor.YELLOW + "/ase version";
    final String UPDATE = ChatColor.YELLOW + "/ase update";
    final String RELOAD = ChatColor.YELLOW + "/ase reload";
    final String GIVECUSTOMMODEL = ChatColor.YELLOW + "/ase give";
    final String GIVEPLAYERHEAD = ChatColor.YELLOW + "/ase playerhead <name>";
    Gson gson = new Gson();

    public CommandEx( ArmorStandEditorPlugin armorStandEditorPlugin) {
        this.plugin = armorStandEditorPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof ConsoleCommandSender){ //Short Term Fix to Support #
            if(args.length == 0){
                sender.sendMessage(VERSION);
                sender.sendMessage(HELP);
                sender.sendMessage(RELOAD);
            } else{
                switch(args[0].toLowerCase()) {
                    case "reload" -> commandReloadConsole(sender);
                    case "help", "?" -> commandHelpConsole(sender);
                    case "version" -> commandVersionConsole(sender);
                    default -> {
                        sender.sendMessage(plugin.getLang().getMessage("noconsolecom","warn"));
                    }
                }
                return true;
            }

        }

        if(sender instanceof Player && !getPermissionBasic( (Player) sender)){
            sender.sendMessage(plugin.getLang().getMessage("noperm", "warn"));
            return true;
        } else {

            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage(LISTMODE);
                player.sendMessage(LISTAXIS);
                player.sendMessage(LISTSLOT);
                player.sendMessage(LISTADJUSTMENT);
                player.sendMessage(VERSION);
                player.sendMessage(UPDATE);
                player.sendMessage(HELP);
                player.sendMessage(RELOAD);
                player.sendMessage(GIVECUSTOMMODEL);
                player.sendMessage(GIVEPLAYERHEAD);
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
                case "playerhead" -> commandGivePlayerHead(player, args);
                case "reload" -> commandReload(player);
                default -> {
                    sender.sendMessage(LISTMODE);
                    sender.sendMessage(LISTAXIS);
                    sender.sendMessage(LISTSLOT);
                    sender.sendMessage(LISTADJUSTMENT);
                    sender.sendMessage(VERSION);
                    sender.sendMessage(UPDATE);
                    sender.sendMessage(HELP);
                    sender.sendMessage(RELOAD);
                    sender.sendMessage(GIVECUSTOMMODEL);
                    sender.sendMessage(GIVEPLAYERHEAD);
                }
            }
            return true;
        }
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

    private void commandGivePlayerHead(Player player,String[] args) {
          if(plugin.getAllowedToRetrievePlayerHead() && checkPermission(player, "head", true)){

            if(args.length == 2){

                //Get the Player head Texture
                String skinTexture = getPlayerHeadTexture(args[1]);

                if(skinTexture == null){
                    player.sendMessage(plugin.getLang().getMessage("playerheaderror", "warn"));
                }

                //Create the ItemStack for the PlayerHead
                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);

                // Get the meta therefore
                SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
                assert playerHeadMeta != null;

                //Generate a Random UUID
                GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
                gameProfile.getProperties().put("textures", new Property("textures", skinTexture));
                Field profileField = null;

                try {
                    profileField = playerHeadMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(playerHeadMeta, gameProfile);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    player.sendMessage(plugin.getLang().getMessage("playerheaderror", "warn"));
                } finally {
                    if (profileField != null) {
                        profileField.setAccessible(false);
                    }
                }

                //Set the Display Name to be that of the Player Given
                playerHeadMeta.setDisplayName(args[1]);

                //Set the Item Meta
                playerHead.setItemMeta(playerHeadMeta);

                //Add the head to the Players Inventory + display PlayerHead Success Message
                player.getInventory().addItem(playerHead);
                player.sendMessage(plugin.getLang().getMessage("playerhead","info"));

                //Let Admins know this command has been ran
                for(Player onlineList : Bukkit.getOnlinePlayers()){
                    if(onlineList.hasPermission("asedit.permpack.admin") && plugin.getAdminOnlyNotifications()){
                        onlineList.sendMessage(ChatColor.YELLOW + "[ArmorStandEditor] " + player.getName() + "has just used the /ase playerhead command to get the head for " + args[1]);
                    }
                }
            }
        } else{
            player.sendMessage(plugin.getLang().getMessage("noplayerhead", "warn"));
        }
    }

    private String getPlayerHeadTexture(String playerName) {
        try {
            // Get the UUID of the Player in Question
            URL uuidURL = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            try (InputStreamReader uuidURLReader = new InputStreamReader(uuidURL.openStream())) {
                JsonObject uuidObject = gson.fromJson(uuidURLReader, JsonObject.class);
                String uuid = uuidObject.get("id").getAsString();


                // Get the Skin from that UUID
                URL skinURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                try (InputStreamReader skinURLReader = new InputStreamReader(skinURL.openStream())) {
                    JsonObject skinObject = gson.fromJson(skinURLReader, JsonObject.class);
                    JsonObject skinTextureProperty = skinObject.get("properties").getAsJsonArray().get(0).getAsJsonObject();
                    return skinTextureProperty.get("value").getAsString();
                }
            }
        } catch (IOException | IllegalStateException e) {
            return null;
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
                    if (args[1].equals("invisible") && !checkPermission(player, "togglearmorstandvisibility", true)) return;
                    if (args[1].equals("itemframe") && !checkPermission(player, "toggleitemframevisibility", true)) return;
                    plugin.editorManager.getPlayerEditor(player.getUniqueId()).setMode(mode);
                    return;
                }
            }
        }
    }

    private void commandHelp(Player player) {
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        player.sendMessage(plugin.getLang().getMessage("help", "info", plugin.editTool.name()));
        player.sendMessage("");
        player.sendMessage(plugin.getLang().getMessage("helptips", "info"));
        player.sendMessage("");
        player.sendRawMessage(plugin.getLang().getMessage("helpurl", ""));
        player.sendRawMessage(plugin.getLang().getMessage("helpdiscord", ""));
    }

    private void commandHelpConsole(CommandSender sender) {
        sender.sendMessage(plugin.getLang().getMessage("help", "info", plugin.editTool.name()));
        sender.sendMessage("");
        sender.sendMessage(plugin.getLang().getMessage("helptips", "info"));
        sender.sendMessage("");
        sender.sendMessage(plugin.getLang().getMessage("helpurl", "info"));
        sender.sendMessage(plugin.getLang().getMessage("helpdiscord", "info"));
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
        String verString = plugin.getArmorStandEditorVersion();
        player.sendMessage(ChatColor.YELLOW + "[ArmorStandEditor] Version: " + verString);
    }

    private void commandVersionConsole(CommandSender sender){
        String verString = plugin.getArmorStandEditorVersion();
        sender.sendMessage(ChatColor.YELLOW + "[ArmorStandEditor] Version: " + verString);
    }

    private void commandReload(Player player){
        if(!(getPermissionReload(player))) return;
        plugin.performReload();
        player.sendMessage(plugin.getLang().getMessage("reloaded", ""));
    }

    private void commandReloadConsole(CommandSender sender) {
        plugin.performReload();
        sender.sendMessage(plugin.getLang().getMessage("reloaded", "info"));
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
        return checkPermission(player, "basic", false);
    }

    private boolean getPermissionGive(Player player) { return checkPermission(player, "give", false); }

    private boolean getPermissionUpdate(Player player){
        return checkPermission(player, "update", false);
    }

    private boolean getPermissionReload(Player player) {
        return checkPermission(player, "reload", false);
    }

    private boolean getPermissionPlayerHead(Player player) { return checkPermission(player,"head",false); }

    //REFACTOR COMPLETION
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> argList = new ArrayList<>();
        Player player = (Player) sender;

        if (isCommandValid(command.getName())) {

            if (args.length == 1) {
                argList.add("mode");
                argList.add("axis");
                argList.add("adj");
                argList.add("slot");
                argList.add("help");
                argList.add("?");

                //Will Only work with permissions - #
                if(getPermissionGive(player)){
                    argList.add("give");
                }
                if(getPermissionUpdate(player)){
                    argList.add("update");
                }
                if(getPermissionReload(player)){
                    argList.add("reload");
                }
                if(getPermissionPlayerHead(player) && plugin.getAllowedToRetrievePlayerHead()){
                    argList.add("playerhead");
                }
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("mode")) {
                argList.addAll(getModeOptions());
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("axis")) {
                argList.addAll(getAxisOptions());
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("slot")) {
                argList.addAll(getSlotOptions());
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("adj")) {
                argList.addAll(getAdjOptions());
            }

            return argList.stream().filter(a -> a.startsWith(args[0])).toList();
        }

        return Collections.emptyList();
    }

    private boolean isCommandValid(String commandName) {
        return commandName.equalsIgnoreCase("ase") ||
                commandName.equalsIgnoreCase("armorstandeditor") ||
                commandName.equalsIgnoreCase("asedit");
    }

    private List<String> getModeOptions() {
        return List.of(
                "None", "Invisible", "ShowArms", "Gravity", "BasePlate",
                "Size", "Copy", "Paste", "Head", "Body", "LeftArm",
                "RightArm", "LeftLeg", "RightLeg", "Placement",
                "DisableSlots", "Rotate", "Equipment", "Reset",
                "ItemFrame", "ItemFrameGlow"
        );
    }

    private List<String> getAxisOptions() {
        return List.of("X", "Y", "Z");
    }

    private List<String> getSlotOptions() {
        return List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    }

    private List<String> getAdjOptions() {
        return List.of("Coarse", "Fine");
    }

}