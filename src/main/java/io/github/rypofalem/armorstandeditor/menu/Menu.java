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
import io.github.rypofalem.armorstandeditor.utils.Configuration;
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

public class Menu implements ItemFactory {
    private final Inventory menuInv;
    private final PlayerEditor pe;

    public Menu(PlayerEditor pe) {
        this.pe = pe;
        menuInv = Bukkit.createInventory(pe.getManager().getMenuHolder(), Configuration.getGUI().getInt("menu.size"), Configuration.color(Configuration.getGUI().getString("menu.title")));
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

        //Variables that need to be Initialized
        ItemStack place = null;
        ItemStack equipment = null;
        ItemStack disableSlots = null;
        ItemStack gravity = null;
        ItemStack playerHead = null;
        ItemStack toggleVulnerabilty = null;

        xAxis = createIcon(new ItemStack(Material.RED_WOOL, 1), "xaxis", "axis x");

        yAxis = createIcon(new ItemStack(Material.GREEN_WOOL, 1), "yaxis", "axis y");

        zAxis = createIcon(new ItemStack(Material.BLUE_WOOL, 1), "zaxis", "axis z");

        coarseAdj = createIcon(new ItemStack(Material.DIRT, 1), "coarseadj", "adj coarse");

        fineAdj = createIcon(new ItemStack(Material.SANDSTONE), "fineadj", "adj fine");

        reset = createIcon(new ItemStack(Material.LEVER), "reset", "mode reset");

        headPos = createIcon(new ItemStack(Material.LEATHER_HELMET), "head", "mode head");

        bodyPos = createIcon(new ItemStack(Material.LEATHER_CHESTPLATE), "body", "mode body");

        leftLegPos = createIcon(new ItemStack(Material.LEATHER_LEGGINGS), "leftleg", "mode leftleg");

        rightLegPos = createIcon(new ItemStack(Material.LEATHER_LEGGINGS), "rightleg", "mode rightleg");

        leftArmPos = createIcon(new ItemStack(Material.STICK), "leftarm", "mode leftarm");

        rightArmPos = createIcon(new ItemStack(Material.STICK), "rightarm", "mode rightarm");

        showArms = createIcon(new ItemStack(Material.STICK), "showarms", "mode showarms");

        //Praise Start - Sikatsu and cowgod, Nicely spotted this being broken
        if (pe.getPlayer().hasPermission("asedit.togglearmorstandvisibility") ||
                pe.plugin.getArmorStandVisibility()) {
            visibility = new ItemStack(Material.POTION, 1);
            PotionMeta potionMeta = (PotionMeta) visibility.getItemMeta();
            PotionEffect eff1 = new PotionEffect(PotionEffectType.INVISIBILITY, 1, 0);
            assert potionMeta != null;
            potionMeta.addCustomEffect(eff1, true);
            visibility.setItemMeta(potionMeta);
            createIcon(visibility, "invisible", "mode invisible");
        } else {
            visibility = null;
        }

        if (pe.getPlayer().hasPermission("asedit.toggleitemframevisibility") ||
                pe.plugin.getItemFrameVisibility()) {
            itemFrameVisible = new ItemStack(Material.ITEM_FRAME, 1);
            createIcon(itemFrameVisible, "itemframevisible", "mode itemframe");
        } else {
            itemFrameVisible = null;
        }

        //Praise end

        if (pe.getPlayer().hasPermission("asedit.toggleInvulnerability")) {
            toggleVulnerabilty = createIcon(new ItemStack(Material.BEDROCK, 1),
                    "vulnerability", "mode vulnerability");
        }

        if (pe.getPlayer().hasPermission("asedit.togglesize")) {
            size = createIcon(new ItemStack(Material.PUFFERFISH, 1),
                    "size", "mode size");
        }
        if (pe.getPlayer().hasPermission("asedit.disableslots")) {
            disableSlots = createIcon(new ItemStack(Material.BARRIER), "disableslots", "mode disableslots");
        }

        if (pe.getPlayer().hasPermission("asedit.togglegravity")) {
            gravity = createIcon(new ItemStack(Material.SAND), "gravity", "mode gravity");
        }

        if (pe.getPlayer().hasPermission("asedit.togglebaseplate")) {
            plate = createIcon(new ItemStack(Material.STONE_SLAB, 1),
                    "baseplate", "mode baseplate");
        }

        if (pe.getPlayer().hasPermission("asedit.movement")) {
            place = createIcon(new ItemStack(Material.MINECART, 1),
                    "placement", "mode placement");
        }

        if (pe.getPlayer().hasPermission("asedit.rotation")) {
            rotate = createIcon(new ItemStack(Material.COMPASS, 1),
                    "rotate", "mode rotate");
        }

        if (pe.getPlayer().hasPermission("asedit.equipment")) {
            equipment = createIcon(new ItemStack(Material.CHEST, 1),
                    "equipment", "mode equipment");
        }

        if (pe.getPlayer().hasPermission("asedit.copy")) {
            copy = createIcon(new ItemStack(Material.WRITABLE_BOOK),
                    "copy", "mode copy");

            slot1 = createIcon(new ItemStack(Material.DANDELION),
                    "copyslot", "slot 1", "1");

            slot2 = createIcon(new ItemStack(Material.AZURE_BLUET, 2),
                    "copyslot", "slot 2", "2");

            slot3 = createIcon(new ItemStack(Material.BLUE_ORCHID, 3),
                    "copyslot", "slot 3", "3");

            slot4 = createIcon(new ItemStack(Material.PEONY, 4),
                    "copyslot", "slot 4", "4");
        }

        if (pe.getPlayer().hasPermission("asedit.paste")){
            paste = createIcon(new ItemStack(Material.ENCHANTED_BOOK),
                    "paste", "mode paste");
        }

        if(pe.getPlayer().hasPermission("asedit.head") && pe.plugin.getAllowedToRetrievePlayerHead()){
            playerHead = createIcon(new ItemStack(Material.PLAYER_HEAD, 1),
                    "playerheadmenu",
                    "playerhead");
        }

        help = createIcon(new ItemStack(Material.NETHER_STAR), "helpgui", "help");

        ItemStack[] items =
                {
                        xAxis, yAxis, zAxis, null, coarseAdj, fineAdj, null, rotate, place,
                        null, headPos, playerHead, null, null, null, null, null, null,
                        rightArmPos, bodyPos, leftArmPos, reset, null, null, showArms, visibility, size,
                        rightLegPos, equipment, leftLegPos, null, null, toggleVulnerabilty, disableSlots, gravity, plate,
                        null, copy, paste, null, null, null, null, itemFrameVisible, null,
                        slot1, slot2, slot3, slot4, null, null, null, null, help
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
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
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
}