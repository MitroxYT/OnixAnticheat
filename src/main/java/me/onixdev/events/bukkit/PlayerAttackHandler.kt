package me.onixdev.events.bukkit

import me.onixdev.OnixAnticheat
import me.onixdev.util.extend.KotlinExtends.getData
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class PlayerAttackHandler : Listener {
    @EventHandler
    fun onPlayerAttack(event: EntityDamageByEntityEvent) {
        if (event.damager is Player) {
            val player = event.damager as Player
            val onixUser = player.getData()
            if (onixUser != null) {
               if (onixUser.shouldMitigate() && onixUser.mitigateType == "reducedamage") {
                   event.damage *= OnixAnticheat.INSTANCE.configManager.damageMultiPlayer
               }
            }
        }
    }
}