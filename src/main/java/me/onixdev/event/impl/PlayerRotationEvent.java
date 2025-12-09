package me.onixdev.event.impl;

import lombok.Generated;
import lombok.Getter;
import dev.onixac.api.events.api.BaseEvent;

public class PlayerRotationEvent extends BaseEvent {
    private boolean Post;
    private double yaw,pitch;
    private double deltaYaw,deltaPitch;
    public PlayerRotationEvent(boolean post, double yaw, double pitch, double deltaYaw, double deltaPitch) {
        this.Post = post;
        this.yaw = yaw;
        this.pitch = pitch;
        this.deltaYaw = deltaYaw;
        this.deltaPitch = deltaPitch;
    }
    
    public boolean isPost() {
        return this.Post;
    }

    
    public double getYaw() {
        return this.yaw;
    }

    
    public double getPitch() {
        return this.pitch;
    }

    
    public double getDeltaYaw() {
        return this.deltaYaw;
    }

    
    public double getDeltaPitch() {
        return this.deltaPitch;
    }
}
