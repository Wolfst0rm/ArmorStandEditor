package io.github.rypofalem.armorstandeditor;

import io.github.rypofalem.armorstandeditor.devtools.Debug;
import io.github.rypofalem.armorstandeditor.modes.AdjustmentMode;
import io.github.rypofalem.armorstandeditor.modes.Axis;
import io.github.rypofalem.armorstandeditor.modes.CopySlots;
import io.github.rypofalem.armorstandeditor.modes.EditMode;
import lombok.Getter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerEditor {
    public ArmorStandEditorPlugin plugin;
    private Debug debug;
    private UUID uuid;
    private long lastOpened = Integer.MIN_VALUE;

    @Getter EditMode eMode;
    @Getter AdjustmentMode adjMode;
    CopySlots copySlots;
    @Getter Axis axis;
    double eulerAngleChange;
    double degreeAngleChange;
    double movChange;
    //TODO: Add Menu Here once that is reimplemented
    ArmorStand targeted;
    ArrayList<ArmorStand> targetList = null;
    int targetIndex = 0;
    long lastCancelled = 0;

    public PlayerEditor(UUID uuid, ArmorStandEditorPlugin plugin){
        this.uuid = uuid;
        this.plugin = plugin;
        eMode = EditMode.NONE;
        adjMode = AdjustmentMode.COARSE;
        axis = Axis.X;
        copySlots = new CopySlots();
        eulerAngleChange = getManager().coarseAdj;
        degreeAngleChange = eulerAngleChange / Math.PI * 180;
        movChange = getManager().coarseMov;
    }

    public PlayerEditorManager getManager() {
        return plugin.editorManager;
    }

    public Player getPlayer() {
        return plugin.getServer().getPlayer(getUUID());
    }

    public UUID getUUID() {
        return uuid;
    }

}
