package com.walrusone.skywarsreloaded.listeners;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.api.enums.GameType;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.managers.PlayerStat;
import com.walrusone.skywarsreloaded.utilities.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class PlayerJoinListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (SkyWarsReloaded.getCfg().getSpawn() != null && SkyWarsReloaded.getCfg().teleportOnJoin()) {
                    player.teleport(SkyWarsReloaded.getCfg().getSpawn());
                }
            }
        }.runTaskLater(SkyWarsReloaded.get(), 1);

        if (SkyWarsReloaded.getCfg().promptForResource()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setResourcePack(SkyWarsReloaded.getCfg().getResourceLink());
                }
            }.runTaskLater(SkyWarsReloaded.get(), 20);
        }

        if (PlayerStat.getPlayerStats(player) != null) {
            PlayerStat.removePlayer(player.getUniqueId().toString());
        }

        if (!SkyWarsReloaded.getCfg().bungeeMode()) {
            for (GameMap gMap : SkyWarsReloaded.getGameMapMgr().getMapsCopy()) {
                if (gMap.getCurrentWorld() != null && gMap.getCurrentWorld().equals(player.getWorld())) {
                    if (SkyWarsReloaded.getCfg().getSpawn() != null) {
                        player.teleport(SkyWarsReloaded.getCfg().getSpawn());
                    }
                }
            }
        }

        PlayerStat pStats = new PlayerStat(player);
        PlayerStat.getPlayers().add(pStats);
        pStats.updatePlayerIfInLobby(player);

        // Load player data
        pStats.loadStats(() -> {
            // Not allowed? Stop.
            if (!postLoadStats(player)) return;
        });
    }

    /**
     * Handle bungeecord join
     * @param player The joining player
     * @return Whether the player was allowed to join
     */
    public boolean postLoadStats(Player player) {
        // After stats are done loading, move to a game if in bungeecord mode
        if (!SkyWarsReloaded.getCfg().bungeeMode()) return true;

        if (player == null) return false;
        if (SkyWarsReloaded.getCfg().isLobbyServer()) return true;

        Bukkit.getLogger().log(Level.WARNING, "Trying to let " + player.getName() + " join a game");

        boolean joined = MatchManager.get().joinGame(player, GameType.ALL) != null;
        if (joined) return true;

        Bukkit.getLogger().log(Level.WARNING, "Failed to put " + player.getName() + " in a game");
        if (SkyWarsReloaded.getCfg().debugEnabled()) {
            Util.get().logToFile(ChatColor.YELLOW + "Couldn't find an arena for player " + player.getName() + ". Sending the player back to the skywars lobby.");
        }

        if (player.hasPermission("sw.admin")) {
            player.sendMessage(ChatColor.RED +
                    "Skywars encountered an issue while joining this bungeecord mode server.\n" +
                    "However, since you have the sw.admin permissions, you will not be kicked to the lobby.");
        } else {
            SkyWarsReloaded.get().sendBungeeMsg(player, "Connect", SkyWarsReloaded.getCfg().getBungeeLobby());
            kickPlayerIfStillOnline(player);
        }

        return false;
    }

    // UTILS
    private void kickPlayerIfStillOnline(Player player) {
        SkyWarsReloaded pl = SkyWarsReloaded.get();
        pl.getServer().getScheduler().runTaskLater(pl, () -> { if(player.isOnline()) player.kick(Component.empty()); }, 20);
    }
}