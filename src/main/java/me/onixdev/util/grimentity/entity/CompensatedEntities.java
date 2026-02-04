package me.onixdev.util.grimentity.entity;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.attribute.Attribute;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.protocol.potion.PotionType;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.protocol.world.Direction;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.onixdev.user.OnixUser;
import me.onixdev.util.grimentity.attribute.ValuedAttribute;
import me.onixdev.util.grimentity.boxes.BoundingBoxSize;
import me.onixdev.util.grimentity.boxes.SimpleCollisionBox;
import me.onixdev.util.net.BukkitNms;

import java.util.*;

public class CompensatedEntities {

    public static final UUID SPRINTING_MODIFIER_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    public static final UUID SNOW_MODIFIER_UUID = UUID.fromString("1eaf83ff-7207-4596-b37a-d7a07b3ec4ce");

    //    public final SectionedEntityMap entityMap = new SectionedEntityMap();
    public final Int2ObjectOpenHashMap<PacketEntity> entityMap = new Int2ObjectOpenHashMap<>(40, 0.7f); // needs to be linked to replicate vanilla iteration order!
    public final IntArraySet entitiesRemovedThisTick = new IntArraySet();
    public final Int2ObjectOpenHashMap<TrackerData> serverPositionsMap = new Int2ObjectOpenHashMap<>(40, 0.7f); // never iterate over, so iteration order does not matter
    public final Object2ObjectOpenHashMap<UUID, UserProfile> profiles = new Object2ObjectOpenHashMap<>();
    public Integer serverPlayerVehicle = null;
    public boolean hasSprintingAttributeEnabled = false;

    OnixUser player;

    public TrackerData selfTrackedEntity;
    public PacketEntitySelf self;

    public CompensatedEntities(OnixUser player) {
        this.player = player;
        this.self = new PacketEntitySelf(player);
        this.selfTrackedEntity = new TrackerData(0, 0, 0, 0, 0, EntityTypes.PLAYER, player.getConnectionContainer().lastTransactionSent.get());
    }

    public int getPacketEntityID(PacketEntity entity) {
        for (Map.Entry<Integer, PacketEntity> entry : entityMap.int2ObjectEntrySet()) {
            if (entry.getValue() == entity) {
                return entry.getKey();
            }
        }
        return Integer.MIN_VALUE;
    }

    public void tick() {
        this.self.setPositionRaw(player, new SimpleCollisionBox(player.getMovementContainer().x, player.getMovementContainer().y, player.getMovementContainer().z, player.getMovementContainer().x, player.getMovementContainer().y, player.getMovementContainer().z));
        for (PacketEntity vehicle : entityMap.values()) {
            for (PacketEntity passenger : vehicle.passengers) {
                tickPassenger(vehicle, passenger);
            }
        }
    }

    public void removeEntity(int entityID) {
        PacketEntity entity = entityMap.remove(entityID);
        if (entity == null) return;


        for (PacketEntity passenger : new ArrayList<>(entity.passengers)) {
            passenger.eject();
        }
    }

    public OptionalInt getSlowFallingAmplifier() {
        return player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_2) ? OptionalInt.empty() : getPotionLevelForPlayer(PotionTypes.SLOW_FALLING);
    }

    public OptionalInt getPotionLevelForPlayer(PotionType type) {
        return getEntityInControl().getPotionEffectLevel(type);
    }

    public OptionalInt getPotionLevelForSelfPlayer(PotionType type) {
        return self.getPotionEffectLevel(type);
    }

    public boolean hasPotionEffect(PotionType type) {
        return getEntityInControl().hasPotionEffect(type);
    }

    public PacketEntity getEntityInControl() {
        return self.getRiding() != null ? self.getRiding() : self;
    }

    public void updateAttributes(int entityID, List<WrapperPlayServerUpdateAttributes.Property> objects) {
        if (entityID == player.getId()) {
            // Check for sprinting attribute. Note that this value can desync: https://bugs.mojang.com/browse/MC-69459
            for (WrapperPlayServerUpdateAttributes.Property snapshotWrapper : objects) {
                final Attribute attribute = snapshotWrapper.getAttribute();
                if (attribute != Attributes.MOVEMENT_SPEED) continue;

                boolean found = false;
                List<WrapperPlayServerUpdateAttributes.PropertyModifier> modifiers = snapshotWrapper.getModifiers();
                for (WrapperPlayServerUpdateAttributes.PropertyModifier modifier : modifiers) {
                    final ResourceLocation name = modifier.getName();
                    if (name.getKey().equals(SPRINTING_MODIFIER_UUID.toString()) || name.getKey().equals("sprinting")) {
                        found = true;
                        break;
                    }
                }

                // The server can set the player's sprinting attribute
                hasSprintingAttributeEnabled = found;
                break;
            }
        }

        PacketEntity entity = player.compensatedEntities.getEntity(entityID);
        if (entity == null) return;

        for (WrapperPlayServerUpdateAttributes.Property snapshotWrapper : objects) {
            Attribute attribute = snapshotWrapper.getAttribute();
            if (attribute == null) continue; // TODO: Warn if this happens? Either modded server or bug in packetevents.

            if (attribute == Attributes.HORSE_JUMP_STRENGTH) {
                attribute = Attributes.JUMP_STRENGTH;
            }

            final Optional<ValuedAttribute> valuedAttribute = entity.getAttribute(attribute);
            if (valuedAttribute.isEmpty()) {
                continue;
            }

            valuedAttribute.get().with(snapshotWrapper);
        }
    }

    private void tickPassenger(PacketEntity riding, PacketEntity passenger) {
        if (riding == null || passenger == null) {
            return;
        }

        passenger.setPositionRaw(player, riding.getPossibleLocationBoxes().offset(0, BoundingBoxSize.getMyRidingOffset(riding) + BoundingBoxSize.getPassengerRidingOffset(player, passenger), 0));

        for (PacketEntity passengerPassenger : riding.passengers) {
            tickPassenger(passenger, passengerPassenger);
        }
    }

    public PacketEntity addEntity(int entityID, UUID uuid, EntityType entityType, Vector3d position, float xRot, int data) {
        // Dropped items are all server sided and players can't interact with them (except create them!), save the performance
        if (entityType == EntityTypes.ITEM) return null;
        if (entityType != EntityTypes.PLAYER) return null;

        PacketEntity packetEntity;

            packetEntity = new PacketEntity(player, uuid, entityType, position.getX(), position.getY(), position.getZ());

        entityMap.put(entityID, packetEntity);
        return packetEntity;

    }

    public PacketEntity getEntity(int entityID) {
        if (entityID == player.getId()) {
            return self;
        }
        return entityMap.get(entityID);
    }

    public TrackerData getTrackedEntity(int id) {
        if (id == player.getId()) {
            return selfTrackedEntity;
        }
        return serverPositionsMap.get(id);
    }

    public void updateEntityMetadata(int entityID, List<EntityData<?>> watchableObjects) {
        PacketEntity entity = player.compensatedEntities.getEntity(entityID);
        if (entity == null) return;


        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9_4)) {
            EntityData<?> gravity = BukkitNms.getIndex(watchableObjects, 5);

            if (gravity != null) {
                Object gravityObject = gravity.getValue();

                if (gravityObject instanceof Boolean) {
                    // Vanilla uses hasNoGravity, which is a bad name IMO
                    // hasGravity > hasNoGravity
                    entity.hasGravity = !((Boolean) gravityObject);
                }
            }
        }

        if (entity.type == EntityTypes.FIREWORK_ROCKET) {
            int offset = 0;
            if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_12_2)) {
                offset = 2;
            } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
                offset = 1;
            }

            EntityData<?> fireworkWatchableObject = BukkitNms.getIndex(watchableObjects, 9 - offset);
            if (fireworkWatchableObject == null) return;

            if (fireworkWatchableObject.getValue() instanceof Integer) {
                int attachedEntityID = (Integer) fireworkWatchableObject.getValue();
                if (attachedEntityID == player.getId()) {
                  //  player.compensatedFireworks.addNewFirework(entityID);
                }
            } else {
                Optional<Integer> attachedEntityID = (Optional<Integer>) fireworkWatchableObject.getValue();

                if (attachedEntityID.isPresent() && attachedEntityID.get().equals(player.getId())) {
                    //player.compensatedFireworks.addNewFirework(entityID);
                }
            }

        }
    }

    public void updateEntityEquipment(int entityId, List<Equipment> equipment) {
        PacketEntity entity = player.compensatedEntities.getEntity(entityId);
        if (entity == null || !entity.trackEntityEquipment) return;

        for (Equipment equipmentItem : equipment) {
            entity.setItemBySlot(equipmentItem.getSlot(), equipmentItem.getItem());
        }
    }
}
