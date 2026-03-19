package me.onixdev.check.impl.player.inventory

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.event.impl.PlayerClickEvent
import me.onixdev.user.OnixUser
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

@CheckInfo(name = "Inventory", type = "A", stage = CheckStage.RELEASE, maxBuffer = 5.0, decayBuffer = 1.0)
class InventoryA(player: OnixUser?) : Check(player) {
    private var lastSlot = -1
    private var lastItem: ItemStack? = null
    private var lastClick: Long = 0
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerClickEvent) {
            val time = System.currentTimeMillis() - lastClick
            val stack = event.current
            val slot = event.rawSlot
            if ((event.slot_type == InventoryType.SlotType.CONTAINER || event.slot_type == InventoryType.SlotType.ARMOR || event.slot_type == InventoryType.SlotType.QUICKBAR)) {
                if (lastItem != null && lastItem!!.type == stack!!.type) {
                    return
                }
                val parsedItem = player.bukkitPlayer.inventory.getItem(slot)
                val click = event.clickType
                if (click == ClickType.CREATIVE) return
                val action = event.action
                val inv = event.slot_type
                if (time <= 1) {
                    val type =
                        stack?.type?.name ?: (parsedItem?.type?.name ?: "none")
                    fail("item: $type, slot: $slot time: $time cT: $click aT: $action sT: $inv")
                    if (shouldCancel()) event.cancel()
                }

                lastSlot = slot
                lastItem = stack
                lastClick = System.currentTimeMillis()
            }
        }
    }


}