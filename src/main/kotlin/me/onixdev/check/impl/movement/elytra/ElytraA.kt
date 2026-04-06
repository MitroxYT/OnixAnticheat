package me.onixdev.check.impl.movement.elytra

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.OnixAnticheat
import me.onixdev.check.api.Check
import me.onixdev.event.impl.PlayerPositionUpdateEvent
import me.onixdev.user.OnixUser

@CheckInfo(name = "Elytra", type = "A", maxBuffer = 10.0)
class ElytraA(player: OnixUser) : Check(player) {
    private var buffer = 0.0
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerPositionUpdateEvent) {
            if (bukkitNull()) return
            if (OnixAnticheat.INSTANCE.nmsManager.nmsInstance.isGliding(player.bukkitPlayer)) {
                val motion = event.position
                val motionY = motion.getYDelta()
                val debug = "motion: $motionY"
                player.debug(debug)
                if (motionY > 0.0 && motionY < 0.08) {
                    if (player.lastHitTime < 3) {
                        if (buffer > maxBuffer) {
                            fail("$debug $buffer")
                        }
                    }
                    player.debug("test elytraMotion")
                }
                else if (buffer > 0) buffer -= 0.25
            }
        }
    }

}