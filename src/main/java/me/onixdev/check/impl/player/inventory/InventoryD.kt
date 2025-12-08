package me.onixdev.check.impl.player.inventory

import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.user.OnixUser

class InventoryD(user: OnixUser) : Check(user, CheckBuilder.create().setCheckName("Inventory").setType("D")) {
}