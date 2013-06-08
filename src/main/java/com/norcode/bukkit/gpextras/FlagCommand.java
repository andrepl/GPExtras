package com.norcode.bukkit.gpextras;

import com.norcode.bukkit.gpextras.persistence.ClaimBean;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FlagCommand implements TabExecutor {

    GPExtras plugin;

    public FlagCommand(GPExtras extras) {
        this.plugin = extras;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        String action = args[0].toLowerCase();
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(((Player) sender).getLocation(), true, null);
        if (claim == null) {
            sender.sendMessage("You must be standing inside a claim to do that.");
            return true;
        }
        String error = claim.allowEdit((Player) sender);
        if (error != null) {
            sender.sendMessage(error);
            return true;
        }
        Flag flag = null;
        if (args.length == 2) {
            try {
                flag = Flag.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException ex) {
                sender.sendMessage("Unknown Flag: " + args[1]);
                return true;
            }
            if (!sender.hasPermission("gpextras.allowtoggle." + flag.getPermissionName())) {
                sender.sendMessage("Sorry, you don't have permission to set that flag here.");
                return true;
            }
        }
        ClaimBean bean = plugin.getPersistence().getClaimData(claim.getID());
        if (action.equals("set")) {
            bean.setFlag(flag);
            sender.sendMessage(flag.getVerboseName() + ": " + (bean.hasFlag(flag) ? ChatColor.GREEN + "TRUE" : ChatColor.RED + "FALSE"));
            return true;
        } else if (action.equals("unset")) {
            bean.unsetFlag(flag);
            sender.sendMessage(flag.getVerboseName() + ": " + (bean.hasFlag(flag) ? ChatColor.GREEN + "TRUE" : ChatColor.RED + "FALSE"));
            return true;
        } else if (action.equals("toggle")) {
            bean.toggleFlag(flag);
            sender.sendMessage(flag.getVerboseName() + ": " + (bean.hasFlag(flag) ? ChatColor.GREEN + "TRUE" : ChatColor.RED + "FALSE"));
            return true;
        } else if (action.equals("check")) {
            if (flag == null) {
                List<String> lines = new ArrayList<String>(Flag.values().length);
                for (Flag f: Flag.values()) {
                    lines.add(f.getVerboseName() + ": " + (bean.hasFlag(f) ? ChatColor.GREEN + "TRUE" : ChatColor.RED + "FALSE"));
                }
                sender.sendMessage(lines.toArray(new String[lines.size()]));
                return true;
            }
            sender.sendMessage(flag.getVerboseName() + ": " + (bean.hasFlag(flag) ? ChatColor.GREEN + "TRUE" : ChatColor.RED + "FALSE"));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> results = new LinkedList<String>();
        if (args.length == 1) {
            String a = args[0].toLowerCase();
            if ("set".startsWith(a)) {
                results.add("set");
            }
            if ("unset".startsWith(a)) {
                results.add("unset");
            }
            if ("toggle".startsWith(a)) {
                results.add("toggle");
            }
            if ("check".startsWith(a)) {
                results.add("check");
            }
        } else if (args.length == 2) {
            for (Flag f: Flag.values()) {
                if (f.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    results.add(f.name());
                }
            }
        }

        return results;

    }
}
