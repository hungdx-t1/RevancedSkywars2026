package com.walrusone.skywarsreloaded.api.event;

import com.walrusone.skywarsreloaded.enums.Vote;
import com.walrusone.skywarsreloaded.game.GameMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public class SkyWarsVoteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final GameMap map;
    private final Vote vote;

    public SkyWarsVoteEvent(Player p, GameMap game, Vote vote) {
        this.player = p;
        this.map = game;
        this.vote = vote;
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

    public Vote getVote() {
        return vote;
    }

}
