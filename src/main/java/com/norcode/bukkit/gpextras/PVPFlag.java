package com.norcode.bukkit.gpextras;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.data.Claim;
import me.ryanhamshire.GriefPrevention.data.PlayerData;
import me.ryanhamshire.GriefPrevention.flags.BaseFlag;
import me.ryanhamshire.GriefPrevention.messages.TextMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PVPFlag extends BaseFlag implements Listener {

    GPExtras plugin;

    public PVPFlag(GPExtras plugin) {
        super("PVP");
        this.setDisplayName("PVP");
        this.setDescription("Prevents PVP Damage.");
        this.getValidOptions().add("Allow");
        this.getValidOptions().add("Deny");
        this.setRequiredPermission("gpextras.flag.pvp");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled=true, priority= EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if ((event.getDamager() instanceof Player) ||
                    ((event.getDamager() instanceof Projectile) &&
                            ((Projectile) ((Projectile) event.getDamager()).getShooter() instanceof Player))) {
                Player target = (Player) event.getEntity();
                PlayerData pd = GriefPrevention.instance.dataStore.getPlayerData(target.getName());
                Claim claim = null;
                if (pd != null) {
                    claim = pd.getLastClaim();
                }
                claim = GriefPrevention.instance.dataStore.getClaimAt(target.getLocation(), true, claim);
                if (claim != null && claim.getFlag(this).equals("Deny")) {
                    Player damager = event.getDamager() instanceof Player ? (Player) event.getDamager() : (Player) ((Projectile) event.getDamager()).getShooter();
                    damager.sendMessage(GriefPrevention.instance.configuration.getColor(TextMode.ERROR) + "This is a No-PVP Zone.");
                    event.setCancelled(true);
                }
            }
        }
    }
}
