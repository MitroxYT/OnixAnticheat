package me.onixdev.check.impl.combat.aim

import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser
import kotlin.math.abs

class AimC(player: OnixUser?) :
    Check(player, CheckBuilder.create().setCheckName("Aim").setType("C").setCheckStage(CheckStage.BETA).build()) {
    private var lastX = 0.0
    private var lastXPost = 0.0

    override fun onEvent(event: BaseEvent) {
        if (event is PlayerRotationEvent) {
            val rotationEvent = event
            val valid = player.lastHitTime < 60
            if (!valid) return
            if (!rotationEvent.isPost) {
                val dx = rotationEvent.deltaYaw
                val acelx = abs(dx - lastX)
                val absdx = abs(dx)
                if (player.lastTeleportTime < 5) return
                val valid = player.combatData.dist > 0.1
                if (absdx > 170.0f && lastX < 50 && acelx > 100 && valid) {
                    val deltax = String.format("%.5f", dx)
                    val ldeltax = String.format("%.5f", lastX)
                    val acelX = String.format("%.5f", acelx)
                    if (acelx > 400) {
                        fail("type=Pre dx: $deltax ldx: $ldeltax ax: $acelX")
                    }

                    //                    if (player.isGliding) {
//                        fail("dx: " + deltax + " ldx: " + ldeltax + " ax: " + acelX);
//                    }
                }
                lastX = dx
            }
//            } else {
//                val dx = rotationEvent.deltaYaw
//                val acelx = abs(dx - lastXPost)
//                val absdx = abs(dx)
//
//                if (absdx > 170.0f && lastXPost < 50 && acelx > 100) {
//                    val deltax = String.format("%.5f", dx)
//                    val ldeltax = String.format("%.5f", lastXPost)
//                    val acelX = String.format("%.5f", acelx)
//                    if (acelx > 400) {
//                        fail("type=Post dx: $deltax ldx: $ldeltax ax: $acelX")
//                    }
//
//                    //                    if (player.isGliding) {
////                        fail("dx: " + deltax + " ldx: " + ldeltax + " ax: " + acelX);
////                    }
//                }
//                lastXPost = dx
//            }
        }
    }
}