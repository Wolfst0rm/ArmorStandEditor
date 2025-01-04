package io.github.rypofalem.armorstandeditor.modes;

import lombok.Getter;
import org.bukkit.entity.ArmorStand;

@Getter
public class CopySlots {
    ArmorStandData[] slots = new ArmorStandData[9];
    public byte currentSlot = 0;

    //returns true if parameters are acceptable, false otherwise.
    public boolean changeSlots(byte slot) {
        if (slot < slots.length && slot >= 0) {
            currentSlot = slot;
            return true;
        } else {
            return false;
        }
    }

    public void copyDataToSlot(ArmorStand armorStand) {
        slots[currentSlot] = new ArmorStandData(armorStand);
    }

    //returns null if there is not data in current slot
    public ArmorStandData getDataToPaste() {
        return slots[currentSlot];
    }
}
