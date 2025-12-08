package me.onixdev.check.impl.movement.noslow

import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.user.OnixUser

class NoslowTick(user: OnixUser) : Check(user, CheckBuilder.create().setCheckName("Noslow").setType("B").build()) {
}