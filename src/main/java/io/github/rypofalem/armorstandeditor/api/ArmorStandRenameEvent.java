package io.github.rypofalem.armorstandeditor.api;

import io.github.rypofalem.armorstandeditor.api.interfaces.ArmorStandEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ArmorStandRenameEvent extends ArmorStandEvent implements Cancellable {


    @Getter
    @Setter
    private boolean isCancelled = false;
    @Getter
    @Setter
    protected String name;

    @Getter
    protected final Player player;

    public ArmorStandRenameEvent(ArmorStand armorStand, Player player, String name) {
        super(armorStand);
        this.player = player;
        this.name = name;
    }

    /* Generated for Bukkit */
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return (handlers);
    }

    @Override
    public HandlerList getHandlers() {
        return (handlers);
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
