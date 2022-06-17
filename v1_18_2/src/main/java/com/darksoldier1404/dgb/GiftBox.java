package com.darksoldier1404.dgb;

import com.darksoldier1404.dgb.commands.DGBCommand;
import com.darksoldier1404.dgb.events.DGBEvent;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("all")
public class GiftBox extends JavaPlugin {
    private static GiftBox plugin;
    public static String prefix;
    public static YamlConfiguration config;

    public static GiftBox getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        config = ConfigUtils.loadDefaultPluginConfig(plugin);
        prefix = ColorUtils.applyColor(config.getString("Settings.prefix"));
        plugin.getServer().getPluginManager().registerEvents(new DGBEvent(), plugin);
        getCommand("선물박스").setExecutor(new DGBCommand());
    }

    @Override
    public void onDisable() {
    }
}
