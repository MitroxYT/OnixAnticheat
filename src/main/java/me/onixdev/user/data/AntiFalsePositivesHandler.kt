package me.onixdev.user.data

import dev.onixac.api.events.api.BaseEvent
import me.onixdev.event.impl.PlayerCloseInventoryEvent
import me.onixdev.event.impl.PlayerPacketClickEvent
import me.onixdev.event.impl.TickEvent
import me.onixdev.user.OnixUser

class AntiFalsePositivesHandler(private val user: OnixUser) {
    var InventoryStopingWhileTicks:Int = 0
    var lastClickTime:Int = 100
    var lastStopInventoryTime:Int = 100
    fun onEvent(event: BaseEvent) {
        if (event is TickEvent && event.notTickEnd()) {
            if (InventoryStopingWhileTicks > 0) InventoryStopingWhileTicks--
            lastClickTime++
            lastStopInventoryTime++
        }
        if (event is PlayerCloseInventoryEvent) {
            InventoryStopingWhileTicks++
            lastClickTime++
        }
        if (event is PlayerPacketClickEvent) {
            lastClickTime =0
        }
    }
    fun tryingFalseInvChecks(): Boolean {
        return InventoryStopingWhileTicks > 1 && lastClickTime < 5 && lastStopInventoryTime < 5 || user.connectionContainer.transactionPingMs > 500
    }
}