package me.onixdev.util.items;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class MaterialsUtil {
    private final Material chorus = Material.getMaterial("CHORUS_FRUIT");
    private final Material shield = Material.getMaterial("SHIELD");
    public boolean isUsable(ItemStack stack,double foodLevel) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        if (stack.getType() == Material.GOLDEN_APPLE || stack.getType() == chorus) return true;
        if (stack.getType() == shield) return true;
        return false;
    }
}
