package me.onixdev.user.data;

import lombok.Getter;
import me.onixdev.event.impl.PlayerRotationEvent;
import me.onixdev.user.OnixUser;
import me.onixdev.util.math.MathUtil;
import me.onixdev.util.net.MinecraftValues;

import java.util.ArrayDeque;

@Getter
public class RotationContainer {
    private final OnixUser user;
    private double yaw,pitch,lastYaw,lastPitch;
    private double deltaYaw,deltaPitch,lastDeltaYaw,lastDeltaPitch;
    private double finalSensitivity;
    private double mcpSensitivity;
    private final ArrayDeque<Integer> sensitivitySamples;
    private int sensitivity;

    public RotationContainer(final OnixUser user) {
        this.user = user;
        this.sensitivitySamples = new ArrayDeque<Integer>();
    }
    public void handle(double yaw, double pitch) {
        lastYaw = this.yaw;
        lastPitch = this.pitch;
        if (user.getMovementContainer().getSetbackLocation() != null) {
            user.getMovementContainer().getSetbackLocation().setYaw((float) yaw);
            user.getMovementContainer().getSetbackLocation().setPitch((float) pitch);
        }
        this.yaw = yaw;
        this.pitch = pitch;
        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;
        this.deltaYaw = Math.abs(yaw - this.lastYaw);
        this.deltaPitch = Math.abs(pitch - this.lastPitch);
        PlayerRotationEvent preRotation = new PlayerRotationEvent(false, yaw, pitch, deltaYaw, deltaPitch);
        user.handleEvent(preRotation);
        if (this.deltaPitch > 0.1 && this.deltaPitch < 25.0f) {
            this.processSensitivity();
        }
        //Тут будет просчет сенсы и тд
        PlayerRotationEvent postRotation = new PlayerRotationEvent(true, yaw, pitch, deltaYaw, deltaPitch);
        user.handleEvent(postRotation);
    }
    private void processSensitivity() {
        final float gcd = (float) MathUtil.getGcd(this.deltaPitch, this.lastDeltaPitch);
        final double sensitivityModifier = Math.cbrt(0.8333 * gcd);
        final double sensitivityStepTwo = 1.666 * sensitivityModifier - 0.3333;
        final double finalSensitivity = sensitivityStepTwo * 200.0;
        this.finalSensitivity = finalSensitivity;
        this.sensitivitySamples.add((int)finalSensitivity);
        if (this.sensitivitySamples.size() == 40) {
            this.sensitivity = MathUtil.getMode(this.sensitivitySamples);
            if (this.hasValidSensitivity()) {
                this.mcpSensitivity = MinecraftValues.SENSITIVITY_MCP_VALUES.get(this.sensitivity);
            }
            this.sensitivitySamples.clear();
        }
    }

    public boolean hasValidSensitivity() {
        return this.sensitivity > 0 && this.sensitivity < 200;
    }

    public boolean hasValidSensitivityNormalaized() {
        return this.sensitivity > 0 && this.sensitivity < 269;
    }
}
