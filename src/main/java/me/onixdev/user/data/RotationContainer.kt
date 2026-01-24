package me.onixdev.user.data

import dev.onixac.api.user.data.IPlayerRotationData
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser
import me.onixdev.util.math.GraphUtil
import me.onixdev.util.math.MathUtil
import me.onixdev.util.net.MinecraftValues
import me.onixdev.util.rotation.Rotation
import java.util.*
import kotlin.math.abs
import kotlin.math.cbrt

class RotationContainer(private val user: OnixUser) : IPlayerRotationData {
    private var yaw = 0f
    private var pitch = 0f
    private var lastYaw = 0f
    private var lastPitch = 0f
    private var deltaYaw = 0f
    private var deltaPitch = 0f
    private var lastDeltaYaw = 0f
    private var lastDeltaPitch = 0f
    private var yawAccel = 0f
    var deltaYawABS = 0f
    var deltaPitchABS = 0f
    var lastDeltaYawABS = 0f
    var lastDeltaPitchABS = 0f
    private var pitchAccel = 0f
    private var lastYawAccel = 0f
    private var lastPitchAccel = 0f
    private var rawMouseDeltaX = 0f
    private var rawMouseDeltaY = 0f
    private var fuckedPredictedPitch = 0f
    private var fuckedPredictedYaw = 0f
    private var lastFuckedPredictedPitch = 0f
    private var lastFuckedPredictedYaw = 0f
    private var cinematic = false
    private var finalSensitivity = 0.0
    private var mcpSensitivity = 0.0
    private val sensitivitySamples: ArrayDeque<Int?> = ArrayDeque<Int?>()
    private var sensitivity = 0
    private var mouseDeltaX = 0
    private var mouseDeltaY = 0

    private var lastJoltYaw = 0f

    private var lastJoltPitch = 0f

    private var joltYaw = 0f

    private var joltPitch = 0f
    private var smoothnessYaw = 0.0f
    private var smoothnessPitch = 0.0f
    private var consistency = 0.0f
    private var lastSmooth = 0L
    private var lastHighRate = 0L
    private val yawSamples: MutableList<Double?> = ArrayList<Double?>()
    private val pitchSamples: MutableList<Double?> = ArrayList<Double?>()
    private var cinematicRotation = false
    private var isTotallyNotCinematic = 0

    fun handle(yaw: Double, pitch: Double) {
        user.rotation = Rotation(yaw, pitch)
        this.lastYaw = this.yaw
        this.lastPitch = this.pitch
        this.yaw = yaw.toFloat()
        this.pitch = pitch.toFloat()
        this.lastJoltYaw = this.joltYaw
        this.lastJoltPitch = this.joltPitch
        this.joltYaw = abs(deltaYaw - lastDeltaYaw)
        this.joltPitch = abs(deltaPitch - lastDeltaPitch)
        this.lastDeltaYawABS = this.deltaYawABS
        this.lastDeltaPitchABS = this.deltaPitchABS
        this.deltaYawABS = abs(yaw - this.lastYaw).toFloat()
        this.deltaPitchABS = abs(pitch - this.lastPitch).toFloat()
        this.lastDeltaYaw = this.deltaYaw
        this.lastDeltaPitch = this.deltaPitch
        this.deltaYaw = abs(yaw - this.lastYaw).toFloat()
        this.deltaPitch = abs(pitch - this.lastPitch).toFloat()
        val preRotation = PlayerRotationEvent(false, yaw, pitch, deltaYaw.toDouble(), deltaPitch.toDouble())
        user.handleEvent(preRotation)
        this.lastPitchAccel = this.pitchAccel
        this.lastYawAccel = this.yawAccel
        this.yawAccel = abs(this.deltaYaw - this.lastDeltaYaw)
        this.pitchAccel = abs(this.deltaPitch - this.lastDeltaPitch)
        val f = this.mcpSensitivity.toFloat() * 0.6f + 0.2f
        val gcd = f * f * f * 1.2f
        this.rawMouseDeltaX = this.deltaYaw / gcd
        this.rawMouseDeltaY = this.deltaPitch / gcd
        this.mouseDeltaX = (this.deltaYaw / gcd).toInt()
        this.mouseDeltaY = (this.deltaPitch / gcd).toInt()
        this.processCinematic()
        val expectedYaw = this.deltaYaw * 1.073742f + (this.deltaYaw + 0.15).toFloat()
        val expectedPitch = this.deltaPitch * 1.073742f - (this.deltaPitch - 0.15).toFloat()
        val pitchDiff = abs(this.deltaPitch - expectedPitch)
        val yawDiff = abs(this.deltaYaw - expectedYaw)
        this.lastFuckedPredictedPitch = this.fuckedPredictedPitch
        this.lastFuckedPredictedYaw = this.fuckedPredictedYaw
        this.fuckedPredictedPitch = abs(this.deltaPitch - pitchDiff)
        this.fuckedPredictedYaw = abs(this.deltaYaw - yawDiff)
        this.smoothnessYaw = 10.0f - abs(yawAccel - lastYawAccel)
        this.smoothnessPitch = 5.0f - abs(pitchAccel - lastPitchAccel)
        this.consistency = 1.0f - abs(1.0f - (deltaYaw / (deltaPitch + 0.0001f)))

        if (this.deltaPitch > 0.1 && this.deltaPitch < 25.0f) {
            this.processSensitivity()
        }
        val postRotation = PlayerRotationEvent(true, yaw, pitch, deltaYaw.toDouble(), deltaPitch.toDouble())
        user.handleEvent(postRotation)
        //        lastYaw = this.yaw;
//        lastPitch = this.pitch;
//        if (user.getMovementContainer().getSetbackLocation() != null) {
//            user.getMovementContainer().getSetbackLocation().setYaw((float) yaw);
//            user.getMovementContainer().getSetbackLocation().setPitch((float) pitch);
//        }
//        this.yaw = yaw;
//        this.pitch = pitch;
//        lastDeltaYaw = deltaYaw;
//        lastDeltaPitch = deltaPitch;
//        this.deltaYaw = Math.abs(yaw - this.lastYaw);
//        this.deltaPitch = Math.abs(pitch - this.lastPitch);
//        PlayerRotationEvent preRotation = new PlayerRotationEvent(false, yaw, pitch, deltaYaw, deltaPitch);
//        user.handleEvent(preRotation);
//        if (this.deltaPitch > 0.1 && this.deltaPitch < 25.0f) {
//            this.processSensitivity();
//        }
//        processCinematic();
//        //Тут будет просчет сенсы и тд
//        PlayerRotationEvent postRotation = new PlayerRotationEvent(true, yaw, pitch, deltaYaw, deltaPitch);
//        user.handleEvent(postRotation);
    }

    private fun processSensitivity() {
        val gcd = MathUtil.getGcd(this.deltaPitch.toDouble(), this.lastDeltaPitch.toDouble()).toFloat()
        val sensitivityModifier = cbrt(0.8333 * gcd)
        val sensitivityStepTwo = 1.666 * sensitivityModifier - 0.3333
        val finalSensitivity = sensitivityStepTwo * 200.0
        this.finalSensitivity = finalSensitivity
        this.sensitivitySamples.add(finalSensitivity.toInt())
        if (this.sensitivitySamples.size == 40) {
            this.sensitivity = MathUtil.getMode(this.sensitivitySamples)
            if (this.hasValidSensitivity()) {
                this.mcpSensitivity = MinecraftValues.SENSITIVITY_MCP_VALUES[this.sensitivity]!!
            }
            this.sensitivitySamples.clear()
        }
    }
    fun hasTooLowSensitivity(): Boolean {
        return finalSensitivity > -5 && finalSensitivity < 40
    }

    @Suppress("UNCHECKED_CAST")
    private fun processCinematic() {
        val now = System.currentTimeMillis()

        val differenceYaw = abs(deltaYaw - lastDeltaYaw).toDouble()
        val differencePitch = abs(deltaPitch - lastDeltaPitch).toDouble()

        val joltYaw = abs(differenceYaw - deltaYaw)
        val joltPitch = abs(differencePitch - deltaPitch)

        val cinematic = (now - lastHighRate > 250L) || (now - lastSmooth < 9000L)

        if (joltYaw > 1.0 && joltPitch > 1.0) {
            lastHighRate = now
        }

        yawSamples.add(deltaYaw.toDouble())
        pitchSamples.add(deltaPitch.toDouble())

        if (yawSamples.size >= 20 && pitchSamples.size >= 20) {
            val shannonYaw: MutableSet<Double?> = HashSet<Double?>()
            val shannonPitch: MutableSet<Double?> = HashSet<Double?>()
            val stackYaw: MutableList<Double?> = ArrayList<Double?>()
            val stackPitch: MutableList<Double?> = ArrayList<Double?>()

            for (yawSample in yawSamples) {
                stackYaw.add(yawSample)
                if (stackYaw.size >= 10) {
                    shannonYaw.add(MathUtil.getSE(stackYaw))
                    stackYaw.clear()
                }
            }

            for (pitchSample in pitchSamples) {
                stackPitch.add(pitchSample)
                if (stackPitch.size >= 10) {
                    shannonPitch.add(MathUtil.getSE(stackPitch))
                    stackPitch.clear()
                }
            }

            if (shannonYaw.size != 1 || shannonPitch.size != 1 || (shannonYaw.toTypedArray()[0] != shannonPitch.toTypedArray()[0])) {
                isTotallyNotCinematic = 20
            }

            val resultsYaw = GraphUtil.getGraph(yawSamples as List<Double>)
            val resultsPitch = GraphUtil.getGraph(pitchSamples as List<Double>)

            val negativesYaw = resultsYaw.negatives
            val negativesPitch = resultsPitch.negatives
            val positivesYaw = resultsYaw.positives
            val positivesPitch = resultsPitch.positives

            if (positivesYaw > negativesYaw || positivesPitch > negativesPitch) {
                lastSmooth = now
            }

            yawSamples.clear()
            pitchSamples.clear()
        }

        if (isTotallyNotCinematic > 0) {
            isTotallyNotCinematic--
            cinematicRotation = false
        } else {
            cinematicRotation = cinematic
        }
    }

    override fun hasValidSensitivity(): Boolean {
        return this.sensitivity in 1..<200
    }

    override fun isCinematicRotation(): Boolean {
        return cinematicRotation
    }

    override fun hasValidSensitivityNormalaized(): Boolean {
        return this.sensitivity in 1..<269
    }

    override fun getYaw(): Float {
        return this.yaw
    }

    override fun getPitch(): Float {
        return this.pitch
    }

    override fun getLastYaw(): Float {
        return this.lastYaw
    }

    override fun getLastPitch(): Float {
        return this.lastPitch
    }

    override fun getDeltaYaw(): Float {
        return this.deltaYaw
    }

    override fun getDeltaPitch(): Float {
        return this.deltaPitch
    }

    override fun getLastDeltaYaw(): Float {
        return this.lastDeltaYaw
    }

    override fun getLastDeltaPitch(): Float {
        return this.lastDeltaPitch
    }

    override fun getYawAccel(): Float {
        return this.yawAccel
    }

    override fun getPitchAccel(): Float {
        return this.pitchAccel
    }

    override fun getLastYawAccel(): Float {
        return this.lastYawAccel
    }

    override fun getLastPitchAccel(): Float {
        return this.lastPitchAccel
    }

    override fun getRawMouseDeltaX(): Float {
        return this.rawMouseDeltaX
    }

    override fun getRawMouseDeltaY(): Float {
        return this.rawMouseDeltaY
    }

    override fun getFuckedPredictedPitch(): Float {
        return this.fuckedPredictedPitch
    }

    override fun getFuckedPredictedYaw(): Float {
        return this.fuckedPredictedYaw
    }

    override fun getLastFuckedPredictedPitch(): Float {
        return this.lastFuckedPredictedPitch
    }

    override fun getLastFuckedPredictedYaw(): Float {
        return this.lastFuckedPredictedYaw
    }

    override fun isCinematic(): Boolean {
        return this.cinematic
    }

    override fun getFinalSensitivity(): Double {
        return this.finalSensitivity
    }

    override fun getMcpSensitivity(): Double {
        return this.mcpSensitivity
    }

    override fun getSensitivitySamples(): ArrayDeque<Int?> {
        return this.sensitivitySamples
    }

    override fun getSensitivity(): Int {
        return this.sensitivity
    }

    override fun getMouseDeltaX(): Int {
        return this.mouseDeltaX
    }

    override fun getMouseDeltaY(): Int {
        return this.mouseDeltaY
    }

    override fun getSmoothnessYaw(): Float {
        return smoothnessYaw
    }

    override fun getSmoothnessPitch(): Float {
        return smoothnessPitch
    }

    override fun getConsistency(): Float {
        return consistency
    }

    override fun setCinematic(cinematic: Boolean) {
        this.cinematic = cinematic
    }
}