package me.onixdev.check.impl.combat.aim;

import dev.onixac.api.check.CheckInfo;
import dev.onixac.api.check.CheckStage;
import dev.onixac.api.events.api.BaseEvent;
import me.onixdev.check.api.Check;
import me.onixdev.event.impl.PlayerRotationEvent;
import me.onixdev.user.OnixUser;

@CheckInfo(name = "Aim",type = "Y",stage = CheckStage.RELEASE,maxBuffer = 20)
public class AimY extends Check {
    private double buffer;
    public AimY(OnixUser player) {
        super(player);
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof PlayerRotationEvent rotationEvent) {
            if (rotationEvent.isPost() && player.lastHitTime < 10) {
                double dyaw = player.getRotationContainer().getDeltaYaw();
                double dpitch = player.getRotationContainer().getDeltaPitch();
                if (dyaw < 1e-3 || dpitch < 1e-3) return;
                double yawMod = dyaw % player.yawGcd;
                double pitchMod = dpitch % player.pitchGcd;
                boolean badYaw = yawMod > 1e-2 && player.yawGcd - yawMod > 1e-2;
                boolean badPitch = pitchMod > 1e-2 && player.pitchGcd - pitchMod > 1e-2;
                if (badYaw || badPitch) if (++buffer > maxbuffer) fail("bY: " + badYaw + " bP: " + badPitch);
                else if (buffer > 0) buffer-=0.25;
            }
        }
    }
}
