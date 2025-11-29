package me.onixdev.event.impl;

import lombok.Getter;
import me.onixdev.event.api.BaseEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerClickEvent extends BaseEvent {
    private final ClickType click;
    private final InventoryAction action;
    private InventoryType.SlotType slot_type;
    private int whichSlot;
    private int rawSlot;
    private ItemStack current;
    private int hotbarKey;
    public PlayerClickEvent(@NotNull InventoryType.@NotNull SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action) {
        this.current = null;
        this.hotbarKey = -1;
        this.slot_type = type;
        this.rawSlot = slot;
        this.click = click;
        this.action = action;
    }
}
