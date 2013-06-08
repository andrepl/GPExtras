package com.norcode.bukkit.gpextras;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.EnumSet;
import java.util.List;

public class EntityListener implements Listener {
    private GPExtras plugin;
    private EnumSet<EntityType> blockedEntityTypes;
    private EnumSet<CreatureSpawnEvent.SpawnReason> blockedSpawnReasons;

    public EntityListener(GPExtras plugin) {
        this.plugin = plugin;
        reconfigure();
    }

    public void reconfigure() {
        blockedEntityTypes.clear();
        List<String> blockedTypes = this.plugin.getConfig().getStringList("nomobs.blocked-types");
        if (blockedTypes != null) {
            for (String s: blockedTypes) {
                blockedEntityTypes.add(EntityType.valueOf(s.toUpperCase()));
            }
        }
        List<String> blockedReasons = this.plugin.getConfig().getStringList("nomobs.blocked-reasons");
        if (blockedReasons != null) {
            for (String s: blockedReasons) {
                blockedSpawnReasons.add(CreatureSpawnEvent.SpawnReason.valueOf(s.toUpperCase()));
            }
        }

    }
    @EventHandler(ignoreCancelled=true)
    public void onPVP(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if ((event.getDamager() instanceof Player) ||
                    ((event.getDamager() instanceof Projectile) &&
                    ((Projectile) event.getDamager()).getShooter() instanceof Player)) {
                Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getEntity().getLocation(), false, null);
                if (claim != null) {
                    if (plugin.getPersistence().getClaimData(claim.getID()).hasFlag(Flag.NOPVP)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (!blockedEntityTypes.contains(event.getEntity().getType())) return;
        if (!blockedSpawnReasons.contains(event.getSpawnReason())) return;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getLocation(), false, null);
        if (claim != null) {
            if (plugin.getPersistence().getClaimData(claim.getID()).hasFlag(Flag.NOMOBS)) {
                event.setCancelled(true);
            }
        }
    }
}
