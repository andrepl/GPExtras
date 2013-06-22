package com.norcode.bukkit.gpextras.commands;

import com.norcode.bukkit.gpextras.GPExtras;
import com.norcode.bukkit.griefprevention.commands.BaseClaimCommand;
import com.norcode.bukkit.griefprevention.data.Claim;
import com.norcode.bukkit.griefprevention.data.PluginClaimMeta;
import com.norcode.bukkit.griefprevention.messages.Messages;
import com.norcode.bukkit.griefprevention.messages.TextMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SetMessageCommand extends BaseClaimCommand {

    private GPExtras extras;

    public SetMessageCommand(GPExtras extras) {
        super(extras.getGP(), "gpsetmessage", Messages.ClaimMissing);
        this.extras = extras;
    }

    @Override
    public boolean onCommand(Player player, Claim claim, Command command, String label, LinkedList<String> args) {
        if (!(claim.isManager(player.getName()) || claim.getOwnerName().equals(player.getName()))) {
            extras.getGP().sendMessage(player, TextMode.ERROR, Messages.NoPermissionTrust, claim.getOwnerName());
            return true;
        }
        if (args.size() == 0) {
            return false;
        }
        String msgType = args.pop().toLowerCase();
        if (!(msgType.equals("entry") || msgType.equals("exit"))) {
            return false;
        }
        String msg = "";
        while (args.size() > 0) {
            msg += args.pop() + " ";
        }
        if (msg.endsWith(" ")) {
            msg = msg.substring(0,msg.length()-1);
        }
        if (msg == "") {
            msg = null;
        }
        PluginClaimMeta meta = claim.getClaimMeta(extras, true);
        meta.set(msgType + "-message", msg);
        extras.getGP().getDataStore().saveClaim(claim);
        player.sendMessage(extras.getGP().configuration.getColor(TextMode.SUCCESS) + "This claim's " + msgType + " message has been " + (msg == null ? "cleared" : "set."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, LinkedList<String> args) {
        List<String> results = new LinkedList<String>();
        if (args.size() == 1) {
            if ("entry".startsWith(args.peek().toLowerCase())) {
                results.add("entry");
            }
            if ("exit".startsWith(args.peek().toLowerCase())) {
                results.add("entry");
            }
        }
        return results;
    }
}
