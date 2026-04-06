package me.onixdev.check.impl.movement.elytra

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.event.impl.PlayerPositionUpdateEvent
import me.onixdev.user.OnixUser

@CheckInfo(name = "Elytra", type = "A")
class ElytraA(player: OnixUser) : Check(player) {

    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerPositionUpdateEvent) {
            val motion = event.position
            val motionY = motion.getYDelta();
            val debug = "motion: $motionY"
            player.debug(debug)
        }
    }
}