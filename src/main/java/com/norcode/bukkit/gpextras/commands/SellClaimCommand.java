package com.norcode.bukkit.gpextras.commands;

import com.norcode.bukkit.gpextras.GPExtras;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.commands.BaseClaimCommand;
import me.ryanhamshire.GriefPrevention.data.Claim;
import me.ryanhamshire.GriefPrevention.data.PlayerData;
import me.ryanhamshire.GriefPrevention.data.PluginClaimMeta;
import me.ryanhamshire.GriefPrevention.messages.Messages;
import me.ryanhamshire.GriefPrevention.messages.TextMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class SellClaimCommand extends BaseClaimCommand {

    GPExtras extras;

    public SellClaimCommand(GPExtras extras, String cmdName) {
        super(extras.getGP(), "gpsellclaim", Messages.ClaimMissing);
        this.extras = extras;
    }

    @Override
    public boolean onCommand(Player player, Claim claim, Command command, String label, LinkedList<String> args) {
        if (args.size() == 0) {
            return false;
        }
        if (claim.getParent() != null) {
            plugin.sendMessage(player, TextMode.ERROR, Messages.TransferTopLevel);
            return true;
        }
        PlayerData playerData = plugin.getDataStore().getPlayerData(player.getName());

        if (!(claim.getOwnerName() == player.getName()) ||
                playerData.isIgnoreClaims() && player.hasPermission("gpextras.sellclaim.others")) {
            plugin.sendMessage(player, TextMode.ERROR, Messages.OnlyOwnersModifyClaims);
            return true;
        }
        Double salePrice;
        try {
            salePrice = Double.parseDouble(args.peek());
        } catch (IllegalArgumentException ex) {
            plugin.sendMessage(player, TextMode.ERROR, Messages.NotANumber, args.peek());
            return true;
        }
        PluginClaimMeta meta = claim.getClaimMeta(extras, true);
        meta.set("sale-price", salePrice);
        plugin.getDataStore().saveClaim(claim);
        player.sendMessage(plugin.configuration.getColor(TextMode.SUCCESS) + "Claim successfully put up for sale.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, LinkedList<String> args) {
        return null;
    }
}
