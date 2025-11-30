package me.onixdev.event.impl;

import lombok.Getter;
import me.onixdev.event.api.BaseEvent;

@Getter
public class PlayerCloseInventoryEvent extends BaseEvent {
    private int id;
    public PlayerCloseInventoryEvent(final int id) {
        this.id = id;
    }

    public boolean isClient() {
        return id == 0;
    }
}
