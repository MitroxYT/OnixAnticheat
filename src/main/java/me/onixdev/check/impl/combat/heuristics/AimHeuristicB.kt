package me.onixdev.check.impl.combat.heuristics

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying
import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import me.onixdev.check.api.Check
import me.onixdev.user.OnixUser
import me.onixdev.util.math.RotList
import me.onixdev.util.net.PacketUtil.isTickPacketLegacy
import kotlin.math.abs
import kotlin.math.max

@Suppress("DEPRECATED_IDENTITY_EQUALS")
@CheckInfo(name = "AimHeuristic", type = "B", stage = CheckStage.EXPERIMENTAL, maxBuffer = 5.0, decayBuffer = 1.0)
class AimHeuristicB(player: OnixUser?) : Check(player) {
    private val yawDeltaBuffer = RotList(20)
    private val pitchDeltaBuffer = RotList(20)
    private var rotationCheckTimer = 0
    private var currentYaw = 0f
    private var accelerationCounter = 0
    private var previousYawDelta = 0f
    private var previousPitch = 0f
    private var currentPitch = 0f
    override fun onPacketIn(event: PacketReceiveEvent) {
        if (isTickPacketLegacy(event.packetType)) {
            if (this.rotationCheckTimer > 0) {
                --this.rotationCheckTimer
            }
        }

        if (WrapperPlayClientPlayerFlying.isFlying(event.packetType)) {
            val flying = WrapperPlayClientPlayerFlying(event)

            if (flying.hasRotationChanged()) {
                checkRotation(flying.location.yaw, flying.location.pitch)
            }
        }

        if (event.packetType === PacketType.Play.Client.INTERACT_ENTITY) {
            val wrapperPlayClientInteractEntity = WrapperPlayClientInteractEntity(event)
            if (wrapperPlayClientInteractEntity.action == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                if (player.combatData.target != null && player.combatData.target!!
                        .entityId === wrapperPlayClientInteractEntity.entityId
                ) {
                    this.rotationCheckTimer = 15
                }
            }
        }
    }

    fun checkRotation(yaw: Float, pitch: Float) {
        val yawDelta = getDelta(yaw, this.currentYaw)
        val pitchDelta = getDelta(pitch, this.currentPitch)

        val deltaXZ: Double = player.movementContainer.deltaXZ
        val cps: Double = player.clickData.cps.toDouble()
        val absoluteYawDelta = abs(yawDelta - this.previousYawDelta)
        val absolutePitchDelta = abs(pitchDelta - this.previousPitch)
        this.yawDeltaBuffer.add(absoluteYawDelta)
        this.pitchDeltaBuffer.add(absolutePitchDelta)

        if (this.yawDeltaBuffer.isFull() && this.pitchDeltaBuffer.isFull() && this.rotationCheckTimer >= 5) {
            val suspiciousRotation: Boolean
            val yawDeltaDeviation = this.yawDeltaBuffer.getStandardDeviation()
            val pitchDeltaDeviation = this.pitchDeltaBuffer.getStandardDeviation()
            val isLowYaw = yawDelta < 1.5f
            suspiciousRotation = yawDeltaDeviation < 5.0f && pitchDeltaDeviation > 5.0f && !isLowYaw
            if (suspiciousRotation && deltaXZ > 0.05 && cps < 3) {
                ++this.accelerationCounter
                if (this.accelerationCounter > 8) {
                    fail(" acceleration [$accelerationCounter]")
                    if (this.accelerationCounter > 10) {
                        this.accelerationCounter = 10
                    }
                }
            } else {
                this.accelerationCounter = max(0, this.accelerationCounter - 1)
            }
        }


        this.currentYaw = yaw
        this.currentPitch = pitch
        this.previousYawDelta = yawDelta
        this.previousPitch = pitchDelta
    }

    companion object {
        fun getDelta(yaw: Float, currentYaw: Float): Float {
            return abs(yaw - currentYaw)
        }
    }
}