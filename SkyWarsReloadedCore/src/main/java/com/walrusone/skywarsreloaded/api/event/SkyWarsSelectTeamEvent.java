package com.walrusone.skywarsreloaded.api.event;

import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.TeamCard;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public class SkyWarsSelectTeamEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final GameMap map;
    private final TeamCard team;

    public SkyWarsSelectTeamEvent(Player p, GameMap game, TeamCard team) {
        this.player = p;
        this.map = game;
        this.team = team;
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

    public TeamCard getTeam() {
        return team;
    }
}
