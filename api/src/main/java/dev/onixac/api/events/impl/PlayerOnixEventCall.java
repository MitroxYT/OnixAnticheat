package dev.onixac.api.events.impl;

import dev.onixac.api.events.api.BaseEvent;
import dev.onixac.api.user.IOnixUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerOnixEventCall extends Event implements Cancellable {
    private static final HandlerList handlers;
    private final IOnixUser onixUser;
    private final BaseEvent event;
    private final boolean preChecks;
    private boolean cancelled;
    public PlayerOnixEventCall(BaseEvent event,IOnixUser user,boolean preChecks) {
        this.onixUser = user;
        this.event = event;
        this.preChecks = preChecks;
    }
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public HandlerList getHandlers() {
        return PlayerOnixEventCall.handlers;
    }

    public static HandlerList getHandlerList() {
        return PlayerOnixEventCall.handlers;
    }

    public BaseEvent getEvent() {
        return event;
    }

    public IOnixUser getOnixUser() {
        return onixUser;
    }

    public boolean isPreChecks() {
        return preChecks;
    }
    static {
        handlers = new HandlerList();
    }
}
