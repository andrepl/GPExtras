package com.norcode.bukkit.gpextras.flags;

import com.norcode.bukkit.gpextras.GPExtras;
import com.norcode.bukkit.griefprevention.data.Claim;
import com.norcode.bukkit.griefprevention.data.PlayerData;
import com.norcode.bukkit.griefprevention.flags.BaseFlag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class HungerFlag extends BaseFlag implements Listener {

    GPExtras plugin;

    public HungerFlag(GPExtras plugin) {
        super("HUNGER", "Hunger", "If hunger is Denied, the food level of players in this area will not decrease.", "Allow", "gpextras.flag.hunger");
        this.plugin = plugin;
        getValidOptions().add("Allow");
        getValidOptions().add("Deny");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            PlayerData pd = plugin.getGP().getDataStore().getPlayerData(p.getName());
            Claim claim = plugin.getGP().getDataStore().getClaimAt(p.getLocation(), true, pd.getLastClaim());
            if (claim != null && claim.getFlag(this) != null && claim.getFlag(this).equals("Deny")) {
                event.setCancelled(true);
            }
        }
    }
}

