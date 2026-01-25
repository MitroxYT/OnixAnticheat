package me.onixdev.check.impl.combat.heuristics;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.user.OnixUser;
import me.onixdev.util.math.RotList;
import me.onixdev.util.net.PacketUtil;

public class AimHeuristicB extends Check {
    private final RotList yawDeltaBuffer = new RotList(20);
    private final RotList pitchDeltaBuffer = new RotList(20);
    private int rotationCheckTimer;
    private float currentYaw;
    private int accelerationCounter = 0;
    private float previousYawDelta;
    private float previousPitch;
    private float currentPitch;
    public AimHeuristicB(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("AimHeuristic").setType("B"));
    }

    @Override
    public void onPacketIn(PacketReceiveEvent event) {
        if (PacketUtil.INSTANCE.isTickPacketLegacy(event.getPacketType())) {
            if (this.rotationCheckTimer > 0) {
                --this.rotationCheckTimer;
            }
        }

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);

            if (flying.hasRotationChanged()) {
                checkRotation(flying.getLocation().getYaw(), flying.getLocation().getPitch());
            }
        }

        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity wrapperPlayClientInteractEntity = new WrapperPlayClientInteractEntity(event);
            if (wrapperPlayClientInteractEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                if (player.combatData.getTarget() != null && player.combatData.getTarget().getEntityId() == wrapperPlayClientInteractEntity.getEntityId()) {
                    this.rotationCheckTimer = 15;
                }
            }
        }
    }
    public void checkRotation(float yaw, float pitch) {
        float yawDelta = getDelta(yaw, this.currentYaw);
        float pitchDelta = getDelta(pitch, this.currentPitch);

        double deltaXZ = player.getMovementContainer().deltaXZ;
        double cps = player.clickData.getCPS();
        float absoluteYawDelta = Math.abs(yawDelta - this.previousYawDelta);
        float absolutePitchDelta = Math.abs(pitchDelta - this.previousPitch);
        this.yawDeltaBuffer.add(absoluteYawDelta);
        this.pitchDeltaBuffer.add(absolutePitchDelta);

        if (this.yawDeltaBuffer.isFull() && this.pitchDeltaBuffer.isFull() && this.rotationCheckTimer >= 5) {
            boolean suspiciousRotation;
            float yawDeltaDeviation = this.yawDeltaBuffer.getStandardDeviation();
            float pitchDeltaDeviation = this.pitchDeltaBuffer.getStandardDeviation();
            boolean isLowYaw = yawDelta < 1.5f;
            suspiciousRotation = yawDeltaDeviation < 5.0f && pitchDeltaDeviation > 5.0f && !isLowYaw;
            if (suspiciousRotation  && deltaXZ > 0.05 && cps < 3) {
                ++this.accelerationCounter;
                if (this.accelerationCounter > 8) {
                    fail(" acceleration [" + accelerationCounter + "]");
                    if (this.accelerationCounter > 10) {
                        this.accelerationCounter = 10;
                    }
                }
            } else {
                this.accelerationCounter = Math.max(0, this.accelerationCounter - 1);
            }
        }


        this.currentYaw = yaw;
        this.currentPitch = pitch;
        this.previousYawDelta = yawDelta;
        this.previousPitch = pitchDelta;
    }
    public static float getDelta(float yaw, float currentYaw) {
        return Math.abs(yaw - currentYaw);
    }
}
