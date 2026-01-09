package me.onixdev.check.impl.player.badpackets

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerActionPacket
import me.onixdev.user.OnixUser


class BadPacketB(player: OnixUser?) :
    Check(player, CheckBuilder.create().setCheckName("BadPacket").setType("B").build()) {
    private var threshold: Long = 0
    private var buffer = 0.0

    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerActionPacket) {
            if (event.action === WrapperPlayClientEntityAction.Action.START_SPRINTING) {
                val passed: Long = player.getValue("sprintstop").get() as Long
                // проверка работала всегда а не только при пвп ><
                val imIdiot = player.lastHitTime < 12
                if (passed < threshold && imIdiot) {
                    if (++buffer > maxBuffer) {
                        fail("time: $passed ms")
                    }
                } else if (buffer > 0) {
                    buffer -= 0.25
                }
            }
        }
    }

    fun validdate(): Boolean {
        if (player.isUsingBukkitItem) {
            return false
        }
        return true
    }

    override fun reload() {
        threshold = checkConfig.getDouble(checkPatch + "threshold", 75.0).toLong()
        super.reload()
    }
}
