package me.onixdev.check.impl.player.inventory;

import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import dev.onixac.api.events.api.BaseEvent;
import me.onixdev.event.impl.PlayerClickEvent;
import me.onixdev.user.OnixUser;

public class InventoryC extends Check {
    public InventoryC(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("Inventory").setType("C").setCheckStage(CheckStage.BETA).build());
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof PlayerClickEvent) {
            if (player.lastTeleportTime < 5) return;
            if (player.theoreticalInput.hasInput()) {
                StringBuilder verbose = new StringBuilder();
                if (player.theoreticalInput.isForward()) {
                    verbose.append("W");
                    verbose.append(" ");
                }
                if (player.theoreticalInput.isBackward()) {
                    verbose.append("S");
                    verbose.append(" ");
                }
                if (player.theoreticalInput.isLeft()) {
                    verbose.append("A");
                    verbose.append(" ");
                }
                if (player.theoreticalInput.isRight()) {
                    verbose.append("D");
                    verbose.append(" ");
                }
                failAndSetback(" pressed key: "+ verbose.toString());
                if (shouldCancel()) event.cancel();
            }
        }
    }
}
