package me.onixdev.check.impl.combat.aim

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser


@CheckInfo(name = "Aim", type = "Y", stage = CheckStage.RELEASE, maxBuffer = 100.0)
class AimY(player: OnixUser?) : Check(player) {
    private var buffer = 0.0
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerRotationEvent) {
            if (event.isPost && player.lastHitTime < 10) {
                if (player.yawGcd == 0f || player.pitchGcd == 0f) return
                val dyaw = player.rotationContainer.deltaYaw.toDouble()
                val dpitch = player.rotationContainer.deltaPitch.toDouble()
                if (dyaw < 1e-3 || dpitch < 1e-3) return
                val yawMod = dyaw % player.yawGcd
                val pitchMod = dpitch % player.pitchGcd
                val badYaw = yawMod > 1e-2 && player.yawGcd - yawMod > 1e-2
                val badPitch = pitchMod > 1e-2 && player.pitchGcd - pitchMod > 1e-2
                if (badYaw || badPitch) {
                    if (++buffer > maxbuffer) {
                        val info = String.format("yM=%.5f, pM=%.5f ", yawMod, pitchMod)
                        fail("$info bY: $badYaw bP: $badPitch")
                        buffer -= 0.9
                    }
                } else if (buffer > 0) buffer -= 0.25
            }
        }
    }
}
