package me.onixdev.check.impl.movement.noslow;

import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.event.api.BaseEvent;
import me.onixdev.event.impl.TickEvent;
import me.onixdev.user.OnixUser;

public class NoslowA extends Check {
    private boolean lastTickNoslow;
    private double buffer;
    public NoslowA(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("Noslow").setType("A").build());
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof TickEvent) {
            if (((TickEvent) event).getTickType() == TickEvent.Target.FLYING) {
                player.sendMessage("w: " + player.theoreticalInput.isForward() + " s: " + player.theoreticalInput.isBackward() + " a: " + player.theoreticalInput.isLeft() + " d: " + player.theoreticalInput.isRight());
                if (player.isUsingItem() && player.getItemUseTime() > 3) {
                    double offsetHorr = player.theoreticalInput.getForwardMotion();
                    double offsetSt = player.theoreticalInput.getStrafe();
                    if (Math.abs(offsetHorr) > 0.1970 || Math.abs(offsetSt) > 0.1970) {
                        //     if (lastTickNoslow) {
                        if (++buffer > 3) {
                            fail("of: " + offsetHorr + ", strafe: " + offsetHorr);
//                            BukkitNMS.resetBukkitItemUsage(player);
                            //setback();
                        }
                        if (player.getItemUseTime() > 6 && !lastTickNoslow) {
                            fail("type [B] of: " + offsetHorr + ", strafe: " + offsetSt);
                        }
                        lastTickNoslow = true;
                    } else if (buffer > 0) {
                        buffer = buffer - 0.5;
                        lastTickNoslow = false;
                    }

                }
            }
        }
    }
}
