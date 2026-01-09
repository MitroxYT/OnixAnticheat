package me.onixdev.check.impl.combat.aura

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play
import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying
import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerUseEntityEvent
import me.onixdev.event.impl.TickEvent
import me.onixdev.user.OnixUser

class AuraA(player: OnixUser?) : Check(player, CheckBuilder.create().setCheckName("Aura").setType("A").setDescription("PostCheck").setCheckStage(CheckStage.RELEASE).build()) {
    private var send = false
    private var last: Long = 0
    override fun onPacketIn(event: PacketReceiveEvent?) {
        if (WrapperPlayClientPlayerFlying.isFlying(event!!.packetType)) {
            val delay: Long = (System.currentTimeMillis() - last)
            if (player.clickData.cps > 3) return
            if (this.send) {
                if (delay in 39..99) {
                    fail("delay=$delay")
                }

                this.send = false
            }

            this.last = System.currentTimeMillis()
        }

        if (event!!.packetType === Play.Client.INTERACT_ENTITY) {
            val action = WrapperPlayClientInteractEntity(event)
            if (action.action != InteractAction.ATTACK) {
                return
            }


            val delay2: Long = System.currentTimeMillis() - this.last
            if (delay2 < 10L) {
                this.send = true
            }
        }
    }
}