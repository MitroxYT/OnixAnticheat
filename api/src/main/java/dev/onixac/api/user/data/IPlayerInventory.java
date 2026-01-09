package dev.onixac.api.user.data;

import org.bukkit.inventory.ItemStack;

public interface IPlayerInventory {
    ItemStack getItemInOffHand();
    ItemStack getItemInMainHand();
    ItemStack getItemInSlot(int index);
    void swapSlot();
}
