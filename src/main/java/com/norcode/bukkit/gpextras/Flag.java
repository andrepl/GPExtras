package com.norcode.bukkit.gpextras;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.security.Permissions;
import java.util.EnumSet;
import java.util.HashMap;


public enum Flag {

    NOMOBS('M', "Prevent Hostile Mob Spawns", "nomobs"),
    NOPVP('P', "Prevent PVP", "nopvp");

    private char character;
    private String verboseName;
    private String permissionName;

    private static HashMap<Character, Flag> flags = new HashMap<Character, Flag>();

    static {
        for (Flag flag: values()) {
            flags.put(flag.character, flag);
        }
    }

    Flag(char c, String verbose, String permName) {
        this.character = c;
        this.verboseName = verbose;
        this.permissionName = permName;
    }

    public static Flag forChar(char c) {
        return flags.get(c);
    }

    public static EnumSet<Flag> fromString(String s) {
        EnumSet set = EnumSet.noneOf(Flag.class);
        for (char c: s.toCharArray()) {
            set.add(forChar(c));
        }
        return set;
    }

    public static String serialize(EnumSet<Flag> set) {
        StringBuilder sb = new StringBuilder();
        for (Flag f: set) {
            sb.append(f.character);
        }
        return sb.toString();
    }

    public String getVerboseName() {
        return verboseName;
    }

    public static void registerPermissions(GPExtras plugin) {
        Permission wildCard = plugin.getServer().getPluginManager().getPermission("gpextras.allowtoggle.*");
        if (wildCard == null) {
            wildCard = new Permission("gpextras.allowtoggle.*", "Wildcard to allow toggling all claim flags", PermissionDefault.OP);
            plugin.getServer().getPluginManager().addPermission(wildCard);
        }
        for (Flag f: values()) {
            Permission flagPerm = plugin.getServer().getPluginManager().getPermission("gpextras.allowtoggle." + f.permissionName);
            if (flagPerm == null) {
                flagPerm = new Permission("gpextras.allowtoggle." + f.permissionName, "Allow toggling " + f.name() + " flag.", PermissionDefault.OP);
                plugin.getServer().getPluginManager().addPermission(flagPerm);
                flagPerm.addParent(wildCard, true);
            }
        }
    }

    public String getPermissionName() {
        return permissionName;
    }
}
