package me.onixdev.check.impl.combat.heuristics;

import dev.onixac.api.events.api.BaseEvent;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.event.impl.PlayerRotationEvent;
import me.onixdev.user.OnixUser;

public class AimHeuristicA extends Check {
    //private AimHeuristicAS aimHeuristicAS;
    private double lastDeltaYaw;
    public AimHeuristicA(OnixUser user) {
        super(user, CheckBuilder.create().setCheckName("AimHeuristic").setType("A").build());
//        aimHeuristicAS = new AimHeuristicAS();
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof PlayerRotationEvent rotationEvent) {
            if (!rotationEvent.isPost()) {
                if (player.lastTeleportTime < 10 || player.inVehicle()) {
                    lastDeltaYaw = rotationEvent.getDeltaYaw();
                    return;
                }

                if (player.rotation.getYaw() < 360 && player.rotation.getYaw() > -360 && rotationEvent.getDeltaYaw() > 320 && Math.abs(lastDeltaYaw) < 30) {
                    String info = String.format("dx=%.2f, ldx=%.2f, la=%d", rotationEvent.getDeltaYaw(), Math.abs(lastDeltaYaw), (player.lastHitTime));

                    fail(info);
                }

                lastDeltaYaw = rotationEvent.getDeltaYaw();
//                if (player.combatData.getTarget() != null && player.lastTeleportTime > 50) {
//                    float yaw = (float) player.rotation.getYaw();
//                    float lastYaw1 = player.getRotationContainer().getLastYaw();
//                    if (AimHeuristicAS.isObserving(aimHeuristicAS)) {
//                        if (true) {
//                            float lastYaw = player.getRotationContainer().getLastYaw();
//                            if (lastYaw != 0.0F) {
//                                String res = "possible rotation modulo clamp";
//                                player.debug(res);
//                                fail(res);
//                            }
//                        }
//
//                       AimHeuristicAS.setObserving(aimHeuristicAS, false);
//                    } else {
//                        if (player.combatData.hasAttackedSince(1000L)) {
//                            float deltaX = Math.abs(yaw - lastYaw1);
//                            boolean factorOne = Math.abs(yaw) <= 360.0F && Math.abs(lastYaw1) <= 360.0F;
//                            boolean factorTwo = factorOne && deltaX > 100.0F;
//                            if (factorTwo) {
//                                AimHeuristicAS.setObserving(aimHeuristicAS, true);
//                            }
//                        }
//
//                    }
//                }
            }
        }
    }
}
