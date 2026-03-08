package me.onixdev.check.impl.combat.aim

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser
import me.onixdev.util.math.MathUtil
import kotlin.math.abs

@CheckInfo(name = "Aim", type = "D", stage = CheckStage.EXPERIMENTAL, decayBuffer = 1.0)
class AimD(user: OnixUser) : Check(user) {
    private var buffer:Double = 0.0
    private val deltaYaws: MutableList<Double?> = ArrayList<Double?>()
    private val deltaPitches: MutableList<Double?> = ArrayList<Double?>()
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerRotationEvent && event.isPost) {
            var runszs: Double
            val dy: Double = abs(event.deltaYaw)
            val dp: Double = abs(event.deltaPitch)
            if (player.lastTeleportTime < 5) return
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