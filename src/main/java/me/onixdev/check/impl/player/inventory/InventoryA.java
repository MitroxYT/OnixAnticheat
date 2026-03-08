package me.onixdev.check.impl.player.inventory;

import dev.onixac.api.check.CheckInfo;
import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import dev.onixac.api.events.api.BaseEvent;
import me.onixdev.event.impl.PlayerClickEvent;
import me.onixdev.user.OnixUser;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

@CheckInfo(name = "Inventory", type = "A", stage = CheckStage.RELEASE, maxBuffer = 5.0, decayBuffer = 1.0)
public class InventoryA extends Check {
    private int lastSlot = -1;
    private ItemStack lastItem;
    private long lastClick;
    public InventoryA(OnixUser player) {
        super(player);
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
                var click = clickEvent.getClick();
                if (click == ClickType.CREATIVE) return;
                var action = clickEvent.getAction();
                var inv = clickEvent.getSlot_type();
                if (time <= 1) {
                    String type = stack == null ? parsedItem != null ? parsedItem.getType().name() : "none" : stack.getType().name();
                    fail("item: " + type + ", slot: " + slot + " time: " + time + " cT: " + click + " aT: " + action+ " sT: " + inv);
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
