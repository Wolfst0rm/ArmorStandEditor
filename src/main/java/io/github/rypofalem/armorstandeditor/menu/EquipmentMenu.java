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

import io.github.rypofalem.armorstandeditor.PlayerEditor;
import io.github.rypofalem.armorstandeditor.utils.Configuration;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class EquipmentMenu implements ItemFactory {
    Inventory menuInv;
    private final PlayerEditor pe;
    private final ArmorStand armorstand;
    ItemStack helmet, chest, pants, feetsies, rightHand, leftHand;

    public EquipmentMenu(PlayerEditor pe, ArmorStand as) {
        this.pe = pe;
        this.armorstand = as;
        menuInv = Bukkit.createInventory(pe.getManager().getEquipmentHolder(), Configuration.getGUI().getInt("equipment.size"), Configuration.color(Configuration.getGUI().getString("equipment.title")));
    }

    private void fillInventory() {
        menuInv.clear();
        EntityEquipment equipment = armorstand.getEquipment();
        assert equipment != null;
        ItemStack helmet = equipment.getHelmet();
        ItemStack chest = equipment.getChestplate();
        ItemStack pants = equipment.getLeggings();
        ItemStack feetsies = equipment.getBoots();
        ItemStack rightHand = equipment.getItemInMainHand();
        ItemStack leftHand = equipment.getItemInOffHand();
        equipment.clear();

        ItemStack disabledIcon = this.createItem(Configuration.getGUI().getConfigurationSection("equipment.disabled-icon"), (Inventory) null, null);
        ItemMeta meta = disabledIcon.getItemMeta();
        meta.getPersistentDataContainer().set(pe.plugin.getIconKey(), PersistentDataType.STRING, "ase icon"); // mark as icon
        disabledIcon.setItemMeta(meta);

        for (String string : Configuration.getGUI().getStringList("equipment.disabled-slots")) {
            int slot = NumberUtils.toInt(string, -1);
            if (slot == -1) continue;

            menuInv.setItem(slot, disabledIcon);
        }
        this.createItem(Configuration.getGUI().getConfigurationSection("equipment.items.headIcon"), menuInv, this::createIcon);
        this.createItem(Configuration.getGUI().getConfigurationSection("equipment.items.chestIcon"), menuInv, this::createIcon);
        this.createItem(Configuration.getGUI().getConfigurationSection("equipment.items.legsIcon"), menuInv, this::createIcon);
        this.createItem(Configuration.getGUI().getConfigurationSection("equipment.items.feetIcon"), menuInv, this::createIcon);
        this.createItem(Configuration.getGUI().getConfigurationSection("equipment.items.rightHandIcon"), menuInv, this::createIcon);
        this.createItem(Configuration.getGUI().getConfigurationSection("equipment.items.leftHandIcon"), menuInv, this::createIcon);
        menuInv.setItem(Configuration.getGUI().getInt("equipment.slots.helmet"), helmet);
        menuInv.setItem(Configuration.getGUI().getInt("equipment.slots.chestplate"), chest);
        menuInv.setItem(Configuration.getGUI().getInt("equipment.slots.leggings"), pants);
        menuInv.setItem(Configuration.getGUI().getInt("equipment.slots.boots"), feetsies);
        menuInv.setItem(Configuration.getGUI().getInt("equipment.slots.rightHand"), rightHand);
        menuInv.setItem(Configuration.getGUI().getInt("equipment.slots.leftHand"), leftHand);
    }

    private ItemStack createIcon(ItemStack icon) {
        ItemMeta meta = icon.getItemMeta();
        meta.getPersistentDataContainer().set(pe.plugin.getIconKey(), PersistentDataType.STRING, "ase icon");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        icon.setItemMeta(meta);
        return icon;
    }

    public void open() {
        fillInventory();
        pe.getPlayer().openInventory(menuInv);
    }

    public void equipArmorstand() {
        helmet = menuInv.getItem(Configuration.getGUI().getInt("equipment.slots.helmet"));
        chest = menuInv.getItem(Configuration.getGUI().getInt("equipment.slots.chestplate"));
        pants = menuInv.getItem(Configuration.getGUI().getInt("equipment.slots.leggings"));
        feetsies = menuInv.getItem(Configuration.getGUI().getInt("equipment.slots.boots"));
        rightHand = menuInv.getItem(Configuration.getGUI().getInt("equipment.slots.rightHand"));
        leftHand = menuInv.getItem(Configuration.getGUI().getInt("equipment.slots.leftHand"));
        armorstand.getEquipment().setHelmet(helmet);
        armorstand.getEquipment().setChestplate(chest);
        armorstand.getEquipment().setLeggings(pants);
        armorstand.getEquipment().setBoots(feetsies);
        armorstand.getEquipment().setItemInMainHand(rightHand);
        armorstand.getEquipment().setItemInOffHand(leftHand);
    }
}
