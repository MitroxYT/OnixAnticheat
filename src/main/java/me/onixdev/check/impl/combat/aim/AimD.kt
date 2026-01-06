package me.onixdev.check.impl.combat.aim

import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser

class AimD(user: OnixUser) : Check(user, CheckBuilder.create().setCheckName("Aim").setDecay(0.25).setType("D").setBuffer(5.0).build()) {
    private var buffer:Double = 0.0
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerRotationEvent && event.isPost) {
            //TODO тут будет другая проверка предыдущая полная залупа
        }
    }
}