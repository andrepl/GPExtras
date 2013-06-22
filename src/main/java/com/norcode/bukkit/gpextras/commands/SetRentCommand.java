package com.norcode.bukkit.gpextras.commands;

import com.norcode.bukkit.gpextras.GPExtras;
import com.norcode.bukkit.griefprevention.commands.BaseClaimCommand;
import com.norcode.bukkit.griefprevention.data.Claim;
import com.norcode.bukkit.griefprevention.data.PluginClaimMeta;
import com.norcode.bukkit.griefprevention.messages.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetRentCommand extends BaseClaimCommand {

    GPExtras extras;
    public SetRentCommand(GPExtras extras) {
        super(extras.getGP(), "gpsetrent", Messages.ClaimMissing);
        this.extras = extras;
    }

    @Override
    public boolean onCommand(Player player, Claim claim, Command command, String lbl, LinkedList<String> args) {
        if (args.size() != 2) {
            return false;
        }
        if (claim.getParent() == null) {
            player.sendMessage("You may only rent subclaims.");
            return true;
        }

        String error = claim.getParent().allowEdit(player);
        if (error != null) {
            player.sendMessage(error);
            return true;
        }

        long duration = parseDuration(args.peek());
        if (duration == 0) {
            player.sendMessage("Invalid rent duration: " + args.peek());
            return true;
        }
        args.pop();

        double price;
        try {
            price = Double.parseDouble(args.peek());
        } catch (IllegalArgumentException ex) {
            player.sendMessage("Invalid price: " + args.peek());
            return true;
        }

        PluginClaimMeta meta = claim.getClaimMeta(extras, true);
        meta.set("rent-price", price);
        meta.set("rent-duration", duration);
        extras.getRentedClaims().add(claim.getId());
        player.sendMessage("Rent set.");
        plugin.getDataStore().saveClaim(claim);
        return true;
    }

    Pattern durationPattern = Pattern.compile("(\\d+)([wd])", Pattern.CASE_INSENSITIVE);
    private long parseDuration(String s) {
        Matcher m = durationPattern.matcher(s);
        long duration = 0;
        while (m.find()) {
            long c = Long.parseLong(m.group(1));
            String i = m.group(2).toLowerCase();
            if (i.equals("d")) {
                duration += TimeUnit.MILLISECONDS.convert(c, TimeUnit.DAYS);
            } else if (i.equals("w")) {
                duration += TimeUnit.MILLISECONDS.convert(c*7, TimeUnit.DAYS);
            }
        }
        return duration;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String lbl, LinkedList<String> args) {
        return null;
    }
}
