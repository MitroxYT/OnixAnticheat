package me.onixdev.check.impl.player.badpackets;

import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.user.OnixUser;

public class BadPacketB extends Check {
    public BadPacketB(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("BadPacket").setType("B").build());
    }

    public boolean validdate() {
        if (player.isUsingBukkitItem())  {
            return false;
        }
        return true;
    }
}
