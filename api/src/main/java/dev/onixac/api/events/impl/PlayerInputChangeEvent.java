package dev.onixac.api.events.impl;

import dev.onixac.api.events.api.BaseEvent;
import dev.onixac.api.user.IClientInput;

public class PlayerInputChangeEvent extends BaseEvent {
    private IClientInput input;
    public PlayerInputChangeEvent(IClientInput input) {
        this.input = input;
    }

    public IClientInput getInput() {
        return input;
    }

    public void setInput(IClientInput input) {
        this.input = input;
    }
}
