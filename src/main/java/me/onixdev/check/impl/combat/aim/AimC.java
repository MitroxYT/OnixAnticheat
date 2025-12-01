package me.onixdev.check.impl.combat.aim;

import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.event.api.BaseEvent;
import me.onixdev.event.impl.PlayerRotationEvent;
import me.onixdev.user.OnixUser;


public class AimC extends Check {
    private double lastX,lastXPost;

    public AimC(OnixUser player) {
        super(player,CheckBuilder.create().setCheckName("Aim").setType("C").setCheckStage(CheckStage.BETA).build());
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof PlayerRotationEvent) {
            PlayerRotationEvent rotationEvent = (PlayerRotationEvent) event;
            boolean valid = player.lastHitTime < 60;
            if (!valid) return;
            if (!rotationEvent.isPost()) {
                double dx = rotationEvent.getDeltaYaw();
                double acelx = Math.abs(dx - lastX);
                double absdx = Math.abs(dx);

                if (absdx > 170.0f && lastX < 50 && acelx > 100) {

                    String deltax = String.format("%.5f", dx);
                    String ldeltax = String.format("%.5f", lastX);
                    String acelX = String.format("%.5f", acelx);
                    if (acelx > 400) {
                        fail("type=Pre dx: " + deltax + " ldx: " + ldeltax + " ax: " + acelX);
                    }
//                    if (player.isGliding) {
//                        fail("dx: " + deltax + " ldx: " + ldeltax + " ax: " + acelX);
//                    }

                }
                lastX = dx;
            }else {
                double dx = rotationEvent.getDeltaYaw();
                double acelx = Math.abs(dx - lastXPost);
                double absdx = Math.abs(dx);

                if (absdx > 170.0f && lastXPost < 50 && acelx > 100) {

                    String deltax = String.format("%.5f", dx);
                    String ldeltax = String.format("%.5f", lastXPost);
                    String acelX = String.format("%.5f", acelx);
                    if (acelx > 400) {
                        fail("type=Post dx: " + deltax + " ldx: " + ldeltax + " ax: " + acelX);
                    }
//                    if (player.isGliding) {
//                        fail("dx: " + deltax + " ldx: " + ldeltax + " ax: " + acelX);
//                    }

                }
                lastXPost = dx;
            }
        }
    }
}
