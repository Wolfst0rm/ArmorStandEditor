package io.github.rypofalem.armorstandeditor.api;

import io.github.rypofalem.armorstandeditor.api.interfaces.ItemFrameEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class ItemFrameManipulationEvent extends ItemFrameEvent implements Cancellable {

    @Getter
    @Setter
    private boolean isCancelled = false;

    @Getter
    protected final Player player;

    public ItemFrameManipulationEvent(ItemFrame itemFrame, Player player) {
        super(itemFrame);
        this.player = player;
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
