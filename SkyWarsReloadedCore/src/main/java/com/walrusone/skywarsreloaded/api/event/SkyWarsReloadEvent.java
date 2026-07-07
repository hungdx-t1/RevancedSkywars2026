package com.walrusone.skywarsreloaded.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public class SkyWarsReloadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public SkyWarsReloadEvent() { }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return handlers;
    }

}
