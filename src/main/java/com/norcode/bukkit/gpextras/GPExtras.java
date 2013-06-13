package com.norcode.bukkit.gpextras;

import com.norcode.bukkit.gpextras.commands.SetMessageCommand;
import com.norcode.bukkit.gpextras.flags.HungerFlag;
import com.norcode.bukkit.gpextras.flags.MobSpawnsFlag;
import com.norcode.bukkit.gpextras.flags.PVPFlag;
import com.norcode.bukkit.gpextras.listeners.ClaimChangeListener;
import com.norcode.bukkit.gpextras.listeners.GriefPreventionListener;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.exceptions.FlagAlreadyRegisteredException;
import me.ryanhamshire.GriefPrevention.exceptions.InvalidFlagException;
import org.bukkit.plugin.java.JavaPlugin;

public class GPExtras extends JavaPlugin {

    GriefPrevention griefPreventionPlugin;
    ClaimChangeListener claimChangeListener;

    @Override
    public void onEnable() {
        initConfig();
        griefPreventionPlugin = (GriefPrevention) getServer().getPluginManager().getPlugin("GriefPrevention");
        new GriefPreventionListener(this);
        loadFlags();
        claimChangeListener = new ClaimChangeListener(this);
        if (getConfig().getBoolean("entry-exit-messages")) {
            new SetMessageCommand(this);
        }
    }

    private void initConfig() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public GriefPrevention getGP() {
        return griefPreventionPlugin;
    }

    public void loadFlags() {
        try {
        if (getConfig().getBoolean("mob_spawns.enabled")) {
            griefPreventionPlugin.getFlagManager().registerFlag(new MobSpawnsFlag(this));
        }
        if (getConfig().getBoolean("pvp.enabled")) {
            griefPreventionPlugin.getFlagManager().registerFlag(new PVPFlag(this));
        }
        if (getConfig().getBoolean("hunger.enabled")) {
            griefPreventionPlugin.getFlagManager().registerFlag(new HungerFlag(this));
        }
        } catch (InvalidFlagException ex) {
            getLogger().severe("Something went horribly wrong! Disabling!");
            getServer().getPluginManager().disablePlugin(this);
        } catch (FlagAlreadyRegisteredException ex) {
            getLogger().severe("Something went horribly wrong! Disabling!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void debug(String s) {
        if (getConfig().getBoolean("debug", false)) {
            getLogger().info(s);
        }
    }
}
