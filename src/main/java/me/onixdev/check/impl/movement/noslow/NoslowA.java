package me.onixdev.check.impl.movement.noslow;

import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.event.api.BaseEvent;
import me.onixdev.event.impl.TickEvent;
import me.onixdev.user.OnixUser;

public class NoslowA extends Check {
    public NoslowA(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("Noslow").setType("A").build());
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof TickEvent) {
            player.sendMessage("using: " + player.isUsingItem() + " hand: " + player.getUsingHand() + " nms: " + player.isUsingBukkitItem());
        }
    }
}
