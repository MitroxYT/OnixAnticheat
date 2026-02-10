package me.onixdev.check.impl.player.block

import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerBlockInteractEvent
import me.onixdev.user.OnixUser
import me.onixdev.util.net.PlayerUtil
import me.onixdev.util.world.utils.versions.BlockData

class GhostHandA(user: OnixUser) : Check(user, CheckBuilder().setCheckName("GhostHand").setDescription("It doesn't allow opening the chest through blocks.").setType("A").setCheckStage(
    CheckStage.EXPERIMENTAL).build()) {
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerBlockInteractEvent) {
            val block = event.getBlock()
            if (block.type.name.contains("CHEST")) {
                val valid = block.world == player.bukkitPlayer.world
                if (!valid) return
                val distance = block.location.distance(player.bukkitPlayer.location)
                val vec = player.rotation.toDirection()
                val result = PlayerUtil.raytrace(player.bukkitPlayer,vec,distance,0.1)
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