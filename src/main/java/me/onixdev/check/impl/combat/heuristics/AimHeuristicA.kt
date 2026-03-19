package me.onixdev.check.impl.combat.heuristics

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser
import kotlin.math.abs

@CheckInfo(name = "AimHeuristic", type = "A", stage = CheckStage.RELEASE, maxBuffer = 5.0, decayBuffer = 1.0)
class AimHeuristicA(user: OnixUser?) : Check(user) {
    private var lastDeltaYaw = 0.0
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerRotationEvent) {
            if (!event.isPost) {
                if (player.lastTeleportTime < 10 || player.inVehicle()) {
                    lastDeltaYaw = event.deltaYaw
                    return
                }

                if (player.rotation.yaw < 360 && player.rotation.yaw > -360 && event.deltaYaw > 320 && abs(
                        lastDeltaYaw
                    ) < 30
                ) {
                    val info = String.format(
                        "dx=%.2f, ldx=%.2f, la=%d",
                        event.deltaYaw,
                        abs(lastDeltaYaw),
                        (player.lastHitTime)
                    )

                    fail(info)
                }

                lastDeltaYaw = event.deltaYaw
            }
        }
    }
}