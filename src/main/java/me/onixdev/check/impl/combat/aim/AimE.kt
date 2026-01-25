package me.onixdev.check.impl.combat.aim

import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser
import me.onixdev.util.math.MathUtil
import java.lang.Double.max
import java.lang.String
import kotlin.Double
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.compareTo
import kotlin.inc
import kotlin.math.roundToInt
import kotlin.times

class AimE(user: OnixUser) : Check(user, CheckBuilder.create().setCheckName("Aim").setType("E").setBuffer(3.0).build()) {
    private var buffer:Double = 0.0
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerRotationEvent && event.isPost && player.lastHitTime < 20) {
            val deltaPitch: Float = player.rotationContainer.deltaPitchABS
            val lastDeltaPitch: Float = player.rotationContainer.lastDeltaPitchABS

            val sensitivityF = player.sensitivity.roundToInt()
            val sensitivityY = player.rotationContainer.sensitivity

            val expandedDeltaPitch = (deltaPitch * MathUtil.EXPANDER).toLong()
            val expandedLastDeltaPitch = (lastDeltaPitch * MathUtil.EXPANDER).toLong()

            val gcd: Long = MathUtil.getGcd(expandedDeltaPitch, expandedLastDeltaPitch)

            val exempt = deltaPitch == 0f || player.rotationContainer.hasTooLowSensitivity()
                    || lastDeltaPitch == 0f || player.rotationContainer.isCinematicRotation

            if (!exempt && gcd < 131072L) {
                if (++buffer > maxbuffer) {
                    val debug = String.format(
                        "Type: A | gcd=%s, sensF=%s, sens=%s, buffer=%.2f",
                        gcd,
                        sensitivityF,
                        sensitivityY,
                        buffer
                    )
                    fail(debug)
                }
            } else {
                buffer = max(0.0, buffer - 1)
            }
//            if (player.lastHitTime < 4) {
//                if (player.lastTeleportTime < 5) return
//                val sens = player.sensitivity
//                val valid = player.combatData.dist > 0.1
//                if (sens < 0 && !player.rotationContainer.isCinematicRotation && valid) {
//                    if (++buffer > maxBuffer) {
//                        val form = String.format("%.5f", sens)
//                        fail(form)
//                    }
//                }
//                else if (buffer > 0) buffer-= decay
//
//            }
        }
    }
}