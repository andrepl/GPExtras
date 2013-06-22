package com.norcode.bukkit.gpextras;

import com.norcode.bukkit.griefprevention.data.Claim;
import com.norcode.bukkit.griefprevention.data.PluginClaimMeta;
import org.bukkit.entity.Player;
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
        if (!iter.hasNext()) {
            return;
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
        if (expiry == null) {
            return;
        }
        if (System.currentTimeMillis() > expiry) {
            // expired.
            claim.clearPermissions();
            for (String man: claim.getManagerList()) {
                claim.removeManager(man);
            }
            String owner = meta.getString("renter-name", null);
            meta.set("renter-name", null);
            meta.set("rent-expires", null);
            plugin.getGP().getDataStore().saveClaim(claim);
            if (owner != null) {
                Player player = plugin.getServer().getPlayerExact(owner);
                if (player.isOnline()) {
                    player.sendMessage("Your claim at " + claim.getMin() + " has expired.");
                }
            }
        }

    }
}
