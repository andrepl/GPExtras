package com.norcode.bukkit.gpextras.commands;

import com.norcode.bukkit.gpextras.GPExtras;
import com.norcode.bukkit.gpextras.util.TimeUtil;
import com.norcode.bukkit.griefprevention.commands.BaseClaimCommand;
import com.norcode.bukkit.griefprevention.data.Claim;
import com.norcode.bukkit.griefprevention.data.PluginClaimMeta;
import com.norcode.bukkit.griefprevention.messages.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class RentClaimCommand extends BaseClaimCommand {

    GPExtras extras;

    public RentClaimCommand(GPExtras extras) {
        super(extras.getGP(), "gprent", Messages.ClaimMissing);
        this.extras = extras;
    }

    @Override
    public boolean onCommand(Player player, Claim claim, Command command, String label, LinkedList<String> args) {
        PluginClaimMeta meta = claim.getClaimMeta(extras, false);
        if (meta == null) {
            extras.debug("No claim meta");
            player.sendMessage("This claim is not for rent.");
            return true;
        }
        extras.debug(meta.serialize().toString());
        Double price = meta.getDouble("rent-price",null);
        Long duration = meta.getLong("rent-duration", null);
        if (duration == null) {
            Integer intd = meta.getInt("rent-duration", null);
            if (intd != null) {
                duration = (long) (int) intd;
            }
        }
        String currentOccupant = meta.getString("renter-name", null);
        if (currentOccupant != null && !currentOccupant.equals(player.getName())) {
            player.sendMessage(currentOccupant + " is already renting this claim.");
            return true;
        }
        if (price == null || duration == null || price <= 0 || !plugin.hasEconomy()) {
            extras.debug("No price" + price);
            player.sendMessage("This claim is not for rent.");
            return true;
        }
        if (plugin.getEconomy().withdrawPlayer(player.getName(), price).transactionSuccess()) {
            long now = System.currentTimeMillis();
            long expires = meta.getLong("rent-expires", now);
            meta.set("rent-expires", expires + duration);
            meta.set("renter-name", player.getName());
            claim.clearPermissions();
            claim.addManager(player.getName());
            player.sendMessage("You have rented this claim for " + TimeUtil.millisToString(duration));
            plugin.getDataStore().saveClaim(claim);
        } else {
            player.sendMessage("Insufficient funds.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, LinkedList<String> strings) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
