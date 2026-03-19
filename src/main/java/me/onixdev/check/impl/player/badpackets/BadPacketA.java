package me.onixdev.check.impl.player.badpackets;

import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import dev.onixac.api.check.CheckInfo;
import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.user.OnixUser;

import java.util.Locale;

@CheckInfo(name = "BadPacket", type = "A", stage = CheckStage.RELEASE, maxBuffer = 5.0, decayBuffer = 1.0)
public class BadPacketA extends Check {
    public BadPacketA(OnixUser player) {
        super(player);
    }

    public boolean isValid(WrapperPlayClientPlayerDigging dig) {
        if (dig.getAction() == DiggingAction.START_DIGGING || dig.getAction() == DiggingAction.FINISHED_DIGGING || dig.getAction() == DiggingAction.CANCELLED_DIGGING)
            return true;
        if (dig.getBlockFaceId() != 0 || dig.getBlockPosition().getX() != 0 || dig.getBlockPosition().getY() != 0 || dig.getBlockPosition().getZ() != 0 || dig.getSequence() != 0) {
            fail(dig.getAction().toString().toLowerCase(Locale.ROOT));
            return false;
        }
        return true;
    }
}
