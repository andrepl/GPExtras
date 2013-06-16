package com.norcode.bukkit.gpextras;

import me.ryanhamshire.GriefPrevention.data.Claim;
import me.ryanhamshire.GriefPrevention.data.PluginClaimMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.UUID;

public class RentCheckTask extends BukkitRunnable {

    GPExtras plugin;
    Iterator<UUID> iter;

    public RentCheckTask(GPExtras plugin) {
        this.plugin = plugin;
    }

    public void run() {
        if (iter == null || !iter.hasNext()) {
            iter = plugin.getRentedClaims().iterator();
        }

        Claim claim = plugin.getGP().getDataStore().getClaim(iter.next());
        if (claim == null) {
            return;
        }
        PluginClaimMeta meta = claim.getClaimMeta(plugin, false);
        if (meta == null) {
            return;
        }
        Long expiry = meta.getLong("rent-expires", null);
        if (expiry != null) {
            return;
        }
        if (System.currentTimeMillis() > expiry) {
            // expired.
            claim.clearPermissions();
            for (String man: claim.getManagerList()) {
                claim.removeManager(man);
            }
            meta.set("renter-name", null);
            meta.set("rent-expires", null);
        }

    }
}
