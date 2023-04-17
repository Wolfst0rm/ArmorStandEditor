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

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

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
    public String getMessage(String path, String format, String option) {
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
        return getMessage(path, format, null);
    }

    //Gets a message and assumes the format is just Info
    //Not used anywhere at the moment from what it seems
    public String getMessage(String path) {
        return getMessage(path, "info");
    }

    public String getRawMessage(String path, String format, String option){
        //String message = ChatColor.stripColor(getMessage(path, format, option)); //Strip the color from the message - Replaced by Deserializer from #Adventure
        String message = PlainTextComponentSerializer.plainText().serialize(LegacyComponentSerializer.legacySection().deserialize(getMessage(path, format, option)));
        format = getFormat(format); //Get the Format
        ChatColor color = ChatColor.WHITE; //Create a default white color
        String bold = "" , italic = "" , underlined = "" , obfuscated = "" , strikethrough = ""; //Strings for Bold, Underline, Italic, Strikethrough (Traditional things in Word/daily use) and Obfuscated (cause Mojang)
        for(int i = 0; i < format.length(); i++){
            //Get the Chat Color code at a specific character
            ChatColor code = ChatColor.getByChar(format.charAt(i));

            //Switch based on what that character is. Set in ChatColor.class (Bukkit)
            //Sets the <format>: true if a corresponding type has been found
            switch(code) {
                case MAGIC:
                    obfuscated = ", \"obfuscated\": true";
                    break;
                case BOLD:
                    bold = ", \"bold\": true";
                    break;
                case STRIKETHROUGH:
                    strikethrough = ", \"strikethrough\": true";
                    break;
                case UNDERLINE:
                    underlined = ", \"underlined\": true";
                    break;
                case ITALIC:
                    italic = ", \"italic\": true";
                    break;
                default: color = !code.isColor() ? color : code; //Otherwise we update our White to the matching Color Code
            }
        }
        //Return a formatted JSON String containing the message text and formatting information
        return String.format("{\"text\":\"%s\", \"color\":\"%s\"%s%s%s%s%s}", message, color.name().toLowerCase(),
            obfuscated, bold, strikethrough, underlined, italic);
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