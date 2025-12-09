package me.onixdev.check.impl.player.inventory;

import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import dev.onixac.api.events.api.BaseEvent;
import me.onixdev.event.impl.PlayerClickEvent;
import me.onixdev.user.OnixUser;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class InventoryA extends Check {
    private int lastSlot = -1;
    private ItemStack lastItem;
    private long lastClick;
    public InventoryA(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("Inventory").setType("A").setCheckStage(CheckStage.EXPERIMENTAL).setDescription("imposibleDeltaValue").build());
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof PlayerClickEvent) {
            PlayerClickEvent clickEvent = (PlayerClickEvent) event;
            final long time = System.currentTimeMillis() - lastClick;
            ItemStack stack = ((PlayerClickEvent) event).getCurrent();
            int slot = ((PlayerClickEvent) event).getRawSlot();
            if ((clickEvent.getSlot_type() == InventoryType.SlotType.CONTAINER || clickEvent.getSlot_type() == InventoryType.SlotType.ARMOR || clickEvent.getSlot_type() == InventoryType.SlotType.QUICKBAR)) {

                if (lastItem != null && lastItem.getType() == stack.getType()) {
                    return;
                }
                ItemStack parsedItem = player.getBukkitPlayer().getInventory().getItem(slot);
                if (time <= 1) {
                    String type = stack == null ? parsedItem != null ? parsedItem.getType().name() : "none" : stack.getType().name();
                    fail("item: " + type + ", slot: " + slot + " time: " + time);
                    if (shouldCancel()) event.cancel();
                }

                lastSlot = slot;
                lastItem = stack;
                lastClick = System.currentTimeMillis();
            }
        }
    }


    public int[] translatePosition(final int slot) {
        final int row = slot / 9 + 1;
        final int rowPosition = slot - (row - 1) * 9;
        return new int[] { row, rowPosition };
    }

    public double distanceBetween(final int slot1, final int slot2) {
        final int[] slot1XZ = this.translatePosition(slot1);
        final int[] slot2XZ = this.translatePosition(slot2);
        return Math.sqrt((slot1XZ[0] - slot2XZ[0]) * (slot1XZ[0] - slot2XZ[0]) + (slot1XZ[1] - slot2XZ[1]) * (slot1XZ[1] - slot2XZ[1]));

    }
}
