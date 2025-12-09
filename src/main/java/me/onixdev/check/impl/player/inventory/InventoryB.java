package me.onixdev.check.impl.player.inventory;

import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import dev.onixac.api.events.api.BaseEvent;
import me.onixdev.event.impl.*;
import me.onixdev.user.OnixUser;

public class InventoryB extends Check {
    private boolean clicked,picked = false;
    public InventoryB(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("Inventory").setType("B").setDescription("send close packet in one tick click packet").setCheckStage(CheckStage.EXPERIMENTAL).build());
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof PlayerClickEvent) {
            PlayerClickEvent clickEvent = (PlayerClickEvent) event;
            if (((PlayerClickEvent) event).isPlayer()) {
                clicked = true;
            }
        }
        if (event instanceof PlayerPickEvent) {
            picked = true;
        }
        if (event instanceof PlayerCloseInventoryEvent) {
            if (((PlayerCloseInventoryEvent) event).isClient()) {
                if (clicked || picked) {
                    fail("c: " + clicked + " p: " + picked);
                    clicked = false;
                    picked = false;
                }
            }
        }
        if (event instanceof TickEvent) {
            if (((TickEvent) event).getTickType() == TickEvent.Target.FLYING || ((TickEvent) event).getTickType() == TickEvent.Target.TRANSACTION) clicked = picked = false;
        }
    }
}
