package io.github.rypofalem.armorstandeditor.menu;

import org.bukkit.entity.ArmorStand;

public interface EditorMenu extends ASEHolder{
    void open();
    void fillInventory();
    ArmorStand getArmorStand();
}
