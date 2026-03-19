package me.onixdev.check.impl.player.airstuck

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play
import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import me.onixdev.check.api.Check
import me.onixdev.user.OnixUser
import me.onixdev.util.net.PacketUtil

@CheckInfo(name = "AirStuck", type = "A", stage = CheckStage.EXPERIMENTAL, maxBuffer = 5.0, decayBuffer = 1.0)
class AirStuckA(player: OnixUser) : Check(player) {
    private var positions = 0
    private var clock = 0L
    private var lastTransTime: Long = 0
    private var oldTransId = 0

    override fun onPacketIn(event: PacketReceiveEvent) {
        if (PacketUtil.isTransaction(event.packetType)) {
            val ms: Long = (player.connectionContainer.playerClockAtLeast - this.clock) / 1000000L
            val diff = System.currentTimeMillis() - this.lastTransTime
            val exempt = player.inVehicle() || player.isDead || player.isSpectator
            if (diff > 2000L && ms > 2000L) {
                if (this.positions == 0 && (this.clock != 0L) && !exempt) {
                    fail("player no Response Since $ms ms")
                }
                this.positions = 0
                this.clock = player.connectionContainer.playerClockAtLeast
                this.lastTransTime = System.currentTimeMillis()
                this.oldTransId = player.connectionContainer.lastTransactionSent.get()
            }
        }

        if ((event.packetType === Play.Client.PLAYER_POSITION_AND_ROTATION || event.packetType === Play.Client.PLAYER_POSITION)) {
            ++this.positions
        } else if ((event.packetType === Play.Client.STEER_VEHICLE || event.packetType === Play.Client.VEHICLE_MOVE)) {
            ++this.positions
        }
    }
}