package me.onixdev.check.impl.combat.aura;

import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.event.api.BaseEvent;
import me.onixdev.event.impl.PlayerClickEvent;
import me.onixdev.user.OnixUser;

public class AuraA extends Check {
    public AuraA(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("Aura").setType("A").setDescription("PostCheck").setCheckStage(CheckStage.RELEASE).build());
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof PlayerClickEvent) {
            PlayerClickEvent clickEvent = (PlayerClickEvent) event;
            if (fail("click: " + clickEvent.getRawSlot() + " type: " + clickEvent.getClick() + " sd" + clickEvent.getSlot_type())) {

            }

        }
    }
}
