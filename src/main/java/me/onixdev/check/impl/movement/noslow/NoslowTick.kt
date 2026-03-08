package me.onixdev.check.impl.movement.noslow

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.user.OnixUser
@CheckInfo(name = "Noslow", type = "B", stage = CheckStage.EXPERIMENTAL, maxBuffer = 5.0, decayBuffer = 1.0)
class NoslowTick(user: OnixUser) : Check(user) {
}