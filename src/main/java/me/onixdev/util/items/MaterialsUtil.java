package me.onixdev.util.items;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class MaterialsUtil {
    private final Material chorus = Material.getMaterial("CHORUS_FRUIT");
    private final Material shield = Material.getMaterial("SHIELD");
    private final Material potion = Material.getMaterial("POTION");
    public static boolean isUsable(ItemStack stack,int foodLevel) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        if (stack.getType() == Material.GOLDEN_APPLE || stack.getType() == chorus || stack.getType() == potion) return true;
        if (stack.getType() == shield) return true;
        if (stack.getType().isEdible() && foodLevel < 20) return true;
        return false;
    }
}
