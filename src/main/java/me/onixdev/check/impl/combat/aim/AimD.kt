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
            if (player.lastHitTime < 4) {
                val sens = player.sensitivity
                //Спасибо Mojang за такую ахуенную калькуляцию сенсы
                // У меня при ровно 100.0 сенсе кратные дельты ровно и меня фолсят кое какие ач
                // У друга при 200 сенсы сенса улетает до 269
                if (sens < 0 || sens > 269) {
                    if (++buffer > maxBuffer) {
                        val form = String.format("%.5f", sens)
                        fail(form)
                    }
                }
                else if (buffer > 0) buffer-= decay

            }
        }
    }
}