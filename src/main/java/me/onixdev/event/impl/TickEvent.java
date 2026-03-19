package me.onixdev.event.impl;

import dev.onixac.api.events.api.BaseEvent;

public class TickEvent extends BaseEvent {
    public final Target tickType;

    public TickEvent(final Target tickType) {
        this.tickType = tickType;
    }

    public boolean notTickEnd() {
        return tickType == Target.FLYING || tickType == Target.TRANSACTION;
    }

    public Target getTickType() {
        return tickType;
    }

    public enum Target {
        FLYING,
        TRANSACTION,
        TICKEND
    }
}
