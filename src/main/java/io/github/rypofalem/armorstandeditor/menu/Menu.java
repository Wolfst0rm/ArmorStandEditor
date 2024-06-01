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

package io.github.rypofalem.armorstandeditor.menu;

import io.github.rypofalem.armorstandeditor.ArmorStandEditorPlugin;
import io.github.rypofalem.armorstandeditor.PlayerEditor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Menu {
    private final Inventory menuInv;
    private final PlayerEditor pe;
    private static String name = "Armor Stand Editor Menu";

    public Menu(PlayerEditor pe) {
        this.pe = pe;
        name = pe.plugin.getLang().getMessage("mainmenutitle", "menutitle");
        menuInv = Bukkit.createInventory(pe.getManager().getMenuHolder(), 54, name);
        fillInventory();
    }

    private void fillInventory() {

        menuInv.clear();

        ItemStack xAxis;
        ItemStack yAxis;
        ItemStack zAxis;
        ItemStack coarseAdj;
        ItemStack fineAdj;
        ItemStack rotate = null;
        ItemStack headPos;
        ItemStack rightArmPos;
        ItemStack bodyPos;
        ItemStack leftArmPos;
        ItemStack reset;
        ItemStack showArms;
        ItemStack visibility;
        ItemStack size = null;
        ItemStack rightLegPos;
        ItemStack glowing;
        ItemStack leftLegPos;
        ItemStack plate = null;
        ItemStack copy = null;
        ItemStack paste = null;
        ItemStack slot1 = null;
        ItemStack slot2 = null;
        ItemStack slot3 = null;
        ItemStack slot4 = null;
        ItemStack help;
        ItemStack itemFrameVisible;
        ItemStack blankSlot;
        ItemStack presetItem = null;

        //Variables that need to be Initialized
        ItemStack place = null;
        ItemStack equipment = null;
        ItemStack disableSlots = null;
        ItemStack gravity = null;
        ItemStack playerHead = null;
        ItemStack toggleVulnerabilty = null;

        //Slots with No Value
        blankSlot = createIcon(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1),
            "blankslot", "");

        //Axis - X, Y, Z for Movement
        xAxis = createIcon(new ItemStack(Material.RED_CONCRETE, 1),
            "xaxis", "axis x");

        yAxis = createIcon(new ItemStack(Material.GREEN_CONCRETE, 1),
            "yaxis", "axis y");

        zAxis = createIcon(new ItemStack(Material.BLUE_CONCRETE, 1),
            "zaxis", "axis z");

        //Movement Speed
        coarseAdj = createIcon(new ItemStack(Material.COARSE_DIRT, 1),
            "coarseadj", "adj coarse");

        fineAdj = createIcon(new ItemStack(Material.SMOOTH_SANDSTONE),
            "fineadj", "adj fine");

        //Reset Changes
        reset = createIcon(new ItemStack(Material.WATER_BUCKET),
            "reset", "mode reset");

        //Which Part to Move
        headPos = createIcon(new ItemStack(Material.IRON_HELMET),
            "head", "mode head");

        bodyPos = createIcon(new ItemStack(Material.IRON_CHESTPLATE),
            "body", "mode body");

        leftLegPos = createIcon(new ItemStack(Material.IRON_LEGGINGS),
            "leftleg", "mode leftleg");

        rightLegPos = createIcon(new ItemStack(Material.IRON_LEGGINGS),
            "rightleg", "mode rightleg");

        leftArmPos = createIcon(new ItemStack(Material.STICK),
            "leftarm", "mode leftarm");

        rightArmPos = createIcon(new ItemStack(Material.STICK),
            "rightarm", "mode rightarm");

        showArms = createIcon(new ItemStack(Material.STICK),
            "showarms", "mode showarms");

        presetItem = createIcon(new ItemStack(Material.BOOKSHELF), "presetmenu", "mode preset");

        //Praise Start - Sikatsu and cowgod, Nicely spotted this being broken
        if (pe.getPlayer().hasPermission("asedit.togglearmorstandvisibility") ||
            pe.plugin.getArmorStandVisibility()) {
            visibility = new ItemStack(Material.POTION, 1);
            PotionMeta potionMeta = (PotionMeta) visibility.getItemMeta();
            PotionEffect effect = new PotionEffect(PotionEffectType.INVISIBILITY, 1, 0);
            if (potionMeta != null) {
                potionMeta.addCustomEffect(effect, true);
            }
            visibility.setItemMeta(potionMeta);
            createIcon(visibility, "invisible", "mode invisible");
        } else {
            visibility = blankSlot;
        }

        if (pe.getPlayer().hasPermission("asedit.toggleitemframevisibility") ||
            pe.plugin.getItemFrameVisibility()) {
            itemFrameVisible = new ItemStack(Material.ITEM_FRAME, 1);
            createIcon(itemFrameVisible, "itemframevisible", "mode itemframe");
        } else {
            itemFrameVisible = blankSlot;
        }

        //Praise end

        if (pe.getPlayer().hasPermission("asedit.toggleInvulnerability")) {
            toggleVulnerabilty = createIcon(new ItemStack(Material.TOTEM_OF_UNDYING, 1),
                "vulnerability", "mode vulnerability");
        } else {
            toggleVulnerabilty = blankSlot;
        }

        if (pe.getPlayer().hasPermission("asedit.togglesize")) {
            size = createIcon(new ItemStack(Material.PUFFERFISH, 1),
                "size", "mode size");
        } else {
            size = blankSlot;
        }

        if (pe.getPlayer().hasPermission("asedit.disableslots")) {
            disableSlots = createIcon(new ItemStack(Material.BARRIER), "disableslots", "mode disableslots");
        } else {
            disableSlots = blankSlot;
        }

        if (pe.getPlayer().hasPermission("asedit.togglegravity")) {
            gravity = createIcon(new ItemStack(Material.SAND), "gravity", "mode gravity");
        } else {
            gravity = blankSlot;
        }

        if (pe.getPlayer().hasPermission("asedit.togglebaseplate")) {
            plate = createIcon(new ItemStack(Material.SMOOTH_STONE_SLAB, 1),
                "baseplate", "mode baseplate");
        } else {
            plate = blankSlot;
        }

        if (pe.getPlayer().hasPermission("asedit.movement")) {
            place = createIcon(new ItemStack(Material.RAIL, 1),
                "placement", "mode placement");
        } else {
            place = blankSlot;
        }

        if (pe.getPlayer().hasPermission("asedit.rotation")) {
            rotate = createIcon(new ItemStack(Material.COMPASS, 1),
                "rotate", "mode rotate");
        } else {
            rotate = blankSlot;
        }

        if (pe.getPlayer().hasPermission("asedit.equipment")) {
            equipment = createIcon(new ItemStack(Material.CHEST, 1),
                "equipment", "mode equipment");
        } else {
            equipment = blankSlot;
        }

        if (pe.getPlayer().hasPermission("asedit.copy")) {
            copy = createIcon(new ItemStack(Material.FLOWER_BANNER_PATTERN),
                "copy", "mode copy");

            slot1 = createIcon(new ItemStack(Material.BOOK),
                "copyslot", "slot 1", "1");

            slot2 = createIcon(new ItemStack(Material.BOOK, 2),
                "copyslot", "slot 2", "2");

            slot3 = createIcon(new ItemStack(Material.BOOK, 3),
                "copyslot", "slot 3", "3");

            slot4 = createIcon(new ItemStack(Material.BOOK, 4),
                "copyslot", "slot 4", "4");
        }

        if (pe.getPlayer().hasPermission("asedit.paste")) {
            paste = createIcon(new ItemStack(Material.FEATHER),
                "paste", "mode paste");
        }

        if (pe.getPlayer().hasPermission("asedit.head") || pe.plugin.getallowedToRetrieveOwnPlayerHead()) {
            playerHead = createIcon(new ItemStack(Material.PLAYER_HEAD, 1),
                "playerheadmenu",
                "playerhead");
        } else {
            playerHead = blankSlot;
        }

        if (pe.getPlayer().hasPermission("asedit.togglearmorstandglow")) {
            glowing = createIcon(new ItemStack(Material.GLOW_INK_SAC, 1),
                "armorstandglow",
                "mode armorstandglow");
        } else {
            glowing = blankSlot;
        }

        help = createIcon(new ItemStack(Material.NETHER_STAR), "helpgui", "help");

        ItemStack[] items = {

            blankSlot, blankSlot, blankSlot, xAxis, yAxis, zAxis, blankSlot, blankSlot, help,
            copy, paste, blankSlot, playerHead, headPos, reset, blankSlot, itemFrameVisible, glowing,
            slot1, slot2, blankSlot, rightArmPos, bodyPos, leftArmPos, blankSlot, rotate, place,
            slot3, slot4, blankSlot, rightLegPos, equipment, leftLegPos, blankSlot, coarseAdj, fineAdj,
            presetItem, blankSlot, blankSlot, blankSlot, blankSlot, blankSlot, blankSlot, blankSlot, disableSlots,
            blankSlot, showArms, visibility, size, blankSlot, plate, toggleVulnerabilty, gravity, blankSlot
        };


        menuInv.setContents(items);
    }

    private ItemStack createIcon(ItemStack icon, String path, String command) {
        return createIcon(icon, path, command, null);
    }

    private ItemStack createIcon(ItemStack icon, String path, String command, String option) {
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;
        meta.getPersistentDataContainer().set(ArmorStandEditorPlugin.instance().getIconKey(), PersistentDataType.STRING, "ase " + command);
        meta.setDisplayName(getIconName(path, option));
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(getIconDescription(path, option));
        meta.setLore(loreList);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        icon.setItemMeta(meta);
        return icon;
    }


    private String getIconName(String path, String option) {
        return pe.plugin.getLang().getMessage(path, "iconname", option);
    }


    private String getIconDescription(String path, String option) {
        return pe.plugin.getLang().getMessage(path + ".description", "icondescription", option);
    }

    public void openMenu() {
        if (pe.getPlayer().hasPermission("asedit.basic")) {
            fillInventory();
            pe.getPlayer().openInventory(menuInv);
        }
    }

    public static String getName() {
        return name;
    }
}