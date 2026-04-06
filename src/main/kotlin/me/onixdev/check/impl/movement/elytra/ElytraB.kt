package me.onixdev.check.impl.movement.elytra

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.OnixAnticheat
import me.onixdev.check.api.Check
import me.onixdev.event.impl.PlayerPositionUpdateEvent
import me.onixdev.user.OnixUser

@CheckInfo(name = "Elytra", type = "B", maxBuffer = 3.0, stage = CheckStage.EXPERIMENTAL)
class ElytraB(player: OnixUser) : Check(player) {

    private var lastTime = 0
    private var tickExempt = 0
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerPositionUpdateEvent) {
            if (bukkitNull()) return

            tickExempt++;

            if (
                event.position.isOnGround) {
                tickExempt = 0;
            }

            lastTime++;
            if (player.lastFireWorkTime < 2) {
                lastTime = 0;
            }
            if (OnixAnticheat.INSTANCE.nmsManager.nmsInstance.isGliding(player.bukkitPlayer)) {
                val deltaXZ = player.movementContainer.deltaXZ
                val actualMovementY = event.position.getYDelta()
                val exempt = player.rotation.pitch >= 30.0;
                if (lastTime == 0 && tickExempt > 3 && !exempt) {
                    if (deltaXZ > 1.73 && deltaXZ < 1.8 || actualMovementY > 1.62 && actualMovementY < 1.7) {
                        fail(String.format("xz: %.3f, y: %.3f", deltaXZ, actualMovementY))
                    }
                }
            }
        }
    }
}