package me.onixdev.event.impl;

import lombok.Getter;
import dev.onixac.api.events.api.BaseEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerClickEvent extends BaseEvent {
    private final ClickType click;
    private final InventoryAction action;
    private final InventoryType.SlotType slot_type;
    private final int rawSlot;
    private final ItemStack current;
    private final int hotbarKey;
    public final boolean isPlayer;
    public PlayerClickEvent(@NotNull InventoryType.@NotNull SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action,ItemStack current,boolean player) {
        this.current = current;
        this.hotbarKey = -1;
        this.slot_type = type;
        this.rawSlot = slot;
        this.click = click;
        this.action = action;
        this.isPlayer = player;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public ItemStack getCurrent() {
        return current;
    }

    public int getRawSlot() {
        return rawSlot;
    }

    public InventoryType.SlotType getSlot_type() {
        return slot_type;
    }
}
