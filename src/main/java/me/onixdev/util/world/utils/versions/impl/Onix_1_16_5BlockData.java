package me.onixdev.util.world.utils.versions.impl;

import me.onixdev.util.items.MaterialsUtil;
import me.onixdev.util.net.BukkitNms;
import me.onixdev.util.world.utils.versions.VersionCFR;
import me.onixdev.util.world.utils.versions.iBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Onix_1_16_5BlockData implements VersionCFR {

        private iBlockData blockData;

        public Onix_1_16_5BlockData() {
            this.loadBlockData();
        }

        private void loadBlockData() {
            this.blockData = new iBlockData() {
                @Override
                public boolean isStepable(final Block b) {
                    if (MaterialsUtil.isGate(b.getType()) && BukkitNms.getDataFromBlock(b) < 4) {
                        return true;
                    }
                    switch (b.getType()) {

                        case HOPPER:
                        case CAULDRON:
                        case DAYLIGHT_DETECTOR:

                        case IRON_TRAPDOOR:

                        case BREWING_STAND:
                        case COCOA:
                        case CAKE:

                        case SNOW:

                        case ACACIA_STAIRS:
                        case SANDSTONE_STAIRS:

                        case BRICK_STAIRS:
                        case COBBLESTONE_STAIRS:
                        case DARK_OAK_STAIRS:

                        case NETHER_BRICK_STAIRS:
                        case QUARTZ_STAIRS:
                        case RED_SANDSTONE_STAIRS:

                        case ACACIA_FENCE:
                        case BIRCH_FENCE:
                        case DARK_OAK_FENCE:

                        case JUNGLE_FENCE:

                        case SPRUCE_FENCE:
                        case CHEST:
                        case TRAPPED_CHEST:
                        case ENDER_CHEST:

                        case FLOWER_POT: {
                            return true;
                        }
                        default: {
                            final String name = b.getType().name();
                            if (name.contains("slab")) {
                                return true;
                            }
                            if (name.contains("bed")) {
                                return true;
                            }
                            switch (name) {
                                case "PURPUR_STAIRS":
                                case "PURPUR_SLAB":
                                case "PURPUR_PILLAR":
                                case "CHORUS_PLANT": {
                                    return true;
                                }
                                default: {
                                    return false;
                                }
                            }
                        }
                    }
                }

                @Override
                public boolean isClimbable(final Block b) {
                    return b.getType().equals((Object) Material.LADDER) || b.getType().equals((Object)Material.VINE);
                }

                public boolean isInVoidHitbox(final Block b, final Location locationTo) {
                    return this.doesAffectMovement(b);
                }

                @Override
                public boolean doesAffectMovement(final Block b) {
                    if (b.isLiquid()) {
                        return true;
                    }
                    if (MaterialsUtil.isGate(b.getType())) {
                        return false;
                    }
                    switch (b.getType()) {

                        case BROWN_MUSHROOM:

                        case ACTIVATOR_RAIL:
                        case DETECTOR_RAIL:
                        case POWERED_RAIL:
                        case AIR:
                        case RED_MUSHROOM:

                        case TORCH:
                        case STONE_BUTTON:

                        case FIRE:

                        case MELON_SEEDS:

                        case TRIPWIRE:
                        case TRIPWIRE_HOOK:
                        case PUMPKIN_SEEDS:
                        case DEAD_BUSH:

                        case CARROT:

                        case WHEAT: {
                            return false;
                        }
                        default: {
                            return true;
                        }
                    }
                }

                @Override
                public boolean isPassable(final Block b) {
                    if (b.isLiquid()) {
                        return true;
                    }
                    switch (b.getType()) {

                        case HOPPER:
                        case CAULDRON:
                        case DAYLIGHT_DETECTOR:

                        case IRON_TRAPDOOR:

                        case BREWING_STAND:
                        case COCOA:
                        case SNOW:

                        case ACACIA_STAIRS:
                        case SANDSTONE_STAIRS:

                        case BRICK_STAIRS:
                        case COBBLESTONE_STAIRS:
                        case DARK_OAK_STAIRS:

                        case NETHER_BRICK_STAIRS:
                        case QUARTZ_STAIRS:
                        case RED_SANDSTONE_STAIRS:

                        case ACACIA_FENCE:
                        case BIRCH_FENCE:
                        case DARK_OAK_FENCE:

                        case JUNGLE_FENCE:

                        case SPRUCE_FENCE:
                        case CHEST:
                        case TRAPPED_CHEST:
                        case ENDER_CHEST:
                        case FLOWER_POT:

                        case BROWN_MUSHROOM:

                        case ACTIVATOR_RAIL:
                        case DETECTOR_RAIL:
                        case POWERED_RAIL:
                        case AIR:
                        case RED_MUSHROOM:

                        case TORCH:
                        case STONE_BUTTON:

                        case FIRE:

                        case MELON_SEEDS:

                        case TRIPWIRE:
                        case TRIPWIRE_HOOK:
                        case PUMPKIN_SEEDS:
                        case DEAD_BUSH:

                        case CARROT:

                        case WHEAT:
                        case POTATO:
                        case SAND:
                        case GRAVEL:
                        case TNT:
                        case SOUL_SAND:
                        case SLIME_BLOCK:

                        case MELON_STEM:
                        case PUMPKIN_STEM:
                        case ACACIA_DOOR:

                        case LEVER:
                        case IRON_DOOR:
                        case JUNGLE_DOOR:
                        case SPRUCE_DOOR:

                        case REDSTONE_ORE:

                        case STRING:

                        case REDSTONE_WIRE:

                        case ARMOR_STAND:
                        case BIRCH_DOOR:

                        case DARK_OAK_DOOR:
                        case DARK_OAK_FENCE_GATE:
                        case DRAGON_EGG:
                        case BLAZE_POWDER:
                        case CACTUS:

                        case GLOWSTONE_DUST:
                        case LADDER:
                        case ANVIL:

                        case VINE:

                        case REDSTONE:
                        case ACACIA_FENCE_GATE:
                        case BIRCH_FENCE_GATE:
                        case JUNGLE_FENCE_GATE:
                        case SPRUCE_FENCE_GATE: {
                            return true;
                        }
                        default: {
                            final String name = b.getType().name();
                            switch (name) {
                                case "PURPUR_STAIRS":
                                case "PURPUR_SLAB":
                                case "PURPUR_PILLAR":
                                case "CHORUS_PLANT":
                                case "GRASS_PATH": {
                                    return true;
                                }
                                default: {
                                    return false;
                                }
                            }
                        }
                    }
                }

                @Override
                public boolean isLiquid(final Block b) {
                    return b.isLiquid();
                }

                @Override
                public boolean isFrozen(final Block b) {
                    return MaterialsUtil.isIce(b.getType());
                }

                public boolean hasYOffset(final Block b) {
                    return this.getYOffset(b) != 0.0625;
                }

                public double getSnowLayerOffset(final Block b) {
                    return b.getType().equals((Object)Material.SNOW) ? ((BukkitNms.getDataFromBlock(b) - 1) * 0.125) : 0.0;
                }

                @Override
                public double getYOffset(final Block b) {
                    if (b.getType().equals((Object)Material.SNOW)) {
                        return this.getSnowLayerOffset(b);
                    }
                    final String name = b.getType().name();
                    if (name.contains("bed")) {
                        return 0.6;
                    }
                    switch (b.getType()) {
                        case CAKE:

                        case ACACIA_FENCE:
                        case BIRCH_FENCE:
                        case DARK_OAK_FENCE:

                        case JUNGLE_FENCE:

                        case SPRUCE_FENCE:

                        case DARK_OAK_FENCE_GATE:

                        case ACACIA_FENCE_GATE:
                        case BIRCH_FENCE_GATE:
                        case JUNGLE_FENCE_GATE:
                        case SPRUCE_FENCE_GATE: {
                            return 0.62;
                        }
                        case SOUL_SAND: {
                            return 0.25;
                        }

                        default: {
                            return 0.0625;
                        }
                    }
                }
            };
        }

        @Override
        public iBlockData getBlockData() {
            return this.blockData;
        }

        @Override
        public String getName() {
            return "v1_16_R1";
        }

        @Override
        public boolean isAllowedToFly(final Object player) {
            return false;
        }

        @Override
        public boolean isAllowedToPassBlocks(final Object player) {
            return false;
        }
}
