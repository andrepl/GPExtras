package com.norcode.bukkit.gpextras.listeners;

import com.norcode.bukkit.gpextras.GPExtras;
import com.norcode.bukkit.gpextras.util.TimeUtil;
import com.norcode.bukkit.griefprevention.data.PluginClaimMeta;
import com.norcode.bukkit.griefprevention.events.PlayerChangeClaimEvent;
import com.norcode.bukkit.griefprevention.messages.TextMode;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClaimChangeListener implements Listener {
    private GPExtras plugin;

    public ClaimChangeListener(GPExtras plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChangeClaim(PlayerChangeClaimEvent event) {

        if (event.getOldClaim() != null && event.getNewClaim() == null) {
            // leaving a claim
            PluginClaimMeta oldMeta = event.getOldClaim().getClaimMeta(plugin, false);
            if (oldMeta != null) {
                if (plugin.getConfig().getBoolean("entry-exit-messages")) {
                    String exitMessage = oldMeta.getString("exit-message", null);
                    if (exitMessage != null) event.getPlayer().sendMessage(exitMessage);
                }
            }
        } else if (event.getNewClaim() != null) {
            PluginClaimMeta newMeta = event.getNewClaim().getClaimMeta(plugin, false);
            if (newMeta != null) {
                if (plugin.getConfig().getBoolean("entry-exit-messages")) {
                    String entryMessage = newMeta.getString("entry-message", null);
                    if (entryMessage != null) event.getPlayer().sendMessage(entryMessage);
                }
                ChatColor instr = plugin.getGP().configuration.getColor(TextMode.INSTR);
                ChatColor info = plugin.getGP().configuration.getColor(TextMode.INFO);
                if (newMeta.getDouble("sale-price", null) != null) {
                    Double price = newMeta.getDouble("sale-price", null);
                    event.getPlayer().sendMessage(instr + "FOR SALE: " + info + plugin.getGP().getEconomy().format(price));
                } else if (newMeta.getDouble("rent-price", null) != null && newMeta.getString("renter-name", null) != null) {
                    Double price = newMeta.getDouble("rent-price", null);
                    Long duration = newMeta.getLong("rent-duration", null);
                    event.getPlayer().sendMessage(instr + "FOR RENT: " + info + plugin.getGP().getEconomy().format(price) + " / " + TimeUtil.millisToString(duration));
                }
            }
        }
    }
}
