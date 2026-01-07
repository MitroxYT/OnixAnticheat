package me.onixdev.check.impl.combat.aim

import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser
import me.onixdev.util.math.MathUtil
import kotlin.math.abs


class AimD(user: OnixUser) : Check(user, CheckBuilder.create().setCheckName("Aim").setDecay(0.25).setType("D").setBuffer(5.0).build()) {
    private var buffer:Double = 0.0
    private val deltaYaws: MutableList<Double?> = ArrayList<Double?>()
    private val deltaPitches: MutableList<Double?> = ArrayList<Double?>()
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerRotationEvent && event.isPost) {
            var runszs: Double
            val dy: Double = abs(event.deltaYaw)
            val dp: Double = abs(event.deltaPitch)
            this.deltaYaws.add(dy)
            this.deltaPitches.add(dp)
            if (this.deltaYaws.size >= 80) {
                runszs = MathUtil.runsZScore(this.deltaYaws)
                if (runszs > 1.5) {
                    fail("ry: "+format(runszs))
                }
                this.deltaYaws.clear()
            }
            if (this.deltaPitches.size >= 80) {
                runszs = MathUtil.runsZScore(this.deltaPitches)
                if (runszs > 1.5) {
                    fail("rp: "+format(runszs))
                }
                this.deltaPitches.clear()
            }
        }
    }

}