package me.onixdev.check.impl.player.inventory

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import me.onixdev.check.api.Check
import me.onixdev.user.OnixUser
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@CheckInfo(name = "Inventory", type = "D", stage = CheckStage.RELEASE, description = "Chest Stealer")
class InventoryD(user: OnixUser) : Check(user) {
    @JvmField
    var itemStealerVL: Int = 0

    @JvmField
    var lastClickInv: Long = 0

    @JvmField
    var lastClickedSlot: Int = 0

    @JvmField
    var lastClickedItemStack: ItemStack = org.bukkit.inventory.ItemStack(Material.AIR)
}