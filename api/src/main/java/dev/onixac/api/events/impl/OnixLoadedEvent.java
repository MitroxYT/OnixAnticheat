package dev.onixac.api.events.impl;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OnixLoadedEvent extends Event  {
    private static final HandlerList handlers;
    public HandlerList getHandlers() {
        return OnixLoadedEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return OnixLoadedEvent.handlers;
    }
    static {
        handlers = new HandlerList();
    }
}
