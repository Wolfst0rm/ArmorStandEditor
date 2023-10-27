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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class IFEventTester implements Listener {
    Player player;

    //ItemFrameGlowEvent
    @EventHandler
    public void manipulateArmorStand(PlayerInteractAtEntityEvent IFGlowEvent) {
        player = IFGlowEvent.getPlayer();
        IFGlowEvent.setCancelled(true);
        if (IFGlowEvent.isCancelled()) {
            player.sendMessage("ItemFrameGlowEvent has been cancelled");
        } else {
            player.sendMessage("ItemFrameGlowEvent has not been cancelled. Continuing....");
        }
    }

    @EventHandler
    public void manipulateItemFrame(EntityDamageByEntityEvent IFManipulationLeftClickEvent) {
        player = (Player) IFManipulationLeftClickEvent.getDamager();
        IFManipulationLeftClickEvent.setCancelled(true);
        if (IFManipulationLeftClickEvent.isCancelled()) {
            player.sendMessage("ItemFrameManipulatedEvent has been cancelled");
        } else {
            player.sendMessage("ItemFrameManipulatedEvent has not been cancelled. Continuing....");
        }
    }

    @EventHandler
    public void manipulateItemFrameRightClick(PlayerInteractAtEntityEvent IFManipulationRightClickEvent) {
        player = IFManipulationRightClickEvent.getPlayer();
        IFManipulationRightClickEvent.setCancelled(true);
        if (IFManipulationRightClickEvent.isCancelled()) {
            player.sendMessage("ItemFrameManipulatedEvent has been cancelled");
        } else {
            player.sendMessage("ItemFrameManipulatedEvent has not been cancelled. Continuing....");
        }
    }

    @EventHandler
    public void targetEvent(PlayerSwapHandItemsEvent targetIFEvent) {
        player = targetIFEvent.getPlayer();
        targetIFEvent.setCancelled(true);
        if (targetIFEvent.isCancelled()) {
            player.sendMessage("ItemFrameTargetedEvent has been cancelled");
        } else {
            player.sendMessage("ItemFrameTargetedEvent has not been cancelled. Continuing....");
        }
    }

}
