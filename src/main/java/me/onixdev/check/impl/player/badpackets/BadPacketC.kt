package me.onixdev.check.impl.player.badpackets

import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.user.OnixUser

class BadPacketC(user: OnixUser) :Check(user, CheckBuilder.create().setCheckName("BadPacket").setType("C").build()) {
}