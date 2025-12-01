package me.onixdev.check.impl.player.scaffold;

import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.event.api.BaseEvent;
import me.onixdev.event.impl.PlayerRotationEvent;
import me.onixdev.user.OnixUser;

public class ScaffoldA extends Check {
    public ScaffoldA(OnixUser player) {
        super(player,CheckBuilder.create().setCheckName("Scaffold").setType("A").setCheckStage(CheckStage.RELEASE).build());
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof PlayerRotationEvent) {
            if (!((PlayerRotationEvent) event).isPost()) {
                if (!player.getBrigingContainer().isBrige()) return;
                double dx = ((PlayerRotationEvent) event).getDeltaYaw();
                player.sendMessage("dx: " + dx + " enb: " + isEnabled());
                if (dx > 100) {
                    fail("delta: " + dx);
                }
            }
        }
    }
}
