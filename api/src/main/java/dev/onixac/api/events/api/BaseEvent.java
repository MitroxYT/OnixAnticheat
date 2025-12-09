package dev.onixac.api.events.api;

public class BaseEvent {
    private boolean cancelled = false;
    public void cancel() {
        cancelled = true;
    }
    public boolean isCancelled() {
        return cancelled;
    }
}
