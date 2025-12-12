package me.onixdev.check.impl.combat.aura;

import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import dev.onixac.api.events.api.BaseEvent;
import me.onixdev.event.impl.PlayerClickEvent;
import me.onixdev.event.impl.PlayerUseEntityEvent;
import me.onixdev.event.impl.TickEvent;
import me.onixdev.user.OnixUser;

public class AuraA extends Check {
    private boolean send;
    private long last;
    public AuraA(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("Aura").setType("A").setDescription("PostCheck").setCheckStage(CheckStage.RELEASE).build());
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof PlayerUseEntityEvent) {
            send = true;
        }
        if (event instanceof TickEvent && ((TickEvent) event).getTickType() == TickEvent.Target.FLYING) {
            double delta = System.currentTimeMillis() - last;
            if (send) {
                if (delta > 40 && delta < 120) {
                    fail("d: " + delta);
                }
                send = false;
            }
            last = System.currentTimeMillis();
        }
    }
}
