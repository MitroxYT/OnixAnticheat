package me.onixdev.event.impl;

import lombok.Getter;
import dev.onixac.api.events.api.BaseEvent;

public class TickEvent extends BaseEvent {
    @Getter
    public final Target tickType;
    public TickEvent(final Target tickType) {
        this.tickType = tickType;
    }
    public enum Target {
        FLYING,
        TRANSACTION,
        TICKEND
    }
}
