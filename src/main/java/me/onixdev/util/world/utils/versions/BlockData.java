package me.onixdev.util.world.utils.versions;

import me.onixdev.util.net.BukkitNms;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockData {
    public static boolean isStepable(final Block b, final Location locationTo) {
        return BlockVersionManager.getCurrentCFR().getBlockData().isStepable(b) && (isSlabLikeBlock(b) ? isPassable(b.getRelative(BlockFace.UP)) : (!BlockVersionManager.getCurrentCFR().getBlockData().isStepable(b.getRelative(BlockFace.UP))));
    }

    public static boolean isClimbable(final Block b) {
        return BlockVersionManager.getCurrentCFR().getBlockData().isClimbable(b);
    }

    public static boolean isInVoidHitbox(final Block b, final Location locationTo) {
        return doesAffectMovement(b);
    }

    public static boolean isWoopableBlock(final Block b) {
        final String name = b.getType().name();
        switch (name) {
            case "SOUL_SAND":
            case "GRASS_PATH": {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public static boolean doesAffectMovement(final Block b) {
        return (!BlockVersionManager.getCurrentCFR().getBlockData().isStepable(b) || !BlockVersionManager.getCurrentCFR().getBlockData().isStepable(b.getRelative(BlockFace.UP)) || isSlabLikeBlock(b)) && doesAffectMovementSimple(b);
    }

    public static boolean doesAffectMovementSimple(final Block b) {
        return BlockVersionManager.getCurrentCFR().getBlockData().doesAffectMovement(b);
    }

    public static boolean isPassable(final Block b) {
        return BlockVersionManager.getCurrentCFR().getBlockData().isPassable(b);
    }

    public static boolean isLiquid(final Block b) {
        return BlockVersionManager.getCurrentCFR().getBlockData().isLiquid(b);
    }

    public static boolean isSlabLikeBlock(final Block b) {
        final String typeName = b.getType().name();
        return (typeName.contains("SLAB") && !typeName.contains("DOUBLE")) || typeName.contains("STAIRS") || typeName.contains("STEP");
    }

    public static boolean isFrozen(final Block b) {
        return BlockVersionManager.getCurrentCFR().getBlockData().isFrozen(b);
    }

    public static boolean hasYOffset(final Block b) {
        return getYOffset(b) != 0.0625;
    }

    private static double getSnowLayerOffset(final Block b) {
        return b.getType().equals((Object) Material.SNOW) ? ((BukkitNms.getDataFromBlock(b) - 1) * 0.125) : 0.0;
    }

    public static double getYOffset(final Block b) {
        return BlockVersionManager.getCurrentCFR().getBlockData().getYOffset(b);
    }
}
