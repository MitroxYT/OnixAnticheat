package me.onixdev.check.impl.player.inventory;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow;
import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import dev.onixac.api.events.api.BaseEvent;
import me.onixdev.event.impl.*;
import me.onixdev.user.OnixUser;

public class InventoryB extends Check {
    private boolean receivedClick = false;
    private long lastClickTime = 0;
    private double buffer;
    private boolean clicked,picked = false;
    public InventoryB(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("Inventory").setType("B").setDescription("send close packet in one tick click packet").setCheckStage(CheckStage.EXPERIMENTAL).build());
    }
    @Override
    public void onPacketIn(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            receivedClick = true;
            lastClickTime = System.currentTimeMillis();
        }

        if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            WrapperPlayClientCloseWindow wrapper = new WrapperPlayClientCloseWindow(event);

            if (wrapper.getWindowId() == 0 && receivedClick) {
                long diff = System.currentTimeMillis() - lastClickTime;
                boolean isMoving = player.theoreticalInput.hasInput();

                if (player.inVehicle()) {
                    return;
                }

                if (diff <= 10 && isMoving) {
                    fail("diff: " + diff);
                }

                receivedClick = false;
            }
        }
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
            if (((PlayerCloseInventoryEvent) event).isClient() && !player.antiFalsePositivesHandler.tryingFalseInvChecks()) {
                if (picked) {
                    fail("pick");
                }
                clicked = false;
                picked = false;
            }
        }
        if (event instanceof TickEvent) {
            if (((TickEvent) event).getTickType() == TickEvent.Target.FLYING || ((TickEvent) event).getTickType() == TickEvent.Target.TRANSACTION) clicked = picked = false;
        }
    }
}
