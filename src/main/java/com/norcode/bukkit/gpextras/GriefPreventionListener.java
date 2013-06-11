package com.norcode.bukkit.gpextras;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public class GriefPreventionListener implements Listener {

    public GPExtras plugin;

    public GriefPreventionListener(GPExtras plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("GriefPreventionTNG")) {
            plugin.debug("GriefPrevention was disabled, disabling GPExtras.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }
}
