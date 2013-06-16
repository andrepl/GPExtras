package com.norcode.bukkit.gpextras;

import com.norcode.bukkit.gpextras.commands.*;
import com.norcode.bukkit.gpextras.flags.HungerFlag;
import com.norcode.bukkit.gpextras.flags.MobSpawnsFlag;
import com.norcode.bukkit.gpextras.flags.PVPFlag;
import com.norcode.bukkit.gpextras.listeners.ClaimChangeListener;
import com.norcode.bukkit.gpextras.listeners.GriefPreventionListener;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.data.Claim;
import me.ryanhamshire.GriefPrevention.data.PluginClaimMeta;
import me.ryanhamshire.GriefPrevention.exceptions.FlagAlreadyRegisteredException;
import me.ryanhamshire.GriefPrevention.exceptions.InvalidFlagException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.UUID;

public class GPExtras extends JavaPlugin {

    GriefPrevention griefPreventionPlugin;
    ClaimChangeListener claimChangeListener;
    private HashSet<UUID> rentedClaims = new HashSet<UUID>();
    BukkitTask rentCheckTask;

    @Override
    public void onEnable() {
        initConfig();
        griefPreventionPlugin = (GriefPrevention) getServer().getPluginManager().getPlugin("GriefPreventionTNG");
        new GriefPreventionListener(this);
        loadFlags();
        claimChangeListener = new ClaimChangeListener(this);
        if (getConfig().getBoolean("entry-exit-messages")) {
            new SetMessageCommand(this);
        }
        new BuyClaimCommand(this);
        new SellClaimCommand(this);
        new RentClaimCommand(this);
        new SetRentCommand(this);
        PluginClaimMeta meta;
        for (UUID uuid: getGP().getDataStore().getTopLevelClaimIDs()) {
            Claim parent = getGP().getDataStore().getClaim(uuid);
            for (Claim child: parent.getChildren()) {
                meta = child.getClaimMeta(this, false);
                if (meta != null) {
                    if (meta.getLong("rent-expires", null) != null) {
                        rentedClaims.add(child.getId());
                    }
                }
            }
        }
        rentCheckTask = new RentCheckTask(this).runTaskTimer(this, 20, 20);
    }

    private void initConfig() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        if (rentCheckTask != null) {
            rentCheckTask.cancel();
        }
    }

    public GriefPrevention getGP() {
        return griefPreventionPlugin;
    }

    private PVPFlag pvpFlag;
    private HungerFlag hungerFlag;
    private MobSpawnsFlag mobSpawnsFlag;

    public void loadFlags() {
        try {
        if (getConfig().getBoolean("mob_spawns.enabled")) {
            mobSpawnsFlag = new MobSpawnsFlag(this);
            griefPreventionPlugin.getFlagManager().registerFlag(mobSpawnsFlag);
        }
        if (getConfig().getBoolean("pvp.enabled")) {
            pvpFlag = new PVPFlag(this);
            griefPreventionPlugin.getFlagManager().registerFlag(pvpFlag);
        }
        if (getConfig().getBoolean("hunger.enabled")) {
            hungerFlag = new HungerFlag(this);
            griefPreventionPlugin.getFlagManager().registerFlag(hungerFlag);
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

    public PVPFlag getPvpFlag() {
        return pvpFlag;
    }

    public HungerFlag getHungerFlag() {
        return hungerFlag;
    }

    public MobSpawnsFlag getMobSpawnsFlag() {
        return mobSpawnsFlag;
    }

    public HashSet<UUID> getRentedClaims() {
        return rentedClaims;
    }
}
