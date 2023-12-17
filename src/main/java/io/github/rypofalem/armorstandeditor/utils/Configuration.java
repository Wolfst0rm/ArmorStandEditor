package io.github.rypofalem.armorstandeditor.utils;

import io.github.rypofalem.armorstandeditor.ArmorStandEditorPlugin;
import io.github.rypofalem.armorstandeditor.language.Language;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;

/*
* Currently not used but groundwork is now being laid for later
*/
public class Configuration {

    private static String languageFolderLocation = "lang/";
    private final ArmorStandEditorPlugin plugin;
    private static FileConfiguration config;
    private static FileConfiguration gui;
    private Language lang;

    public Configuration(ArmorStandEditorPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        try {
            File dataFolder = plugin.getDataFolder();
            load(new File(dataFolder, "config.yml"), getClass().getDeclaredField("config"));
            load(new File(dataFolder, "guis.yml"), getClass().getDeclaredField("gui"));
            lang = new Language(Configuration.getConfig().getString("lang"), plugin);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void load(File file, Field field) throws Exception {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            plugin.saveResource(file.getName(), false);
        }
        field.set(null, YamlConfiguration.loadConfiguration(file));
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static FileConfiguration getGUI() {
        return gui;
    }

    public static String getLanguageFolderLocation() {
        return languageFolderLocation;
    }

    public static String getString(ConfigurationSection section, String s) {
        if (section == null) return "";

        return section.getString(s, "");
    }

    public static String getString(YamlConfiguration config, String s) {
        return config.getString(s, "");
    }

    public Language getLang() {
        return lang;
    }

    public static String color(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}