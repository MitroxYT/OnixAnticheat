package me.onixdev.event.impl;

import dev.onixac.api.events.api.BaseEvent;
import lombok.Getter;

@Getter
public class PlayerCloseInventoryEvent extends BaseEvent {
    private final int id;

    public PlayerCloseInventoryEvent(final int id) {
        this.id = id;
    }

    public boolean isClient() {
        return id == 0;
    }
}
