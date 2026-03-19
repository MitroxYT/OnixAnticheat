package me.onixdev.check.impl.player.inventory

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.event.impl.PlayerClickEvent
import me.onixdev.user.OnixUser

@CheckInfo(name = "Inventory", type = "C", stage = CheckStage.RELEASE, maxBuffer = 5.0, decayBuffer = 1.0)
class InventoryC(player: OnixUser?) : Check(player) {
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerClickEvent) {
            if (player.lastTeleportTime < 5) return
            if (player.theoreticalInput.hasInput()) {
                val verbose = StringBuilder()
                if (player.theoreticalInput.isForward()) {
                    verbose.append("W")
                    verbose.append(" ")
                }
                if (player.theoreticalInput.isBackward()) {
                    verbose.append("S")
                    verbose.append(" ")
                }
                if (player.theoreticalInput.isLeft()) {
                    verbose.append("A")
                    verbose.append(" ")
                }
                if (player.theoreticalInput.isRight()) {
                    verbose.append("D")
                    verbose.append(" ")
                }
                failAndSetback(" pressed key: $verbose")
                if (shouldCancel()) event.cancel()
            }
        }
    }
}