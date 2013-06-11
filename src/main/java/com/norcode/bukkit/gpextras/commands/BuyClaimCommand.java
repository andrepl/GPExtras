package com.norcode.bukkit.gpextras.commands;

import com.norcode.bukkit.gpextras.GPExtras;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.commands.BaseClaimCommand;
import me.ryanhamshire.GriefPrevention.data.Claim;
import me.ryanhamshire.GriefPrevention.data.PluginClaimMeta;
import me.ryanhamshire.GriefPrevention.exceptions.ClaimOwnershipException;
import me.ryanhamshire.GriefPrevention.messages.Messages;
import me.ryanhamshire.GriefPrevention.messages.TextMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class BuyClaimCommand extends BaseClaimCommand {
    GPExtras extras;
    public BuyClaimCommand(GPExtras extras) {
        super(extras.getGP(), "gpbuyclaim", Messages.ClaimMissing);
        this.extras = extras;
    }

    @Override
    public boolean onCommand(Player player, Claim claim, Command command, String s, LinkedList<String> strings) {
        PluginClaimMeta meta = claim.getClaimMeta(extras, false);
        if (meta == null || meta.getDouble("sale-price", -1D) <= 0) {
            player.sendMessage(plugin.configuration.getColor(TextMode.ERROR) + "This claim is not for sale.");
            return true;
        }
        double price = meta.getDouble("sale-price", 0.0);
        if (plugin.getEconomy().withdrawPlayer(player.getName(), price).transactionSuccess()) {
            // Tranfser claim ownership
            try {
                plugin.getDataStore().changeClaimOwner(claim, player.getName());
            } catch (ClaimOwnershipException e) {
                player.sendMessage(plugin.configuration.getColor(TextMode.ERROR) + "This claim cannot be purchased.");
                return true;
            }
            meta.set("sale-price", null);
            plugin.getDataStore().saveClaim(claim);
            player.sendMessage(plugin.configuration.getColor(TextMode.SUCCESS) + "You successfully purchased the claim.");
            return true;
        } else {
            player.sendMessage(plugin.configuration.getColor(TextMode.ERROR) + "You cannot afford to buy this claim.");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, LinkedList<String> strings) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
