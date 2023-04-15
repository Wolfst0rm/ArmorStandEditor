package io.github.rypofalem.armorstandeditor.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class ArmorStandManipulatedEvent extends ArmorStandEvent implements Cancellable {

    @Getter @Setter
    private boolean cancelled = false;

    @Getter
    protected final Player player;

    public ArmorStandManipulatedEvent(ArmorStand armorStand, Player player) {
        super(armorStand);
        this.player = player;
    }

    /* Generated for Bukkit */
    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList() { return (handlers); }
    @Override public HandlerList getHandlers() { return (handlers); }
}
