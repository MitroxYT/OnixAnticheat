package me.onixdev.util.items;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.FoodProperties;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemConsumable;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import lombok.experimental.UtilityClass;
import me.onixdev.user.OnixUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@UtilityClass
@SuppressWarnings("all")
public class MaterialsUtil {
    private final Material chorus = Material.getMaterial("CHORUS_FRUIT");
    private final Material shield = Material.getMaterial("SHIELD");
    private final Material potion = Material.getMaterial("POTION");
    public static boolean isUsable(ItemStack item, int foodLevel, OnixUser player) {
        if (item == null || item.getType() == ItemTypes.AIR) return false;
        if (item.getType() == ItemTypes.SPLASH_POTION)
            return false;
        if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9) && item.getLegacyData() > 16384) {
            return false;
        }
        final ItemConsumable consumable = item.getComponentOr(ComponentTypes.CONSUMABLE, null);
        final FoodProperties foodComponent = item.getComponentOr(ComponentTypes.FOOD, null);
        if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_2) && consumable != null && foodComponent == null) {
            return true;
        }
        ItemType material = item.getType();
        if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5) && foodComponent != null) {
            return foodComponent.isCanAlwaysEat() || player.food < 20;
        }
        if (material == ItemTypes.POTION || material == ItemTypes.MILK_BUCKET
                || material == ItemTypes.GOLDEN_APPLE || material == ItemTypes.ENCHANTED_GOLDEN_APPLE
                || material == ItemTypes.HONEY_BOTTLE || material == ItemTypes.SUSPICIOUS_STEW ||
                material == ItemTypes.CHORUS_FRUIT) {
            return true;
        }
        final NBTCompound nbt = item.getNBT();
        if (material == ItemTypes.CROSSBOW && nbt != null && nbt.getBoolean("Charged")) {
            return false;
        }
        if (material.hasAttribute(ItemTypes.ItemAttribute.SWORD)) {
            // Спасибо mojang за то что вернули возможность блока мечом )))))))))))))))
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21_11)) {
                if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_11) && item.getComponentOr(ComponentTypes.BLOCKS_ATTACKS,null) != null) {
                    return true;
                }
                else {
                    return false;
                }
            }
            return player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8);
        }
        if (item.getType() == ItemTypes.SHIELD && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) return true;
        return false;
    }
    public static synchronized boolean isValidFood(final Material m, final Player p) {
        return m.equals((Object)Material.MILK_BUCKET) || m.equals((Object)Material.POTION) || m.equals((Object)Material.GOLDEN_APPLE) || (m.isEdible() && p.getFoodLevel() < 20);
    }

    public static synchronized boolean isLeaves(final Material material) {
        switch (material) {
            default: {
                return false;
            }

        }
    }

    public static synchronized boolean isGlas(final Material material) {
        switch (material) {
            case GLASS:
            {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public static synchronized boolean isIce(final Material material) {
        switch (material) {
            case ICE:
            case PACKED_ICE: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public static synchronized boolean isChest(final Material material) {
        switch (material) {
            case CHEST:
            case ENDER_CHEST:
            case TRAPPED_CHEST: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public static synchronized boolean isDoor(final Material material) {
        switch (material) {
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case IRON_DOOR:
            case JUNGLE_DOOR:
            case SPRUCE_DOOR:
            {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public static synchronized boolean isGate(final Material material) {
        switch (material) {
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:

            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
