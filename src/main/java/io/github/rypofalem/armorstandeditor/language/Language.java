/*
 * ArmorStandEditor: Bukkit plugin to allow editing armor stand attributes
 * Copyright (C) 2016-2023  RypoFalem
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.github.rypofalem.armorstandeditor.language;

import io.github.rypofalem.armorstandeditor.ArmorStandEditorPlugin;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.antlr.runtime.BufferedTokenStream;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Language {
    final String DEFAULT_LANG = "en_US.yml";
    private YamlConfiguration langConfig = null;
    private YamlConfiguration defConfig = null;
    private File langFile = null;
    ArmorStandEditorPlugin plugin;

    public Language(String langFileName, ArmorStandEditorPlugin plugin) {
        this.plugin = plugin;
        reloadLang(langFileName);
    }

    public void reloadLang(String langFileName) {
        //File Name not given - use DEFAULT_LANG
        if (langFileName == null) langFileName = DEFAULT_LANG;
        //Create the Language Folder
        File langFolder = new File(plugin.getDataFolder().getPath() + File.separator + "lang");
        //Create the Language File in that folder
        langFile = new File(langFolder, langFileName);

        //Get the default Langauge File
        InputStream input = plugin.getResource("lang" + "/" + DEFAULT_LANG); //getResource doesn't accept File.seperator on windows, need to hardcode unix seperator "/" instead
        assert input != null;

        //Create a UTF_8 StreamReader for the default Language File
        Reader defaultLangStream = new InputStreamReader(input, StandardCharsets.UTF_8);
        defConfig = YamlConfiguration.loadConfiguration(defaultLangStream);

        input = null;
        try {
            //Create a new FileInputStream for the Language File Given
            input = new FileInputStream(langFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        //Create a new UTF_8 Stream Reader for that Language File and load the configuration of it
        Reader langStream = new InputStreamReader(input, StandardCharsets.UTF_8);
        langConfig = YamlConfiguration.loadConfiguration(langStream);
    }

    //path: yml path to message in language file
    //format: yml path to format in language file
    //option: path-specific variable that may be used
    public String getMessageLegacy(String path, String format, String option) {
        if (langConfig == null) reloadLang(langFile.getName()); //Try to get the Lang File - if not re-perform the reloadLang
        if (path == null) return ""; //Set Blank if Message Path is not in the langauge file
        if (option == null) option = ""; //Set blank if options are not in the langauge file

        //Get the Format of that message as a string
        format = getFormat(format);
        for (int i = 0; i < format.length(); i += 2) { //place formatting symbol before each character
            format = format.substring(0, i) + ChatColor.COLOR_CHAR + format.substring(i);
        }

        //If there is an option we also need to use that
        //EX: Replacing <x> in "setgravity.msg: Gravity turned <x>." from on/off
        if (getString(path + "." + option) != null) option = getString(path + "." + option);
        String message = format + getString(path + ".msg");
        message = message.replace("<x>", option);
        return message;
    }

    //Gets a message with the format specified
    //Examples of this can be seen on: CommandEx.java in the commandXXXXXX(player,String[]) Switch.
    //sender.sendMessage(plugin.getLang().getMessage("noperm", "warn"));
    public String getMessage(String path, String format) {
        return getMessageLegacy(path, format, null);
    }

    public Component getMessage(String path, String format, String option) {
        String messageContent = getMessageLegacy(path, format, option);

        // Deserialize the message from Legacy to Plain
        Component component = LegacyComponentSerializer.legacySection().deserialize(messageContent);

        // Apply formatting to the message
        if (format.startsWith("#")) {
            try {
                String hex = format.substring(1);
                if (hex.length() == 6) {
                    int r = Integer.parseInt(hex.substring(0, 2), 16);
                    int g = Integer.parseInt(hex.substring(2, 4), 16);
                    int b = Integer.parseInt(hex.substring(4, 6), 16);
                    component = component.color(TextColor.color(r, g, b));
                }
            } catch (NumberFormatException e) {
                // Ignore invalid hex colors
            }
        } else {
            for(int i = 0; i < format.length(); i++){
                char codeChar = format.charAt(i);

                //Std MC Colors
                Map<Character, NamedTextColor> colorMap = new HashMap<>();
                colorMap.put('0', NamedTextColor.BLACK);
                colorMap.put('1', NamedTextColor.DARK_BLUE);
                colorMap.put('2', NamedTextColor.DARK_GREEN);
                colorMap.put('3', NamedTextColor.DARK_AQUA);
                colorMap.put('4', NamedTextColor.DARK_RED);
                colorMap.put('5', NamedTextColor.DARK_PURPLE);
                colorMap.put('6', NamedTextColor.GOLD);
                colorMap.put('7', NamedTextColor.GRAY);
                colorMap.put('8', NamedTextColor.DARK_GRAY);
                colorMap.put('9', NamedTextColor.BLUE);
                colorMap.put('a', NamedTextColor.GREEN);
                colorMap.put('b', NamedTextColor.AQUA);
                colorMap.put('c', NamedTextColor.RED);
                colorMap.put('d', NamedTextColor.LIGHT_PURPLE);
                colorMap.put('e', NamedTextColor.YELLOW);
                colorMap.put('f', NamedTextColor.WHITE);

                switch (codeChar) {
                    case 'k' -> component = component.decorate(TextDecoration.OBFUSCATED);
                    case 'l' -> component = component.decorate(TextDecoration.BOLD);
                    case 'm' -> component = component.decorate(TextDecoration.STRIKETHROUGH);
                    case 'n' -> component = component.decorate(TextDecoration.UNDERLINED);
                    case 'o' -> component = component.decorate(TextDecoration.ITALIC);
                    default -> {
                        NamedTextColor color = colorMap.get(codeChar);
                        if (color != null) {
                            component = component.color(color);
                        }
                    }
                }
            }
        }
        return component;
    }

    private String getFormat(String format){
        format = getString(format);
        return format == null ? "" : format;
    }

    private String getString(String path) {
        String message = null;
        if (langConfig.contains(path)) {
            message = langConfig.getString(path);
        } else if (defConfig.contains(path)) {
            message = defConfig.getString(path);
        }
        return message;
    }
}