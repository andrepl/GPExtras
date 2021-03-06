package com.norcode.bukkit.gpextras.flags;

import com.norcode.bukkit.gpextras.GPExtras;
import com.norcode.bukkit.griefprevention.configuration.WorldConfig;
import com.norcode.bukkit.griefprevention.data.Claim;
import com.norcode.bukkit.griefprevention.flags.BaseFlag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.EnumSet;

public class MobSpawnsFlag extends BaseFlag implements Listener {

    GPExtras plugin;
    EnumSet<EntityType> blockedTypes = EnumSet.noneOf(EntityType.class);
    EnumSet<CreatureSpawnEvent.SpawnReason> blockedReasons = EnumSet.noneOf(CreatureSpawnEvent.SpawnReason.class);
    private boolean onlyAboveSeaLevel = true;

    public MobSpawnsFlag(GPExtras plugin) {
        super("MOB_SPAWNS");
        this.plugin = plugin;
        this.setDisplayName("Mob Spawns");
        this.setDefaultValue("Allow");
        this.setDescription("Prevents hostile mobs from spawning.");
        this.getValidOptions().add("Allow");
        this.getValidOptions().add("Deny");
        this.setRequiredPermission("gpextras.flag.mobspawns");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        ConfigurationSection cfg = plugin.getConfig().getConfigurationSection("mob_spawns");
        onlyAboveSeaLevel = cfg.getBoolean("only-above-sea-level", true);
        for (String s: cfg.getStringList("blocked-types")) {
            blockedTypes.add(EntityType.valueOf(s.toUpperCase()));
        }
        for (String s: cfg.getStringList("blocked-reasons")) {
            blockedReasons.add(CreatureSpawnEvent.SpawnReason.valueOf(s.toUpperCase()));
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (blockedTypes.contains(event.getEntity().getType()) && blockedReasons.contains(event.getSpawnReason())) {
            WorldConfig worldConfig = plugin.getGP().getWorldCfg(event.getLocation().getWorld());
            if (!onlyAboveSeaLevel || event.getLocation().getBlockY() > worldConfig.getSeaLevelOverride()) {
                Claim claim = plugin.getGP().getDataStore().getClaimAt(event.getLocation(), false, null);
                if (claim != null && claim.getFlag(this) != null && claim.getFlag(this).equals("Deny")) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
