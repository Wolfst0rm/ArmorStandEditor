package io.github.rypofalem.armorstandeditor.language;

import io.github.rypofalem.armorstandeditor.ArmorStandEditorPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Lang {

    protected static final String DEFAULT_LANG = "en_us.yml";
    protected final ArmorStandEditorPlugin plugin;
    private YamlConfiguration langConfig = null;
    private YamlConfiguration defConfig  = null;
    private File              langFile   = null;


    public Lang(final String langFilename, ArmorStandEditorPlugin plugin) {
        this.plugin = plugin;
        reloadLang(langFilename);
    }

    public void reloadLang(String langFileName){
        if(langFileName == null) langFileName = DEFAULT_LANG;
        File langFolder = new File(plugin.getDataFolder().getPath() + File.separator + "lang");
        langFile = new File(langFolder, langFileName);
        InputStream inputStream = plugin.getResource("lang" + "/" + DEFAULT_LANG);
        Reader defaultLangStream = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        defConfig = YamlConfiguration.loadConfiguration(defaultLangStream);
        inputStream = null;
        try{
            inputStream = new FileInputStream(langFile);
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return;
        }
        Reader langStream = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        langConfig = YamlConfiguration.loadConfiguration(langStream);
    }

    //path: yml path to message in language file
    //format: yml path to format in language file
    //option: path-specific variable that may be used
    public String getMessage(String path, String format, String option) {
        if (langConfig == null) reloadLang(langFile.getName());
        if (path == null) return "";
        if (option == null) option = "";

        format = getFormat(format);
        for (int i = 0; i < format.length(); i += 2) { //place formatting symbol before each character
            format = format.substring(0, i) + '\u00A7' + format.substring(i);
        }

        if (getString(path + "." + option) != null) option = getString(path + "." + option);
        String message = format + getString(path + ".msg");
        message = message.replace("<x>", option);
        return message;
    }


    public String getMessage(String path, String format) {
        return getMessage(path, format, null);
    }

    public String getMessage(String path) {
        return getMessage(path, "info");
    }

    private String getFormat(String format) {
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
