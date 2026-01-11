package me.onixdev.check.impl.combat.aim

import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser
import me.onixdev.util.math.DataList
import me.onixdev.util.math.GraphUtil
import me.onixdev.util.math.MathUtil

class AimT(player: OnixUser) : Check(player, CheckBuilder.create().setCheckName("Aim").setType("T").build()) {
    val yawSamples: DataList<Double> = DataList(30)
    val pitchSamples: DataList<Double> = DataList(30)
    val shortYawSamples: DataList<Double> = DataList(15)
    val shortPitchSamples: DataList<Double> = DataList(15)
    override fun onEvent(event: BaseEvent?) {
        if (event !is PlayerRotationEvent || event.isPost) return

        val shouldAnalyze = player.lastHitTime < 20
        if (shouldAnalyze) {
            yawSamples.add(event.deltaYaw)
            pitchSamples.add(event.deltaPitch)
            shortYawSamples.add(event.deltaYaw)
            shortPitchSamples.add(event.deltaPitch)

            if (yawSamples.isCollected) {
                val yawStd = MathUtil.calculateStdDev(yawSamples)
                val graphResult = GraphUtil.getGraph(yawSamples)
                val positives = graphResult.positives

                val negatives = graphResult.negatives

                if (positives == 0 && yawStd > 25) {
                    fail("Zero positives, high std: $yawStd")
                } else if (negatives == 0 && yawStd > 25) {
                    fail("Zero negatives, high std: $yawStd")
                }
                yawSamples.clear()
            }

        }
    }
}