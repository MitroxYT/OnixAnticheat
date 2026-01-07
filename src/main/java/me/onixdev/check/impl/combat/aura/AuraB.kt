package me.onixdev.check.impl.combat.aura

import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerUseEntityEvent
import me.onixdev.user.OnixUser
import me.onixdev.util.net.PlayerUtil
import org.bukkit.Material
import java.util.Locale

class AuraB(player:OnixUser) : Check(player, CheckBuilder.create().setCheckName("Aura").setCheckStage(CheckStage.EXPERIMENTAL).setType("B").build()){
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerUseEntityEvent && event.useType == PlayerUseEntityEvent.UseType.ATTACK && player.bukkitPlayer != null){
            val target = PlayerUtil.getPlayer(event.id)
            if (target != null) {
                val valid = player.bukkitPlayer.world == target.world
                if (valid) {
                    val dist = player.bukkitPlayer.location.distance(target.location)
                    val vec = player.rotation.toDirection()
                    //PlayerUtil.getDirection(player.rotationContainer.yaw.toFloat(),player.rotationContainer.pitch.toFloat())
                    val result = PlayerUtil.raytrace(player.bukkitPlayer,vec,dist,0.1)
                    if (result.second != null) {
                        player.debug(result.second.type.name + " 1: " + result.first)
                        val mat = result.second
                        if (mat != null) {
                            if (mat.type.name.lowercase(Locale.ROOT).contains("slab") || mat.type.name.lowercase(Locale.ROOT).contains("Fence")) return
                        }
                        fail(result.second.type.name)
                        if (shouldCancel()) event.cancel()
                    }
                }
            }
        }
    }
}