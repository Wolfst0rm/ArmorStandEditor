package io.github.rypofalem.armorstandeditor.api.interfaces;

import io.github.rypofalem.armorstandeditor.menu.EditorMenu;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class OpenMenuEvent extends Event {

    @Getter
    protected final Player player;

    @Getter
    protected final EditorMenu edtMenu;

    public OpenMenuEvent(Player player, EditorMenu edtMenu){
        this.player = player;
        this.edtMenu = edtMenu;
    }

}
