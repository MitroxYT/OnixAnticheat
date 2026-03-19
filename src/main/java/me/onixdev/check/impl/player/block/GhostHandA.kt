package me.onixdev.check.impl.player.block

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.event.impl.PlayerBlockInteractEvent
import me.onixdev.user.OnixUser
import me.onixdev.util.net.PlayerUtil
import me.onixdev.util.world.utils.versions.BlockData

@CheckInfo(name = "GhostHand", type = "A", stage = CheckStage.RELEASE, maxBuffer = 5.0, decayBuffer = 1.0)
class GhostHandA(user: OnixUser) : Check(user) {
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerBlockInteractEvent) {
            val block = event.getBlock()
            if (block.type.name.contains("CHEST")) {
                val valid = block.world == player.bukkitPlayer.world
                if (!valid) return
                val distance = block.location.distance(player.bukkitPlayer.location)
                val vec = player.rotation.toDirection()
                val result = PlayerUtil.raytrace(player.bukkitPlayer, vec, distance, 0.5)
                if (result.second != null) {
                    val mat = result.second ?: return
                    if (BlockData.isPassable(mat)) return
                    val validRay = result.second!!.type == block.type
                    if (!validRay) {
                        fail("result: ${result.second.type} e: ${block.type} ")
                        if (shouldCancel()) event.cancel()
                    }
                }
            }
        }
    }
}