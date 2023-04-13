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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class Scheduler {

    private static Boolean IS_FOLIA = null;

    private static boolean methodExist(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            clazz.getDeclaredMethod(methodName, parameterTypes);
            return true;
        } catch (Throwable ignored) {}
        return false;
    }

    public static Boolean isFolia() {
        if (IS_FOLIA == null) IS_FOLIA = methodExist(Bukkit.class, "getGlobalRegionScheduler");
        return IS_FOLIA;
    }

    public static void runTaskTimer(Plugin plugin, Runnable runnable, long initialDelayTicks, long periodTicks) {
        if (isFolia()) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task -> runnable.run(),
                                                             initialDelayTicks, periodTicks);
            return;
        }
        Bukkit.getScheduler().runTaskTimer(plugin, runnable, initialDelayTicks, periodTicks);
    }

    public static void runTaskLater(Plugin plugin, Runnable runnable, long delayedTicks) {
        if (isFolia()) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), delayedTicks);
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, runnable, delayedTicks);
    }

    public static void teleport(Entity entity, Location location) {
        if (IS_FOLIA) entity.teleportAsync(location);
        else entity.teleport(location);
    }
}
