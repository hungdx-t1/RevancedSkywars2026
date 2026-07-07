package com.walrusone.skywarsreloaded.game;

import org.bukkit.ChatColor;

public class GameMapComparator implements java.util.Comparator<GameMap> {
    public int compare(GameMap f1, GameMap f2) {
        if ((f1 != null) && (f2 != null)) {
            return ChatColor.stripColor(translateColorCode(f1.getDisplayName())).compareTo(ChatColor.stripColor(translateColorCode(f2.getDisplayName())));
        }
        return 0;
    }

    private String translateColorCode(String ctx) {
        return ChatColor.translateAlternateColorCodes('&', ctx);
    }
}
