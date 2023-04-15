package io.github.rypofalem.armorstandeditor.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerOpenMenuEvent extends Event implements Cancellable {
    @Getter
    @Setter
    private boolean cancelled = false;

    @Getter
    protected final Player player;

    public PlayerOpenMenuEvent(Player player) {
        this.player = player;
    }

    /* Generated for Bukkit */
    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList() { return (handlers); }
    @Override public HandlerList getHandlers() { return (handlers); }
}