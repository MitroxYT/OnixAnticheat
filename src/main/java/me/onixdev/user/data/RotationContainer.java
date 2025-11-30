package me.onixdev.user.data;

import lombok.Getter;
import me.onixdev.event.impl.PlayerRotationEvent;
import me.onixdev.user.OnixUser;

@Getter
public class RotationContainer {
    private final OnixUser user;
    private double yaw,pitch,lastYaw,lastPitch;
    private double deltaYaw,deltaPitch,lastDeltaYaw,lastDeltaPitch;

    public RotationContainer(final OnixUser user) {
        this.user = user;
    }
    public void handle(double yaw, double pitch) {
        lastYaw = this.yaw;
        lastPitch = this.pitch;
        this.yaw = yaw;
        this.pitch = pitch;
        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;
        this.deltaYaw = Math.abs(yaw - this.lastYaw);
        this.deltaPitch = Math.abs(pitch - this.lastPitch);
        PlayerRotationEvent preRotation = new PlayerRotationEvent(false, yaw, pitch, deltaYaw, deltaPitch);
        user.handleEvent(preRotation);
        //Тут будет просчет сенсы и тд
        PlayerRotationEvent postRotation = new PlayerRotationEvent(true, yaw, pitch, deltaYaw, deltaPitch);
        user.handleEvent(postRotation);
    }
}
