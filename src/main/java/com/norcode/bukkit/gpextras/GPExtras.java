package com.norcode.bukkit.gpextras;

import com.norcode.bukkit.gpextras.persistence.ClaimBean;
import com.norcode.bukkit.gpextras.persistence.DBPersistence;
import com.norcode.bukkit.gpextras.persistence.IPersistence;
import com.norcode.bukkit.gpextras.persistence.YamlPersistence;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public class GPExtras extends JavaPlugin {
    IPersistence persistence;

    @Override
    public void onEnable() {
        if (checkGP()) {
            saveDefaultConfig();
            getConfig().options().copyDefaults(true);
            saveConfig();
            if (getConfig().getBoolean("database", false)) {
                persistence = new DBPersistence(this);
            } else {
                persistence = new YamlPersistence(this);
            }
            persistence.onEnable();
            persistence.reload();
            Flag.registerPermissions(this);
            getServer().getPluginCommand("claimflag").setExecutor(new FlagCommand(this));
            getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        }
    }

    private boolean checkGP() {
        if (getServer().getPluginManager().getPlugin("GriefPrevention") == null) {
            getLogger().severe("GriefPrevention was not found.  GPExtras will not run.");
            return false;
        }
        return true;
    }

    public void initDB() {
        installDDL();
    }

    @Override
    public void onDisable() {
        if (persistence != null) {
            persistence.onDisable();
        }
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        LinkedList<Class<?>> classes = new LinkedList<Class<?>>();
        classes.add(ClaimBean.class);
        return classes;
    }

    public IPersistence getPersistence() {
        return persistence;
    }
}
