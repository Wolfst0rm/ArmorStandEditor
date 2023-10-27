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

package io.github.rypofalem.apiexample;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class ASEventTester implements Listener {

    Player player;

    //ArmorStandRenameEvent
    @EventHandler
    public void renameArmorStand(PlayerInteractAtEntityEvent ASRenameEvent) {
        player = ASRenameEvent.getPlayer();
        ASRenameEvent.setCancelled(true);
        if (ASRenameEvent.isCancelled()) {
            player.sendMessage("ArmorStandRenameEvent has been cancelled");
        } else {
            player.sendMessage("ArmorStandRenameEvent has not been cancelled. Continuing....");
        }
    }

    //ArmorStandManipEvent
    @EventHandler
    public void manipulateArmorStand(PlayerInteractAtEntityEvent ASManipEvent) {
        player = ASManipEvent.getPlayer();
        ASManipEvent.setCancelled(true);
        if (ASManipEvent.isCancelled()) {
            player.sendMessage("ArmorStandManipulationEvent has been cancelled");
        } else {
            player.sendMessage("ArmorStandManipulationEvent has not been cancelled. Continuing....");
        }
    }

    //ArmorStandTargetedEvent
    @EventHandler
    public void targetEvent(PlayerSwapHandItemsEvent targetASEvent) {
        player = targetASEvent.getPlayer();
        targetASEvent.setCancelled(true);
        if (targetASEvent.isCancelled()) {
            player.sendMessage("ArmorStandTargetedEvent has been cancelled");
        } else {
            player.sendMessage("ArmorStandTargetedEvent has not been cancelled. Continuing....");
        }
    }

    //PlayerOpenMenuEvent
    //onArmorStandDamage EntityDamageByEntityEvent event
    @EventHandler
    public void playerOpeningMenuEvent(EntityDamageByEntityEvent ASEDamageMenuOpenEvent) {
        player = (Player) ASEDamageMenuOpenEvent.getDamager();
        ASEDamageMenuOpenEvent.setCancelled(true);
        if (ASEDamageMenuOpenEvent.isCancelled()) {
            player.sendMessage("PlayerOpenMenuEvent has been cancelled");
        } else {
            player.sendMessage("PlayerOpenMenuEvent has not been cancelled. Continuing....");
        }
    }

    //Also PlayerOpenMenuEvent when RightClicking/Interacting
    @EventHandler
    public void playerOpeningMenuRightClickEvent(PlayerInteractEvent ASERightClickMenuOpenEvent) {
        player = ASERightClickMenuOpenEvent.getPlayer();
        ASERightClickMenuOpenEvent.setCancelled(true);
        if (ASERightClickMenuOpenEvent.isCancelled()) {
            player.sendMessage("PlayerOpenMenuEvent has been cancelled");
        } else {
            player.sendMessage("PlayerOpenMenuEvent has not been cancelled. Continuing....");
        }
    }
}
