package com.walrusone.skywarsreloaded.menus.gameoptions.objects;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public record ChestItem(ItemStack item, int chance) implements Comparable<ChestItem> {
    public int compareTo(@Nonnull ChestItem o) {
        return Integer.compare(chance, o.chance);
    }
}
