package me.onixdev.user.data

import dev.onixac.api.events.api.BaseEvent
import me.onixdev.event.impl.PlayerUseEntityEvent
import me.onixdev.event.impl.TickEvent
import me.onixdev.user.OnixUser
import me.onixdev.util.net.PlayerUtil
import org.bukkit.entity.Player


class CombatData(private val user: OnixUser) {
    var dist: Double = 0.0
    var lastAttacked: Int = 158145;
    private var lastAttack:Long = 0
    var target: Player? = null;
    fun onEvent(event: BaseEvent) {
        if (event is PlayerUseEntityEvent && event.useType == PlayerUseEntityEvent.UseType.ATTACK && user.bukkitPlayer != null) {
            target = PlayerUtil.getPlayer(event.id)
            if (target != null && user.bukkitPlayer != null) {
                val valid = user.bukkitPlayer.world == target!!.world
                if (valid) {
                    lastAttacked = target!!.entityId
                    lastAttack = System.currentTimeMillis()
                }
            }
        }
        if (event is TickEvent && !event.notTickEnd()) {
            if (target != null && user.bukkitPlayer != null) {
                val valid = user.bukkitPlayer.world == target!!.world
                if (valid) {
                    dist = user.bukkitPlayer.location.distance(target!!.location)
                }
            }
        }
    }
    fun hasAttackedSince(time: Long) : Boolean {
        return System.currentTimeMillis() - lastAttacked < time
    }
    fun getAttackedSince() : Double {
        return System.currentTimeMillis().minus(lastAttack).toDouble()
    }
}