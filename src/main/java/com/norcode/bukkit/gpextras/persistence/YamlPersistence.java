package com.norcode.bukkit.gpextras.persistence;

import com.norcode.bukkit.gpextras.ConfigAccessor;
import com.norcode.bukkit.gpextras.GPExtras;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: andre
 * Date: 6/7/13
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class YamlPersistence implements IPersistence {
    private GPExtras plugin;
    HashMap<Long, ClaimBean> claimData = new HashMap<Long, ClaimBean>();
    ConfigAccessor accessor;
    public YamlPersistence(GPExtras plugin) {
        this.plugin = plugin;

    }
    @Override
    public void onEnable() {
        accessor = new ConfigAccessor(plugin, "claimdata.yml");
        accessor.getConfig();
    }

    @Override
    public void reload() {
        claimData.clear();
        accessor.reloadConfig();
        ClaimBean bean;
        for (String key: accessor.getConfig().getKeys(false)) {
            bean = new ClaimBean();
            bean.setId(Long.parseLong(key));
            if (GriefPrevention.instance.dataStore.getClaim(bean.getId()) == null) {
                accessor.getConfig().set(key, null);
                continue;
            }
            bean.setFlags(accessor.getConfig().getString(key));
            bean.initFlags();
            claimData.put(bean.getId(), bean);
        }
    }

    @Override
    public ClaimBean getClaimData(long claimId) {
        if (!claimData.containsKey(claimId)) {
            ClaimBean bean = new ClaimBean();
            bean.setId(claimId);
            accessor.getConfig().set(bean.getId().toString(), "");
            claimData.put(bean.getId(), bean);
        }
        return claimData.get(claimId);

    }

    @Override
    public void onDisable() {
        for (ClaimBean bean: claimData.values()) {
            accessor.getConfig().set(bean.getId().toString(), bean.getFlags());
        }
        accessor.saveConfig();
    }
}
