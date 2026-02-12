package me.onixdev.check.impl.combat.aim

import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser
import kotlin.math.abs

class AimF(player:OnixUser) : Check(player,CheckBuilder.create().setCheckName("Aim").setType("F").build()) {
    private var lastTickFlagget: Boolean = false
    private var minFovFactor:Double = Double.MAX_VALUE
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerRotationEvent && !event.isPost && player.lastHitTime < 4) {
            if (player.lastTeleportTime < 5 && player.movementContainer.deltaXZ < 0.09) return
            val rotationData = player.rotationContainer
            val dx = rotationData.deltaYaw
            val dy = rotationData.deltaPitch
            val sx = rotationData.smoothnessYaw
            val sy = rotationData.smoothnessPitch

            if (dx > minFovFactor && sx < -5.0F && player.movementContainer.deltaXZ > 0.05) {
                val info = String.format("dx=%.5f, dy=%.5f, sx=%.5f, sy=%.5f ", dx, dy, sx, sy)
                if (lastTickFlagget) {
                    fail(info)
                    lastTickFlagget = false
                }
                lastTickFlagget = true
//            var yawDiff: Float = abs(player.rotationContainer.lastYaw - player.rotationContainer.yaw)
//            if (yawDiff > 180.0f) {
//                yawDiff = 360.0f - yawDiff
//            }
//            if (yawDiff > minFovFactor) {
//                if (lastTickFlagget) {
//                    fail(String.format("%.5f", yawDiff))
//                }
//                lastTickFlagget = true
//            }
                //  else lastTickFlagget = false
            }
        }
    }

    override fun reload() {
        minFovFactor = checkConfig.getDouble(checkPatch+"minFov",85.0)
        super.reload()
    }
}