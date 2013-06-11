package com.norcode.bukkit.gpextras;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.plugin.java.JavaPlugin;

public class GPExtras extends JavaPlugin {

    GriefPreventionListener griefPreventionListener;
    ClaimChangeListener claimChangeListener;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        reloadConfig();
        griefPreventionListener = new GriefPreventionListener(this);
        loadFlags();
        if (getConfig().getBoolean("entry-exit-messages")) {
            claimChangeListener = new ClaimChangeListener(this);
            new SetMessageCommand(this);
        }
    }

    public void loadFlags() {
        if (getConfig().getBoolean("mob_spawns.enabled")) {
            GriefPrevention.instance.getFlagManager().registerFlag(new MobSpawnsFlag(this));
        }
        if (getConfig().getBoolean("pvp.enabled")) {
            GriefPrevention.instance.getFlagManager().registerFlag(new PVPFlag(this));
        }
    }

    public void debug(String s) {
        if (getConfig().getBoolean("debug", false)) {
            getLogger().info(s);
        }
    }
}
