package me.onixdev.event.impl;

import lombok.Getter;
import me.onixdev.event.api.BaseEvent;

public class TickEvent extends BaseEvent {
    @Getter
    private final Target tickType;
    public TickEvent(final Target tickType) {
        this.tickType = tickType;
    }
    public enum Target {
        FLYING,
        TRANSACTION,
        TICKEND
    }
}
