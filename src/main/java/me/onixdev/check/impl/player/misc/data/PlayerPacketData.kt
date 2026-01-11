package me.onixdev.check.impl.player.misc.data

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.DiggingAction
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.user.OnixUser
import me.onixdev.util.net.PacketUtil

class PlayerPacketData(user: OnixUser) : Check(user, CheckBuilder().setCheckName("AAAA").setType("A").build()) {
    var attacking = false
    var swapping = false
    var sprinting = false
    var heldItemChange = false
    var using = false
    override fun onPacketIn(event: PacketReceiveEvent?) {
        val packetType = event?.packetType
        when (packetType) {
            PacketType.Play.Client.HELD_ITEM_CHANGE -> heldItemChange = true
            PacketType.Play.Client.PLAYER_DIGGING -> {
                val dig = WrapperPlayClientPlayerDigging(event)
                if (dig.action == DiggingAction.SWAP_ITEM_WITH_OFFHAND) swapping = true
            }
            PacketType.Play.Client.USE_ITEM -> using = true

            PacketType.Play.Client.ENTITY_ACTION -> {
                val ac = WrapperPlayClientEntityAction(event)
                if (ac.action == WrapperPlayClientEntityAction.Action.START_SPRINTING || ac.action == WrapperPlayClientEntityAction.Action.STOP_SPRINTING) sprinting = true
            }

            PacketType.Play.Client.INTERACT_ENTITY -> {
                val ac = WrapperPlayClientInteractEntity(event)
                if (ac.action == WrapperPlayClientInteractEntity.InteractAction.ATTACK) attacking = true
            }
        }
        if (PacketUtil.isTickPacketLegacy(packetType)) {
            swapping = false
            sprinting = false
            heldItemChange = false
            using = false
            attacking = false
        }
    }
}