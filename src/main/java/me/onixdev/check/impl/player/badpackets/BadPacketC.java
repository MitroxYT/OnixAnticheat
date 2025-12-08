package me.onixdev.check.impl.player.badpackets;

import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.user.OnixUser;

public class BadPacketC extends Check {
    public BadPacketC(OnixUser player, CheckBuilder builder) {
        super(player, builder);
    }
}
