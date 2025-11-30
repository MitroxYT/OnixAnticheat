package me.onixdev.event.impl;

import lombok.Getter;
import me.onixdev.event.api.BaseEvent;

@Getter
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
}
