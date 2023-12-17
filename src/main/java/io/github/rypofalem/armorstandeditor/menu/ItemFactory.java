package io.github.rypofalem.armorstandeditor.menu;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ItemFactory {
    static ItemFactory getInstance() {
        return new ItemFactory() {};
    }

    default ItemStack createItem(ConfigurationSection section, Inventory inventory, Function<ItemStack, ItemStack> function) {
        ItemStack itemStack = createItem(section, (Function<String, String>) null, null);
        if (itemStack == null || section == null) return itemStack;

        int slot = section.getInt("slot", -1);
        if (slot != -1 && inventory != null) inventory.setItem(slot, function == null ? itemStack : function.apply(itemStack));
        return itemStack;
    }

    default ItemStack createItem(ConfigurationSection section, Function<String, String> nameReplacements, Function<List<String>, List<String>> loreReplacements) {
        if (section == null) return null; // If the section is null, return null.

        String upper = section.getString( "material");
        if (upper == null) return null;

        Material material = Material.getMaterial(loreReplacements == null ? upper.toUpperCase() : nameReplacements.apply(upper)); // Get the material from the config.
        if (material == null || material == Material.AIR) return null; // If the material is null or air, return null.
        if (material == getSkullMaterial() && (section.getString("owner") != null || section.getString("texture") != null)) // If the material is a skull and the owner or texture is set, create a skull.
            return createSkull(section, nameReplacements, loreReplacements);

        ItemStack itemStack = new ItemStack(material, section.getInt("amount", 1), (short) section.getInt("data")); // Create the item stack.
        return addBaseItemMeta(itemStack, itemStack.getItemMeta(), section, nameReplacements, loreReplacements); // Add the base item meta.
    }

    default ItemStack createSkull(ConfigurationSection section, Function<String, String> nameReplacements, Function<List<String>, List<String>> loreReplacements) {
        if (section == null) return null;

        ItemStack itemStack = new ItemStack(getSkullMaterial(), section.getInt("amount", 1), (short) section.getInt("data")); // Create the item stack.
        return addBaseItemMeta(itemStack, applyBasicSkull(itemStack, section, nameReplacements), section, nameReplacements, loreReplacements); // Add the base item meta.
    }

    default SkullMeta applyBasicSkull(ItemStack itemStack, ConfigurationSection section, Function<String, String> nameReplacements) {
        SkullMeta im = (SkullMeta) itemStack.getItemMeta(); // Get the item meta.
        String owner = section.getString( "owner"); // Get the owner from the config.
        if (owner == null) im.setOwner(nameReplacements == null ? owner : nameReplacements.apply(owner)); // If the owner is not empty, set the owner.

        String texture = section.getString("texture"); // Get the texture from the config.
        if (texture == null) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null); // Create a new game profile.
            profile.getProperties().put("textures", new Property("textures", texture)); // Set the texture.

            try {
                Field profileField = im.getClass().getDeclaredField("profile"); // Get the profile field.
                profileField.setAccessible(true); // Set the field to accessible.
                profileField.set(im, profile); // Set the profile.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return im; // Return the item meta.
    }

    default ItemStack addBaseItemMeta(ItemStack itemStack, ItemMeta im, ConfigurationSection section, Function<String, String> nameReplacements, Function<List<String>, List<String>> loreReplacements) {
        if (section == null || itemStack.getAmount() <= 0) return null; // If the section is null or the amount is 0, return null.

        String name = ChatColor.translateAlternateColorCodes('&', section.getString("name")); // Get the name from the config.
        im.setDisplayName((nameReplacements == null || name == null ? name : nameReplacements.apply(name))); // Set the display name.

        List<String> lore = section.getStringList("lore"); // Get the lore from the config.
        im.setLore((loreReplacements == null ? lore : loreReplacements.apply(lore).stream().flatMap(x -> Arrays.stream(x.split("\n"))).toList()).stream().map(x -> ChatColor.translateAlternateColorCodes('&', x)).collect(Collectors.toList())); // Set the lore.

        if (section.getBoolean("glowing")) { // If the item is glowing,
            im.addEnchant(Enchantment.DURABILITY, 1, true); // Add the durability enchantment.
            try {
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS); // Add the hide enchants flag.
            } catch (NoClassDefFoundError ignored) {
            }
        }
        try {
            section.getStringList("item-flags").forEach(x -> im.addItemFlags(ItemFlag.valueOf(x))); // Add the item flags.
        } catch (NoClassDefFoundError ignored) {
        }
        try {
            section.getStringList("enchantments").forEach(x -> im.addEnchant(Enchantment.getByName(x.split(":")[0].toUpperCase()), Integer.parseInt(x.split(":")[1]), true)); // Add the enchantments.
        } catch (NoClassDefFoundError ignored) {
        }
        try {
            int customModelData = section.getInt("custom-model-data", -1); // Get the custom model data.
            if (customModelData != -1) im.setCustomModelData(customModelData); // Set the custom model data.
        } catch (Throwable ignored) {}

        if (im instanceof PotionMeta) {
            try {
                PotionEffect effect = new PotionEffect(PotionEffectType.getByName(section.getString("effect")), section.getInt("duration"), section.getInt("amplifier"));
                ((PotionMeta) im).addCustomEffect(effect, true);
            } catch (Throwable ignored) {}
        }
        itemStack.setItemMeta(im); // Set the item meta.
        return itemStack; // Return the item stack.
    }

    default Material getSkullMaterial() {
        Material icon = Material.getMaterial("PLAYER_HEAD"); // Get the player head material.
        if (icon == null) return Material.getMaterial("SKULL_ITEM"); // If the player head material is null, get the skull item material.
        return icon;
    }
}
