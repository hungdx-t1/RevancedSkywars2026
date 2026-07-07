package com.walrusone.skywarsreloaded.api.event;

import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.GameKit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public class SkyWarsSelectKitEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final GameMap map;
    private final GameKit kit;

    public SkyWarsSelectKitEvent(Player p, GameMap game, GameKit kit) {
        this.player = p;
        this.map = game;
        this.kit = kit;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public GameMap getGame() {
        return map;
    }

    public GameKit getKit() {
        return kit;
    }

}
