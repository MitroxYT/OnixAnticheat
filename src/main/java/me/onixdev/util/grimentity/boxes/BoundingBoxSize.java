package me.onixdev.util.grimentity.boxes;


import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.util.Vector3d;
import lombok.experimental.UtilityClass;
import me.onixdev.user.OnixUser;
import me.onixdev.util.grimentity.entity.PacketEntity;

@UtilityClass
public final class BoundingBoxSize {

    public static float getWidth(OnixUser player, PacketEntity packetEntity) {
        float width = getWidthMinusBaby(player, packetEntity);
        return width * (packetEntity.isBaby ? getBabyScaleFactor(packetEntity) : 1f);
    }

    private static float getWidthMinusBaby(OnixUser player, PacketEntity packetEntity) {
        final EntityType type = packetEntity.type;
        if (type == EntityTypes.AXOLOTL) {
            return 0.75f;
        } else if (type == EntityTypes.PANDA) {
            return 1.3f;
        } else if (type == EntityTypes.BAT || type == EntityTypes.PARROT || type == EntityTypes.COD || type == EntityTypes.EVOKER_FANGS || type == EntityTypes.TROPICAL_FISH || type == EntityTypes.FROG || type == EntityTypes.COPPER_GOLEM) {
            return 0.5f;
        } else if (type == EntityTypes.ARMADILLO || type == EntityTypes.BEE || type == EntityTypes.PUFFERFISH || type == EntityTypes.SALMON || type == EntityTypes.SNOW_GOLEM || type == EntityTypes.CAVE_SPIDER) {
            return 0.7f;
        } else if (type == EntityTypes.WITHER_SKELETON) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.7f : 0.72f;
        } else if (type == EntityTypes.WITHER_SKULL || type == EntityTypes.SHULKER_BULLET) {
            return 0.3125f;
        } else if (type == EntityTypes.HOGLIN || type == EntityTypes.ZOGLIN) {
            return 1.3964844f;
        } else if (type == EntityTypes.SKELETON_HORSE || type == EntityTypes.ZOMBIE_HORSE || type == EntityTypes.HORSE || type == EntityTypes.DONKEY || type == EntityTypes.MULE) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.3964844f : 1.4f;
        } else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.BOAT)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.375f : 1.5f;
        } else if (type == EntityTypes.HAPPY_GHAST) {
            return 4.0f;
        } else if (type == EntityTypes.CHICKEN || type == EntityTypes.ENDERMITE || type == EntityTypes.SILVERFISH || type == EntityTypes.VEX || type == EntityTypes.TADPOLE) {
            return 0.4f;
        } else if (type == EntityTypes.RABBIT) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.4f : 0.6f;
        } else if (type == EntityTypes.CREAKING || type == EntityTypes.STRIDER || type == EntityTypes.COW || type == EntityTypes.SHEEP || type == EntityTypes.MOOSHROOM || type == EntityTypes.PIG || type == EntityTypes.LLAMA || type == EntityTypes.DOLPHIN || type == EntityTypes.WITHER || type == EntityTypes.TRADER_LLAMA || type == EntityTypes.WARDEN || type == EntityTypes.GOAT) {
            return 0.9f;
        }   else if (type == EntityTypes.END_CRYSTAL) {
            return 2f;
        } else if (type == EntityTypes.ENDER_DRAGON) {
            return 16f;
        } else if (type == EntityTypes.FIREBALL) {
            return 1f;
        } else if (type == EntityTypes.GHAST) {
            return 4f;
        } else if (type == EntityTypes.GIANT) {
            return 3.6f;
        } else if (type == EntityTypes.GUARDIAN) {
            return 0.85f;
        } else if (type == EntityTypes.IRON_GOLEM) {
            return 1.4f;
        }  else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.MINECART_ABSTRACT)) {
            return 0.98f;
        } else if (type == EntityTypes.PLAYER || type == EntityTypes.MANNEQUIN) {
            return 0.6f;
        } else if (type == EntityTypes.POLAR_BEAR) {
            return 1.4f;
        } else if (type == EntityTypes.RAVAGER) {
            return 1.95f;
        } else if (type == EntityTypes.SHULKER) {
            return 1f;
        }  else if (type == EntityTypes.SMALL_FIREBALL) {
            return 0.3125f;
        } else if (type == EntityTypes.SPIDER) {
            return 1.4f;
        } else if (type == EntityTypes.SQUID) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.8f : 0.95f;
        } else if (type == EntityTypes.TURTLE) {
            return 1.2f;
        } else if (type == EntityTypes.ALLAY) {
            return 0.35f;
        } else if (type == EntityTypes.SNIFFER) {
            return 1.9f;
        } else if (type == EntityTypes.CAMEL) {
            return 1.7f;
        } else if (type == EntityTypes.WIND_CHARGE) {
            return 0.3125f;
        } else if (type == EntityTypes.ARMOR_STAND) {
            return 0.5F;
        } else if (type == EntityTypes.FALLING_BLOCK) {
            return 0.98F;
        } else if (type == EntityTypes.FIREWORK_ROCKET) {
            return 0.25F;
        }
        return 0.6f;
    }


    private static Vector3d yRot(float yaw, Vector3d start) {
        double cos = (float) Math.cos(yaw);
        double sin = (float) Math.sin(yaw);
        return new Vector3d(
                start.x * cos + start.z * sin,
                start.y,
                start.z * cos - start.x * sin
        );
    }

    public static float getHeight(OnixUser player, PacketEntity packetEntity) {
        float height = getHeightMinusBaby(player, packetEntity);
        return height * (packetEntity.isBaby ? getBabyScaleFactor(packetEntity) : 1f);
    }

    public static double getMyRidingOffset(PacketEntity packetEntity) {
        final EntityType type = packetEntity.type;
        if (type == EntityTypes.PIGLIN || type == EntityTypes.ZOMBIFIED_PIGLIN || type == EntityTypes.ZOMBIE) {
            return packetEntity.isBaby ? -0.05 : -0.45;
        } else if (type == EntityTypes.SKELETON) {
            return -0.6;
        } else if (type == EntityTypes.ENDERMITE || type == EntityTypes.SILVERFISH) {
            return 0.1;
        } else if (type == EntityTypes.EVOKER || type == EntityTypes.ILLUSIONER || type == EntityTypes.PILLAGER || type == EntityTypes.RAVAGER || type == EntityTypes.VINDICATOR || type == EntityTypes.WITCH) {
            return -0.45;
        } else if (type == EntityTypes.PLAYER || type == EntityTypes.MANNEQUIN) {
            return -0.35;
        }

        if (EntityTypes.isTypeInstanceOf(type, EntityTypes.ABSTRACT_ANIMAL)) {
            return 0.14;
        }

        return 0;
    }

    public static double getPassengerRidingOffset(OnixUser player, PacketEntity packetEntity) {
        final EntityType type = packetEntity.type;
        if (EntityTypes.isTypeInstanceOf(type, EntityTypes.MINECART_ABSTRACT)) {
            return 0;
        } else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.BOAT)) {
            return -0.1;
        } else if (type == EntityTypes.HAPPY_GHAST) {
            return 0.5;
        } else if (type == EntityTypes.HOGLIN || type == EntityTypes.ZOGLIN) {
            return getHeight(player, packetEntity) - (packetEntity.isBaby ? 0.2 : 0.15);
        } else if (type == EntityTypes.LLAMA) {
            return getHeight(player, packetEntity) * 0.67;
        } else if (type == EntityTypes.PIGLIN) {
            return getHeight(player, packetEntity) * 0.92;
        } else if (type == EntityTypes.RAVAGER) {
            return 2.1;
        } else if (type == EntityTypes.SKELETON) {
            return (getHeight(player, packetEntity) * 0.75) - 0.1875;
        } else if (type == EntityTypes.SPIDER) {
            return getHeight(player, packetEntity) * 0.5;
        } else if (type == EntityTypes.STRIDER) {// depends on animation position, good luck getting it exactly, this is the best you can do though
            return getHeight(player, packetEntity) - 0.19;
        }
        return getHeight(player, packetEntity) * 0.75;
    }

    private static float getHeightMinusBaby(OnixUser player, PacketEntity packetEntity) {
        final EntityType type = packetEntity.type;
        if (type == EntityTypes.ARMADILLO) {
            return 0.65f;
        } else if (type == EntityTypes.AXOLOTL) {
            return 0.42f;
        } else if (type == EntityTypes.BEE || type == EntityTypes.DOLPHIN || type == EntityTypes.ALLAY) {
            return 0.6f;
        } else if (type == EntityTypes.EVOKER_FANGS || type == EntityTypes.VEX) {
            return 0.8f;
        } else if (type == EntityTypes.SQUID) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.8f : 0.95f;
        } else if (type == EntityTypes.PARROT || type == EntityTypes.BAT || type == EntityTypes.PIG || type == EntityTypes.SPIDER) {
            return 0.9f;
        } else if (type == EntityTypes.WITHER_SKULL || type == EntityTypes.SHULKER_BULLET) {
            return 0.3125f;
        } else if (type == EntityTypes.BLAZE) {
            return 1.8f;
        } else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.BOAT)) {
            // WHY DOES VIAVERSION OFFSET BOATS? THIS MAKES IT HARD TO SUPPORT, EVEN IF WE INTERPOLATE RIGHT.
            // I gave up and just exempted boats from the reach check and gave up with interpolation for collisions
            return 0.5625f;
        } else if (type == EntityTypes.HAPPY_GHAST) {
            return 4.0f;
        } else if (type == EntityTypes.CAT) {
            return 0.7f;
        } else if (type == EntityTypes.CAVE_SPIDER) {
            return 0.5f;
        } else if (type == EntityTypes.FROG) {
            return 0.55f;
        } else if (type == EntityTypes.CHICKEN) {
            return 0.7f;
        } else if (type == EntityTypes.HOGLIN || type == EntityTypes.ZOGLIN) {
            return 1.4f;
        } else if (type == EntityTypes.COW) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.4f : 1.3f;
        } else if (type == EntityTypes.STRIDER) {
            return 1.7f;
        } else if (type == EntityTypes.CREEPER) {
            return 1.7f;
        } else if (type == EntityTypes.DONKEY) {
            return 1.5f;
        }  else if (type == EntityTypes.ENDERMAN || type == EntityTypes.WARDEN) {
            return 2.9f;
        } else if (type == EntityTypes.ENDERMITE || type == EntityTypes.COD) {
            return 0.3f;
        } else if (type == EntityTypes.END_CRYSTAL) {
            return 2f;
        } else if (type == EntityTypes.ENDER_DRAGON) {
            return 8f;
        } else if (type == EntityTypes.FIREBALL) {
            return 1f;
        } else if (type == EntityTypes.FOX) {
            return 0.7f;
        } else if (type == EntityTypes.GHAST) {
            return 4f;
        } else if (type == EntityTypes.GIANT) {
            return 12f;
        } else if (type == EntityTypes.GUARDIAN) {
            return 0.85f;
        } else if (type == EntityTypes.HORSE) {
            return 1.6f;
        } else if (type == EntityTypes.IRON_GOLEM) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 2.7f : 2.9f;
        } else if (type == EntityTypes.CREAKING) {
            return 2.7f;
        } else if (type == EntityTypes.LLAMA || type == EntityTypes.TRADER_LLAMA) {
            return 1.87f;
        } else if (type == EntityTypes.TROPICAL_FISH) {
            return 0.4f;
        }  else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.MINECART_ABSTRACT)) {
            return 0.7f;
        } else if (type == EntityTypes.MULE) {
            return 1.6f;
        } else if (type == EntityTypes.MOOSHROOM) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.4f : 1.3f;
        } else if (type == EntityTypes.OCELOT) {
            return 0.7f;
        } else if (type == EntityTypes.PANDA) {
            return 1.25f;
        } else if (type == EntityTypes.PLAYER || type == EntityTypes.MANNEQUIN) {
            return 1.8f;
        } else if (type == EntityTypes.POLAR_BEAR) {
            return 1.4f;
        } else if (type == EntityTypes.PUFFERFISH) {
            return 0.7f;
        } else if (type == EntityTypes.RABBIT) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.5f : 0.7f;
        } else if (type == EntityTypes.RAVAGER) {
            return 2.2f;
        } else if (type == EntityTypes.SALMON) {
            return 0.4f;
        } else if (type == EntityTypes.SHEEP || type == EntityTypes.GOAT) {
            return 1.3f;
        } else if (type == EntityTypes.SHULKER) { // Could maybe guess peek size, although seems useless
            return 2f;
        } else if (type == EntityTypes.SILVERFISH) {
            return 0.3f;
        } else if (type == EntityTypes.SKELETON) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.99f : 1.95f;
        } else if (type == EntityTypes.SKELETON_HORSE) {
            return 1.6f;
        }  else if (type == EntityTypes.SMALL_FIREBALL) {
            return 0.3125f;
        } else if (type == EntityTypes.SNOW_GOLEM) {
            return 1.9f;
        } else if (type == EntityTypes.STRAY) {
            return 1.99f;
        } else if (type == EntityTypes.TURTLE) {
            return 0.4f;
        } else if (type == EntityTypes.WITHER) {
            return 3.5f;
        } else if (type == EntityTypes.WITHER_SKELETON) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 2.4f : 2.535f;
        } else if (type == EntityTypes.WOLF) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.85f : 0.8f;
        } else if (type == EntityTypes.ZOMBIE_HORSE) {
            return 1.6f;
        } else if (type == EntityTypes.TADPOLE) {
            return 0.3f;
        } else if (type == EntityTypes.SNIFFER) {
            return 1.75f;
        } else if (type == EntityTypes.CAMEL) {
            return 2.375f;
        } else if (type == EntityTypes.BREEZE) {
            return 1.77f;
        } else if (type == EntityTypes.BOGGED) {
            return 1.99f;
        } else if (type == EntityTypes.WIND_CHARGE) {
            return 0.3125f;
        } else if (type == EntityTypes.ARMOR_STAND) {
            return 1.975F;
        } else if (type == EntityTypes.FALLING_BLOCK) {
            return 0.98F;
        } else if (type == EntityTypes.VILLAGER && player.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
            return 1.8F;
        } else if (type == EntityTypes.FIREWORK_ROCKET) {
            return 0.25F;
        } else if (type == EntityTypes.COPPER_GOLEM) {
            return 1.0F;
        }
        return 1.95f;
    }

    private static float getBabyScaleFactor(PacketEntity packetEntity) {
        final EntityType type = packetEntity.type;
        if (type == EntityTypes.TURTLE) return 0.3f;
        if (type == EntityTypes.HAPPY_GHAST) return 0.2375f;
        if (type == EntityTypes.DOLPHIN) return 0.65f;
        if (type == EntityTypes.ARMADILLO) return 0.6f;
        if (type == EntityTypes.CAMEL) return 0.45f;
        return 0.5f;
    }
}

