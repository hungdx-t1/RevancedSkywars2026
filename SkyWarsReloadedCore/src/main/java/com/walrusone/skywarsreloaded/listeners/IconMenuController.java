package com.walrusone.skywarsreloaded.listeners;

import com.google.common.collect.Maps;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.menus.IconMenu;
import com.walrusone.skywarsreloaded.menus.IconMenu.OptionClickEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;

public class IconMenuController implements Listener {
    private final Map<Player, IconMenu> menu = Maps.newHashMap();
    private final Map<String, IconMenu> persistantMenus = Maps.newHashMap();

    public void create(Player player, ArrayList<Inventory> invs, OptionClickEventHandler optionClickEventHandler) {
        if (player != null) {
            menu.put(player, new IconMenu(invs, optionClickEventHandler));
        }
    }

    public void create(String key, ArrayList<Inventory> invs, OptionClickEventHandler optionClickEventHandler) {
        if (key != null) {
            // persistantMenus.put(key, new IconMenu(invs, DisplayName)); // Giữ cấu hình gộp menu
            persistantMenus.put(key, new IconMenu(invs, optionClickEventHandler));
        }
    }

    public IconMenu getMenu(String string) {
        return persistantMenus.get(string);
    }

    public boolean hasViewers(String key) {
        IconMenu iconMenu = persistantMenus.get(key);
        if (iconMenu != null) {
            for (Inventory inv : iconMenu.getInventories()) {
                if (!inv.getViewers().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void show(Player player, @Nullable String key) {
        if (key != null) {
            if (persistantMenus.containsKey(key)) {
                (persistantMenus.get(key)).openInventory(player, 0);
            }
        } else if (menu.containsKey(player)) {
            (menu.get(player)).openInventory(player, 0);
        }
    }

    private void destroy(Player key) {
        menu.remove(key);
    }

    public boolean has(Player player) {
        return menu.containsKey(player);
    }

    public boolean has(String key) {
        return persistantMenus.containsKey(key);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if ((event.getWhoClicked() instanceof Player player) && this.menu.containsKey(player)) {
            this.menu.get(player).onInventoryClick(event);
        }
        for (IconMenu menu : persistantMenus.values()) {
            if (menu.getInventories().contains(event.getInventory())) {
                menu.onInventoryClick(event);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClose(final InventoryCloseEvent event) {
        if ((event.getPlayer() instanceof Player player) && menu.containsKey(player)) {
            SkyWarsReloaded plugin = SkyWarsReloaded.get();

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                IconMenu currentMenu = menu.get(player);
                if (currentMenu != null) {
                    // 🌟 SỬA CHÍ MẠNG: Lấy kho đồ hàng đầu hiện tại (getTopInventory) từ InventoryView
                    // Để trả về đúng kiểu đối tượng `Inventory`, giúp hàm .contains() đối chiếu chính xác!

                    // old: var inventory = player.getOpenInventory(); if (currentMenu.getInventories().contains(inventory))...
                    Inventory topInventory = player.getOpenInventory().getTopInventory();

                    if (currentMenu.getInventories().contains(topInventory)) {
                        IconMenuController.this.destroy(player);
                    }
                }
            }, 5L);
        }
    }
}
