package me.onixdev.check.impl.player.badpackets

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.user.OnixUser

@CheckInfo(name = "BadPacket", type = "C", stage = CheckStage.EXPERIMENTAL, maxBuffer = 5.0, decayBuffer = 1.0)
class BadPacketC(user: OnixUser) :Check(user) {
}