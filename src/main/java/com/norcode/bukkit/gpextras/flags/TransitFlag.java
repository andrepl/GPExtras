package com.norcode.bukkit.gpextras.flags;

import com.norcode.bukkit.gpextras.GPExtras;
import com.norcode.bukkit.griefprevention.data.Claim;
import com.norcode.bukkit.griefprevention.flags.BaseFlag;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;

public class TransitFlag extends BaseFlag implements Listener {
    GPExtras plugin;
    public TransitFlag(GPExtras extras) {
        super("TRANSIT");
        plugin = extras;
        setDisplayName("Transit");
        setDefaultValue("False");
        getValidOptions().add("True");
        getValidOptions().add("False");
        setRequiredPermission("gpextras.flag.transit");
        setDescription("Marks a claim as a transit-zone.");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled=true)
    public void onVehicleCollideEntity(VehicleEntityCollisionEvent event) {
        if (!(event.getVehicle().getType().equals(EntityType.MINECART))) {
            return;
        }
        if (!(event.getEntity().getType().equals(EntityType.MINECART))) {
            return;
        }
        Claim claim = plugin.getGP().getDataStore().getClaimAt(event.getEntity().getLocation(), false, null);
        if (claim == null) {
            return;
        }
        if (claim.getFlag(this).equals("True")) {
            Vehicle v1 = event.getVehicle();
            Vehicle v2 = (Vehicle) event.getEntity();
            if (v1.isEmpty() && !v2.isEmpty()) {
                v1.remove();
                //v1.getLocation().getWorld().dropItem(v1.getLocation(), new ItemStack(Material.MINECART));
                event.setCancelled(true);
            } else if (v2.isEmpty() && !v1.isEmpty()) {
                v2.remove();
                //v2.getLocation().getWorld().dropItem(v2.getLocation(), new ItemStack(Material.MINECART));
                event.setCancelled(true);
            }
        }
    }

}
