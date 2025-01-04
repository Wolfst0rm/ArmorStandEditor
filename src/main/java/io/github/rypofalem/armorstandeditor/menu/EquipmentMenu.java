package io.github.rypofalem.armorstandeditor.menu;

import io.github.rypofalem.armorstandeditor.PlayerEditor;
import io.github.rypofalem.armorstandeditor.api.EquipmentMenuOpenedEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EquipmentMenu implements EditorMenu{

    private int HELMET_INDEX;
    private int CHEST_INDEX;
    private int PANTS_INDEX;
    private int BOOTS_INDEX;
    private int RHAND_INDEX;
    private int LHAND_INDEX;

    Inventory equipMenu;
    private PlayerEditor pe;
    private ArmorStand armorStand;
    static String menuName = "ArmorStand Equipment";

    public EquipmentMenu(PlayerEditor pe, ArmorStand as){
        this.pe = pe;
        this.armorStand = as;
        menuName = pe.plugin.getLanguage().getMessage("equiptitle", "menutitle");
        equipMenu = Bukkit.createInventory(this, 18, Component.text(menuName));
    }

    @Override
    public void open() {
        EquipmentMenuOpenedEvent event = new EquipmentMenuOpenedEvent(pe.getPlayer(), this);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        fillInventory();
        pe.getPlayer().openInventory(this.equipMenu);

    }

    @Override
    public void fillInventory() {
        equipMenu.clear();
        EntityEquipment equipmentOnAS = armorStand.getEquipment();
       //ItemStack
    }

    @Override
    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return equipMenu;
    }
}
