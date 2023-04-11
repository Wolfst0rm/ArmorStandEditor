package io.github.rypofalem.armorstandeditor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Scheduler {

    private static Boolean IS_FOLIA = null;
    private static Object GLOBAL_REGION_SCHEDULER = null;
    private static Object ASYNC_SCHEDULER = null;

    public static <T> T callMethod(Class<?> clazz, Object object, String methodName, Class<?>[] parameterTypes, Object... args) {
        try {
            return (T) clazz.getDeclaredMethod(methodName, parameterTypes).invoke(object, args);
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    public static <T> T callMethod(Object object, String methodName, Class<?>[] parameterTypes, Object... args) {
        return callMethod(object.getClass(), object, methodName, parameterTypes, args);
    }

    public static <T> T callMethod(Object object, String methodName) {
        return callMethod(object.getClass(), null, methodName, new Class[]{});
    }

    public static <T> T callMethod(Class<?> clazz, String methodName) {
        return callMethod(clazz, null, methodName, new Class[]{});
    }

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

    public static Object getGlobalRegionScheduler() {
        if (GLOBAL_REGION_SCHEDULER == null) {
            GLOBAL_REGION_SCHEDULER = callMethod(Bukkit.class, "getGlobalRegionScheduler");
        }
        return GLOBAL_REGION_SCHEDULER;
    }

    public static Object getAsyncScheduler() {
        if (ASYNC_SCHEDULER == null) {
            ASYNC_SCHEDULER = callMethod(Bukkit.class, "getAsyncScheduler");
        }
        return ASYNC_SCHEDULER;
    }

    public static void runTask(Plugin plugin, Runnable runnable) {
        if (isFolia()) {
            Object globalRegionScheduler = getGlobalRegionScheduler();
            callMethod(globalRegionScheduler, "run", new Class[]{Plugin.class, Consumer.class}, plugin, (Consumer<?>) (task) -> runnable.run());
            return;
        }
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static void runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        if (isFolia()) {
            Object asyncScheduler = getAsyncScheduler();
            callMethod(asyncScheduler, "runNow", new Class[]{Plugin.class, Consumer.class}, plugin, (Consumer<?>) (task) -> runnable.run());
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static void runTaskTimer(Plugin plugin, Runnable runnable, long initialDelayTicks, long periodTicks) {
        if (isFolia()) {
            Object globalRegionScheduler = getGlobalRegionScheduler();
            callMethod(globalRegionScheduler, "runAtFixedRate", new Class[]{Plugin.class, Consumer.class, long.class, long.class},
                       plugin, (Consumer<?>) (task) -> runnable.run(), initialDelayTicks, periodTicks);
            return;
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, initialDelayTicks, periodTicks);
    }

    public static void runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long initialDelayTicks, long periodTicks) {
        if (isFolia()) {
            Object asyncScheduler = getAsyncScheduler();
            callMethod(asyncScheduler, "runAtFixedRate", new Class[]{Plugin.class, Consumer.class, long.class, long.class, TimeUnit.class},
                       plugin, (Consumer<?>) (task) -> runnable.run(), initialDelayTicks, periodTicks*50, TimeUnit.MILLISECONDS);
            return;
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, initialDelayTicks, periodTicks);
    }


    public static void runTaskLater(Plugin plugin, Runnable runnable, long delayedTicks) {
        if (isFolia()) {
            Object globalRegionScheduler = getGlobalRegionScheduler();
            callMethod(globalRegionScheduler, "runDelayed", new Class[]{Plugin.class, Consumer.class, long.class},
                       plugin, (Consumer<?>) (task) -> runnable.run(), delayedTicks);
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, runnable, delayedTicks);
    }

    public static void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delayedTicks) {
        if (isFolia()) {
            Object asyncScheduler = callMethod(Bukkit.class, "getAsyncScheduler");
            callMethod(asyncScheduler, "runDelayed", new Class[]{Plugin.class, Consumer.class, long.class, TimeUnit.class},
                       plugin, (Consumer<?>) (task) -> runnable.run(), delayedTicks*50, TimeUnit.MILLISECONDS);
            return;
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delayedTicks);
    }

    public static void runTaskForPlayer(Plugin plugin, Player player, Runnable runnable) {
        if (isFolia()) {
            Object entityScheduler = callMethod(player, "getScheduler");
            callMethod(entityScheduler, "run", new Class[]{Plugin.class, Consumer.class, Runnable.class},
                plugin, (Consumer<?>) (task) -> runnable.run(), null);
            return;
        }
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static void cancelTasks(Plugin plugin) {
        if (isFolia()) {
            Object asyncScheduler = getAsyncScheduler();
            Object globalRegionScheduler = getGlobalRegionScheduler();
            callMethod(asyncScheduler, "cancelTasks", new Class[]{Plugin.class}, plugin);
            callMethod(globalRegionScheduler, "cancelTasks", new Class[]{Plugin.class}, plugin);
            return;
        }
        Bukkit.getScheduler().cancelTasks(plugin);
    }
}
