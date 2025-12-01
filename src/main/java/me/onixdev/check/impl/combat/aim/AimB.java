package me.onixdev.check.impl.combat.aim;

import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.event.api.BaseEvent;
import me.onixdev.event.impl.PlayerRotationEvent;
import me.onixdev.user.OnixUser;

public class AimB extends Check {
    private double buffer;
    private float yC;

    public AimB(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("Aim").setType("B").setBuffer(4).setCheckStage(CheckStage.RELEASE).build());
    }
    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof PlayerRotationEvent) {
            PlayerRotationEvent rotationEvent = (PlayerRotationEvent) event;
            if (player.lastHitTime < 4 && !rotationEvent.isPost()) {
                float yR = (float) Math.abs(player.getRotationContainer().getPitch() - player.getRotationContainer().getLastPitch());
                if ( yR < 1) return;
                if (yR == yC) {
                    if (++buffer > getMaxBuffer()) {
                        fail("y=" + yC + ", yc=" + yC);
                    }
                }
                else if (buffer > 0) {
                    buffer = buffer - getDecay();
                }
                yC = yR;
            }
        }
    }
}
