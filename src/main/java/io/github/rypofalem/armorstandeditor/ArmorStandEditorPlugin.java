package io.rypofalem.armorstandeditor;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class ArmorStandEditorPlugin extends JavaPlugin {

    public static final String SEPARATOR_FIELD = "================================";

    // Classes
    private static ArmorStandEditorPlugin instance;
    private io.github.rypofalem.armorstandeditor.Debug debug = new io.github.rypofalem.armorstandeditor.Debug(this);

    // Server Software Check True/False
    boolean isFolia;
    boolean isPaper;
    boolean isSpigotMc;

    // Debug Flag - ALWAYS ON BY DEFAULT
    boolean debugFlag = true;

    //Config Items (in Order)
    String aseVersion;

    public ArmorStandEditorPlugin() {
        instance = this;
    }


    @Override
    public void onEnable() {
        // Plugin startup logic
        //Load Messages in Console
        debug.log("Debug Mode = ON");
        debug.log("Enabling ASE, Getting ASE Version and logging Server Checks");

        aseVersion = getConfig().getString("version");
        getLogger().info("======= ArmorStandEditor =======");
        getLogger().info("Plugin Version: v" + aseVersion);

        isFolia = isFolia();
        debug.log("Using Folia: " + isFolia);
        isPaper = isPaper();
        debug.log("Using Paper or a Fork: " + isPaper);
        isSpigotMc = isSpigotMc();
        debug.log("Using SpigotMC: " + isSpigotMc);


        if(isFolia || isPaper){
            getLogger().log(Level.INFO, "Server Type: Paper/Folia/Purpur or a Fork");
            getLogger().log(Level.INFO, SEPARATOR_FIELD);
            getServer().getPluginManager().enablePlugin(this);

        }

        if(isSpigotMc){
            getLogger().log(Level.INFO, "Server Type: SpigotMC");
            getLogger().log(Level.SEVERE, "This Plugin No Longer works on Spigot. Disabling ArmorStandEditor");
            getLogger().log(Level.INFO, SEPARATOR_FIELD);
            getServer().getPluginManager().disablePlugin(this);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static boolean isFolia(){
        try{
            Class.forName("io.papermc.paper.threadedregions.RegionziedServer");
            return true;
        } catch (ClassNotFoundException e){
            return false;
        }
    }

    private static boolean isPaper() {
        try{
            Class.forName("io.papermc.paper.text.PaperComponents");
            return true;
        } catch (ClassNotFoundException e){
            return false;
        }
    }

    private static boolean isSpigotMc() {
        try{
            Class.forName("org.spigotmc.CustomTimingsHandler");
            return true;
        } catch (ClassNotFoundException e){
            return false;
        }
    }


    public boolean isDebug() {
        return debugFlag;
    }
}
