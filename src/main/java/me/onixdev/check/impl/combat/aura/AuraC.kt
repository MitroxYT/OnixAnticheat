package me.onixdev.check.impl.combat.aura

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.user.OnixUser

class AuraC(user: OnixUser) : Check(user, CheckBuilder().setCheckName("Aura").setType("C").setDescription("AttackPattern").build()) {
    private var movements = 0
    private var lastMovements = 0
    private var total = 0
    private var invalid = 0
    override fun onPacketIn(event: PacketReceiveEvent?) {
        if (event?.packetType == PacketType.Play.Client.INTERACT_ENTITY) {
            val use = WrapperPlayClientInteractEntity(event)
            if (use.action == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
               val valid = player.clickData.cps > 7.2 && this.movements < 4 && this.lastMovements < 4;
                if (valid) {
                    val flag = this.movements == this.lastMovements;
                    if (flag) {
                        ++this.invalid;
                    }
                    if (++this.total == 30) {
                        if (this.invalid > 28) {
                            this.fail("invalid=" + this.invalid);
                        }
                        this.invalid = 0;
                        this.total = 0;
                    }
                }
                this.lastMovements = this.movements;
                this.movements = 0;
            }
        }
        if (WrapperPlayClientPlayerFlying.isFlying(event?.packetType)) {
            ++movements
        }
    }
}