package com.walrusone.skywarsreloaded.api.event;

import com.walrusone.skywarsreloaded.game.GameMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public class SkyWarsKillEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player killer;
    private final Player killed;
    private final GameMap map;

    public SkyWarsKillEvent(Player killer, Player killed, GameMap game) {
        this.killed = killed;
        this.killer = killer;
        this.map = game;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return handlers;
    }

    public Player getKiller() {
        return killer;
    }

    public Player getKilled() {
        return killed;
    }

    public GameMap getGame() {
        return map;
    }

}
