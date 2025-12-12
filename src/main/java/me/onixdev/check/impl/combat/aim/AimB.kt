package me.onixdev.check.impl.combat.aim

import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser
import kotlin.math.abs

class AimB(player: OnixUser?) :
    Check(
        player,
        CheckBuilder.create().setCheckName("Aim").setType("B").setBuffer(4.0).setCheckStage(CheckStage.RELEASE).build()
    ) {
    private var buffer = 0.0
    private var yC = 0f

    override fun onEvent(event: BaseEvent) {
        if (event is PlayerRotationEvent) {
            if (player.lastHitTime < 4 && !event.isPost) {
                val yR =
                    abs(player.rotationContainer.pitch - player.rotationContainer.lastPitch)
                        .toFloat()
                if (yR < 1) return
                if (yR == yC) {
                    if (++buffer > maxBuffer) {
                        fail("y=$yC, yc=$yC")
                    }
                } else if (buffer > 0) {
                    buffer -= getDecay()
                }
                yC = yR
            }
        }
    }
}