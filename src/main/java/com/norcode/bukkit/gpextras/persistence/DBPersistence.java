package com.norcode.bukkit.gpextras.persistence;

import com.norcode.bukkit.gpextras.GPExtras;

import javax.persistence.PersistenceException;
import java.util.HashMap;

public class DBPersistence implements IPersistence {

    GPExtras plugin;
    HashMap<Long, ClaimBean> claimData = new HashMap<Long, ClaimBean>();

    public DBPersistence(GPExtras plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        try {
            int rc = plugin.getDatabase().find(ClaimBean.class).findRowCount();
        } catch (PersistenceException ex) {
            plugin.initDB();
        }
    }

    @Override
    public void reload() {
        claimData.clear();
        for (ClaimBean cd: plugin.getDatabase().find(ClaimBean.class).findList()) {
            cd.initFlags();
            claimData.put(cd.getId(), cd);
        }
    }

    @Override
    public ClaimBean getClaimData(long claimId) {
        if (!claimData.containsKey(claimId)) {
            ClaimBean data = new ClaimBean();
            data.setId(claimId);
            claimData.put(claimId, data);
        }
        return claimData.get(claimId);
    }

    @Override
    public void onDisable() {
        plugin.getDatabase().save(claimData.values());
    }
}
