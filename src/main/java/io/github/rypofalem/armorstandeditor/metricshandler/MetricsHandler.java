package io.github.rypofalem.armorstandeditor.metricshandler;

import io.github.rypofalem.armorstandeditor.ArmorStandEditorPlugin;
import io.github.rypofalem.armorstandeditor.Metrics;

import static java.lang.String.valueOf;

public class MetricsHandler {
    private static final int PLUGIN_ID = 12668;
    public ArmorStandEditorPlugin plugin;

    public MetricsHandler(ArmorStandEditorPlugin plugin){
        this.plugin = plugin;
        Metrics metrics = new Metrics(plugin, PLUGIN_ID);
        metrics.addCustomChart(new Metrics.SimplePie("dev_flag_enabled", () -> valueOf(plugin.isDebug())));
        metrics.addCustomChart(new Metrics.SimplePie("require_sneaking", () -> valueOf(plugin.getRequireSneakingConfig())));
        metrics.addCustomChart(new Metrics.SimplePie("tool_lore_enabled", () -> valueOf(plugin.getRequireToolLoreConfig())));
        metrics.addCustomChart(new Metrics.SimplePie("tool_data_enabled", () -> valueOf(plugin.getRequireToolDataConfig())));
        metrics.addCustomChart(new Metrics.SimplePie("custom_toolname_enabled", () -> valueOf(plugin.getRequireToolNameConfig())));
        metrics.addCustomChart(new Metrics.SimplePie("per_world_enabled", () -> valueOf(plugin.getPerWorldSupportConfig())));
        metrics.addCustomChart(new Metrics.SimplePie("armor_stand_invisiblity_usage", () -> valueOf(plugin.getInvisibleArmorStandsConfig())));
        metrics.addCustomChart(new Metrics.SimplePie("itemframe_invisiblity_usage", () -> valueOf(plugin.getInvisibleItemFramesConfig())));
        metrics.addCustomChart(new Metrics.SimplePie("coarse_rotation_setting", () -> String.valueOf(plugin.getCoarseConfig())));
        metrics.addCustomChart(new Metrics.SimplePie("fine_rotation_setting", () -> String.valueOf(plugin.getFineConfig())));
        metrics.addCustomChart(new Metrics.SimplePie("min_supported_scale_setting", () -> String.valueOf(plugin.getMinScaleConfig())));
        metrics.addCustomChart(new Metrics.SimplePie("max_supported_scale_setting", () -> String.valueOf(plugin.getMaxScaleConfig())));


        //TODO: Readd Languages here!


    }






}
