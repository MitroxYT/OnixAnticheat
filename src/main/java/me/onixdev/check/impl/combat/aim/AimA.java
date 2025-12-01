package me.onixdev.check.impl.combat.aim;

import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.event.api.BaseEvent;
import me.onixdev.event.impl.PlayerRotationEvent;
import me.onixdev.user.OnixUser;

public class AimA extends Check {
    private double buffer;
    private float yawChange;
    public AimA(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("Aim").setType("A").setCheckStage(CheckStage.RELEASE).setDescription("player invalid"));
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof PlayerRotationEvent) {
            PlayerRotationEvent rotationEvent = (PlayerRotationEvent) event;
            if (player.lastHitTime < 4) {
                float yawRate = (float) rotationEvent.getDeltaYaw();//Math.abs(player.xRot - player.lastXRot);
                if ( yawRate < 1) return;
                if (yawRate == yawChange) {
                    if (++buffer > getMaxBuffer()) {
                        fail("x=" + yawRate + ", xc=" + yawChange);
                    }
                }
                else if (buffer > 0) {
                    buffer = buffer - 0.1;
                }
                yawChange = yawRate;
            }
        }
    }
}
