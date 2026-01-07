package me.onixdev.check.impl.player.block

import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerBlockBreakEvent
import me.onixdev.user.OnixUser
import me.onixdev.util.net.PlayerUtil
import java.util.Locale

class BedBreakerA(user: OnixUser) : Check(user, CheckBuilder().setCheckName("BedBreaker").setCheckStage(CheckStage.EXPERIMENTAL).setType("A").build()) {
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerBlockBreakEvent) {
            val block = event.getBlock()
            if (player.bukkitPlayer != null) {
                val valid = block.world == player.bukkitPlayer.world
                if (!valid) return
                val distance = block.location.distance(player.bukkitPlayer.location)
                val vec = PlayerUtil.getDirection(player.rotationContainer.yaw, player.rotationContainer.pitch)
                val result = PlayerUtil.raytrace(player.bukkitPlayer,vec,distance,0.1)
                if (result.second != null) {
                    if (!block.type.name.lowercase(Locale.ROOT).contains("bed")) return
                    val validRay = result.second!!.type == block.type
                    if (!validRay) {
                        fail("result: ${result.second.type} e: ${block.type} ")
                    }
                }
            }
        }
    }
}