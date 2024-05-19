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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;

public class PresetArmorPosesMenu {

    Inventory menuInv;
    private PlayerEditor pe;
    private ArmorStand armorstand;
    static String name;

    //PRESET NAMES
    final String SITTING = "§2§nSitting";
    final String WAVING = "§2§nWaving";
    final String GREETING_1 = "§2§nGreeting 1";
    final String GREETING_2 = "§2§nGreeting 2";
    final String CHEERS = "§2§nCheers";
    final String ARCHER = "§2§nArcher";
    final String DANCING = "§2§nDancing";
    final String HANG = "§2§nHanging";
    final String PRESENT = "§2§nPresent";
    final String FISHING = "§2§nFishing";

    //Menu Stuff
    final String BACKTOMENU = "§2§nBack to Menu";
    final String HOWTO = "§2§nHow To";

    public PresetArmorPosesMenu(PlayerEditor pe, ArmorStand as) {
        this.pe = pe;
        this.armorstand = as;
        name = pe.plugin.getLang().getMessage("presettitle", "menutitle");
        menuInv = Bukkit.createInventory(pe.getManager().getPresetHolder(), 36, name);
    }

    private void fillInventory() {
        menuInv.clear();

        /**
         * Menu Set up in a similar way as to how we do it for
         * the actual ArmorStand menu
         */

        //Blank Slots
        ItemStack blank = createIcon(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1), "blankslot");

        //Presets -- Here to test things out, will get better names soon TM
        ItemStack sitting = createIcon(new ItemStack(Material.ARMOR_STAND, 1), "sitting");
        ItemStack waving = createIcon(new ItemStack(Material.ARMOR_STAND, 2), "waving");
        ItemStack greet1 = createIcon(new ItemStack(Material.ARMOR_STAND, 3), "greeting 1");
        ItemStack greet2 = createIcon(new ItemStack(Material.ARMOR_STAND, 4), "greeting 2");
        ItemStack cheer = createIcon(new ItemStack(Material.ARMOR_STAND, 5), "cheers");
        ItemStack archer = createIcon(new ItemStack(Material.ARMOR_STAND, 6), "archer");
        ItemStack dancing = createIcon(new ItemStack(Material.ARMOR_STAND, 7), "dancing");
        ItemStack hanging = createIcon(new ItemStack(Material.ARMOR_STAND, 8), "hanging");
        ItemStack present = createIcon(new ItemStack(Material.ARMOR_STAND, 9), "present");
        ItemStack fishing = createIcon(new ItemStack(Material.ARMOR_STAND, 10), "fishing");

        //Utilities
        ItemStack backToMenu = createIcon(new ItemStack(Material.RED_WOOL, 1), "backtomenu");
        ItemStack howToPreset = createIcon(new ItemStack(Material.BOOK, 1), "howtopreset");

        //Build for the Menu ---- DO NOT MODIFY THIS UNLESS YOU KNOW WHAT YOU ARE DOING!
        ItemStack[] items = {
            blank, blank, blank, blank, blank, blank, blank, blank, blank,
            blank, backToMenu, sitting, waving, greet1, greet2, cheer, archer, blank,
            blank, howToPreset, dancing, hanging, present, fishing, blank, blank, blank,
            blank, blank, blank, blank, blank, blank, blank, blank, blank
        };

        menuInv.setContents(items);
    }

    private ItemStack createIcon(ItemStack icon, String path) {
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;
        meta.setDisplayName(getIconName(path));
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(getIconDescription(path));
        meta.setLore(loreList);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        icon.setItemMeta(meta);
        return icon;
    }

    private String getIconName(String path) {
        return pe.plugin.getLang().getMessage(path, "iconname");
    }

    private String getIconDescription(String path) {
        return pe.plugin.getLang().getMessage(path + ".description", "icondescription");
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

    public void handlePresetPose(String itemName, Player player) {
        if (itemName == null) return;
        if (player == null) return;
        switch (itemName) {
            case SITTING:
                setPresetPose(player, 345, 0, 10, 350, 0, 350, 280, 20, 0, 280, 340, 0, 0, 0, 0, 0, 0, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                player.closeInventory();
                break;
            case WAVING:
                setPresetPose(player, 220, 20, 0, 350, 0, 350, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                player.closeInventory();
                break;
            case GREETING_1:
                setPresetPose(player, 260, 20, 0, 260, 340, 0, 340, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                player.closeInventory();
                break;
            case GREETING_2:
                setPresetPose(player, 260, 10, 0, 260, 350, 0, 320, 0, 0, 10, 0, 0, 340, 0, 350, 0, 0, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                player.closeInventory();
                break;
            case CHEERS:
                setPresetPose(player, 250, 60, 0, 20, 10, 0, 10, 0, 0, 350, 0, 0, 340, 0, 0, 0, 0, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                player.closeInventory();
                break;
            case ARCHER:
                setPresetPose(player, 270, 350, 0, 280, 50, 0, 340, 0, 10, 20, 0, 350, 0, 0, 0, 0, 0, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                player.closeInventory();
                break;
            case DANCING:
                setPresetPose(player, 14, 0, 110, 20, 0, 250, 250, 330, 0, 15, 330, 0, 350, 350, 0, 0, 0, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                player.closeInventory();
                break;
            case HANG:
                setPresetPose(player, 1, 33, 67, -145, -33, -4, -42, 21, 1, -100, 0, -1, -29, -38, -18, 0, -4, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                player.closeInventory();
                break;
            case PRESENT:
                setPresetPose(player, 280, 330, 0, 10, 0, 350, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                player.closeInventory();
                break;
            case FISHING:
                setPresetPose(player, 300, 320, 0, 300, 40, 0, 280, 20, 0, 280, 340, 0, 0, 0, 0, 0, 0, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                player.closeInventory();
                break;
            case BACKTOMENU:
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                player.closeInventory();
                pe.openMenu();
                break;
            case HOWTO:
                player.sendMessage(pe.plugin.getLang().getMessage("howtopresetmsg"));
                player.sendMessage(pe.plugin.getLang().getMessage("helpurl"));
                player.sendMessage(pe.plugin.getLang().getMessage("helpdiscord"));
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                player.closeInventory();
                break;
        }
    }

    public void setPresetPose(Player player, double rightArmRoll, double rightArmYaw, double rightArmPitch,
        double leftArmRoll, double leftArmYaw, double leftArmPitch,
        double rightLegRoll, double rightLegYaw, double rightLegPitch,
        double leftLegRoll, double LeftLegYaw, double llp_yaw,
        double headRoll, double headYaw, double headPitch,
        double bodyRoll, double bodyYaw, double bodyPitch) {

        for (Entity theArmorStand : player.getNearbyEntities(1, 1, 1)) {
            if (theArmorStand instanceof ArmorStand armorStand) {
                if (!player.hasPermission("asedit.basic")) return;

                //Do the right positions based on what is given
                rightArmRoll = Math.toRadians(rightArmRoll);
                rightArmYaw = Math.toRadians(rightArmYaw);
                rightArmPitch = Math.toRadians(rightArmPitch);
                EulerAngle rightArmEulerAngle = new EulerAngle(rightArmRoll, rightArmYaw, rightArmPitch);
                armorStand.setRightArmPose(rightArmEulerAngle);

                // Calculate and set left arm settings
                leftArmRoll = Math.toRadians(leftArmRoll);
                leftArmYaw = Math.toRadians(leftArmYaw);
                leftArmPitch = Math.toRadians(leftArmPitch);
                EulerAngle leftArmEulerAngle = new EulerAngle(leftArmRoll, leftArmYaw, leftArmPitch);
                armorStand.setLeftArmPose(leftArmEulerAngle);

                // Calculate and set right leg settings
                rightLegRoll = Math.toRadians(rightLegRoll);
                rightLegYaw = Math.toRadians(rightLegYaw);
                rightLegPitch = Math.toRadians(rightLegPitch);
                EulerAngle rightLegEulerAngle = new EulerAngle(rightLegRoll, rightLegYaw, rightLegPitch);
                armorStand.setRightLegPose(rightLegEulerAngle);

                // Calculate and set left leg settings
                leftLegRoll = Math.toRadians(leftLegRoll);
                LeftLegYaw = Math.toRadians(LeftLegYaw);
                llp_yaw = Math.toRadians(llp_yaw);
                EulerAngle leftLegEulerAngle = new EulerAngle(leftLegRoll, LeftLegYaw, llp_yaw);
                armorStand.setLeftLegPose(leftLegEulerAngle);

                // Calculate and set body settings
                bodyRoll = Math.toRadians(bodyRoll);
                bodyYaw = Math.toRadians(bodyYaw);
                bodyPitch = Math.toRadians(bodyPitch);
                EulerAngle bodyEulerAngle = new EulerAngle(bodyRoll, bodyYaw, bodyPitch);
                armorStand.setBodyPose(bodyEulerAngle);

                // Calculate and set head settings
                headRoll = Math.toRadians(headRoll);
                headYaw = Math.toRadians(headYaw);
                headPitch = Math.toRadians(headPitch);
                EulerAngle headEulerAngle = new EulerAngle(headRoll, headYaw, headPitch);
                armorStand.setHeadPose(headEulerAngle);
            }
        }


    }

}
