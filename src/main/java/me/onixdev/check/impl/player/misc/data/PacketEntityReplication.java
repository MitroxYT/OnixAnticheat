package me.onixdev.check.impl.player.misc.data;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.EntityPositionData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.protocol.potion.PotionType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import dev.onixac.api.events.api.BaseEvent;
import io.github.retrooper.packetevents.util.viaversion.ViaVersionUtil;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.event.impl.TickEvent;
import me.onixdev.user.OnixUser;
import me.onixdev.util.grimentity.entity.PacketEntity;
import me.onixdev.util.grimentity.entity.TrackerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class PacketEntityReplication extends Check {

    private final AtomicBoolean hasSentPreWavePacket = new AtomicBoolean(true);

    private final List<Integer> despawnedEntitiesThisTransaction = new ArrayList<>();


    public PacketEntityReplication(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("packete").setType("a").build());
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof TickEvent event1 && ((TickEvent) event).notTickEnd()) {
            player.compensatedEntities.entitiesRemovedThisTick.clear();
        }
    }


    @Override
    public void onPacketOut(PacketSendEvent event) {
        if ((event.getPacketType() == PacketType.Play.Server.PING || event.getPacketType() == PacketType.Play.Server.WINDOW_CONFIRMATION)) {
            despawnedEntitiesThisTransaction.clear();
        }
        else if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            WrapperPlayServerSpawnLivingEntity packetOutEntity = new WrapperPlayServerSpawnLivingEntity(event);
            addEntity(packetOutEntity.getEntityId(), packetOutEntity.getEntityUUID(), packetOutEntity.getEntityType(), packetOutEntity.getPosition(), packetOutEntity.getYaw(), packetOutEntity.getPitch(), packetOutEntity.getEntityMetadata(), 0);
        }
        else if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
            WrapperPlayServerSpawnEntity packetOutEntity = new WrapperPlayServerSpawnEntity(event);
            addEntity(packetOutEntity.getEntityId(), packetOutEntity.getUUID().orElse(null), packetOutEntity.getEntityType(), packetOutEntity.getPosition(), packetOutEntity.getYaw(), packetOutEntity.getPitch(), null, packetOutEntity.getData());
        }
        else if (event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            WrapperPlayServerSpawnPlayer packetOutEntity = new WrapperPlayServerSpawnPlayer(event);
            addEntity(packetOutEntity.getEntityId(), packetOutEntity.getUUID(), EntityTypes.PLAYER, packetOutEntity.getPosition(), packetOutEntity.getYaw(), packetOutEntity.getPitch(), packetOutEntity.getEntityMetadata(), 0);
        }
        else if (event.getPacketType() == PacketType.Play.Server.SPAWN_PAINTING) {
            WrapperPlayServerSpawnPainting packetOutEntity = new WrapperPlayServerSpawnPainting(event);
            addEntity(packetOutEntity.getEntityId(), packetOutEntity.getUUID(), EntityTypes.PAINTING, packetOutEntity.getPosition().toVector3d(), 0, 0f, null, packetOutEntity.getDirection().getHorizontalIndex());
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            WrapperPlayServerEntityRelativeMove move = new WrapperPlayServerEntityRelativeMove(event);
            move.setOnGround(false);
            handleMoveEntity(event, move.getEntityId(), move.getDeltaX(), move.getDeltaY(), move.getDeltaZ(), null, null, true, true);
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            WrapperPlayServerEntityRelativeMoveAndRotation move = new WrapperPlayServerEntityRelativeMoveAndRotation(event);
            move.setOnGround(false);
            handleMoveEntity(event, move.getEntityId(), move.getDeltaX(), move.getDeltaY(), move.getDeltaZ(), move.getYaw() * 0.7111111F, move.getPitch() * 0.7111111F, true, true);
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            WrapperPlayServerEntityTeleport move = new WrapperPlayServerEntityTeleport(event);
            Vector3d pos = move.getPosition();
            move.setOnGround(false);
            handleMoveEntity(event, move.getEntityId(), pos.getX(), pos.getY(), pos.getZ(), move.getYaw(), move.getPitch(), false, true);
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_POSITION_SYNC) {
            // ENTITY_TELEPORT but without relative flags
            WrapperPlayServerEntityPositionSync move = new WrapperPlayServerEntityPositionSync(event);
            final EntityPositionData values = move.getValues();
            final Vector3d pos = values.getPosition();
            move.setOnGround(false);
            // TODO this isn't technically correct
            // If the position sync is to a pos > 4096 from the entity pos, client does some special stuff without interpolation
            handleMoveEntity(event, move.getId(), pos.getX(), pos.getY(), pos.getZ(), values.getYaw(), values.getPitch(), false, true);
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_ROTATION) { // Affects interpolation
            WrapperPlayServerEntityRotation move = new WrapperPlayServerEntityRotation(event);
            move.setOnGround(false);
            handleMoveEntity(event, move.getEntityId(), 0, 0, 0, move.getYaw() * 0.7111111F, move.getPitch() * 0.7111111F, true, false);
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            WrapperPlayServerEntityMetadata entityMetadata = new WrapperPlayServerEntityMetadata(event);
            player.lagCompensation.addTask(player.getConnectionContainer().lastTransactionSent.get(), () -> player.compensatedEntities.updateEntityMetadata(entityMetadata.getEntityId(), entityMetadata.getEntityMetadata()));
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            WrapperPlayServerEntityEquipment equipment = new WrapperPlayServerEntityEquipment(event);
            player.lagCompensation.addTask(player.getConnectionContainer().lastTransactionSent.get(), () -> player.compensatedEntities.updateEntityEquipment(equipment.getEntityId(), equipment.getEquipment()));
        }

        // 1.19.3+
        else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
            WrapperPlayServerPlayerInfoUpdate info = new WrapperPlayServerPlayerInfoUpdate(event);
            player.lagCompensation.addTask(player.getConnectionContainer().lastTransactionSent.get(), () -> {
                for (WrapperPlayServerPlayerInfoUpdate.PlayerInfo entry : info.getEntries()) {
                    final UserProfile gameProfile = entry.getGameProfile();
                    final UUID uuid = gameProfile.getUUID();
                    player.compensatedEntities.profiles.put(uuid, gameProfile);
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_REMOVE) {
            WrapperPlayServerPlayerInfoRemove remove = new WrapperPlayServerPlayerInfoRemove(event);
            player.lagCompensation.addTask(player.getConnectionContainer().lastTransactionSent.get(), () -> remove.getProfileIds().forEach(player.compensatedEntities.profiles::remove));
        } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
            WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(event);
            player.lagCompensation.addTask(player.getConnectionContainer().lastTransactionSent.get(), () -> {
                if (info.getAction() == WrapperPlayServerPlayerInfo.Action.ADD_PLAYER) {
                    for (WrapperPlayServerPlayerInfo.PlayerData entry : info.getPlayerDataList()) {
                        final UserProfile gameProfile = entry.getUserProfile();
                        assert gameProfile != null;
                        final UUID uuid = gameProfile.getUUID();
                        player.compensatedEntities.profiles.put(uuid, gameProfile);
                    }
                } else if (info.getAction() == WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER) {
                    info.getPlayerDataList().forEach(profile -> player.compensatedEntities.profiles.remove(profile.getUserProfile() != null ? profile.getUserProfile().getUUID() : null));
                }
            });
        }

        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
            WrapperPlayServerEntityEffect effect = new WrapperPlayServerEntityEffect(event);

            PotionType type = effect.getPotionType();


            if (isDirectlyAffectingPlayer(player, effect.getEntityId())) player.sendTransaction();

            player.lagCompensation.addTask(player.getConnectionContainer().lastTransactionSent.get(), () -> {
                PacketEntity entity = player.compensatedEntities.getEntity(effect.getEntityId());
                if (entity == null) return;

                entity.addPotionEffect(type, effect.getEffectAmplifier());
            });
        }

        else if (event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
            WrapperPlayServerRemoveEntityEffect effect = new WrapperPlayServerRemoveEntityEffect(event);

            if (isDirectlyAffectingPlayer(player, effect.getEntityId())) player.sendTransaction();

            player.lagCompensation.addTask(player.getConnectionContainer().lastTransactionSent.get(), () -> {
                PacketEntity entity = player.compensatedEntities.getEntity(effect.getEntityId());
                if (entity == null) return;

                entity.removePotionEffect(effect.getPotionType());
            });
        }

        else if (event.getPacketType() == PacketType.Play.Server.UPDATE_ATTRIBUTES) {
            WrapperPlayServerUpdateAttributes attributes = new WrapperPlayServerUpdateAttributes(event);

            int entityID = attributes.getEntityId();

            // The attributes for this entity is active, currently
            if (isDirectlyAffectingPlayer(player, entityID)) player.sendTransaction();

            player.lagCompensation.addTask(player.getConnectionContainer().lastTransactionSent.get(),
                    () -> player.compensatedEntities.updateAttributes(entityID, attributes.getProperties()));
        }





        else if (event.getPacketType() == PacketType.Play.Server.SET_PASSENGERS) {
            WrapperPlayServerSetPassengers mount = new WrapperPlayServerSetPassengers(event);

            int vehicleID = mount.getEntityId();
            int[] passengers = mount.getPassengers();

            handleMountVehicle(event, vehicleID, passengers);
        }

        else if (event.getPacketType() == PacketType.Play.Server.ATTACH_ENTITY) {
            WrapperPlayServerAttachEntity attach = new WrapperPlayServerAttachEntity(event);

            // This packet was replaced by the mount packet on 1.9+ servers - to support multiple passengers on one vehicle
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) return;

            // If this is mounting rather than leashing
            if (!attach.isLeash()) {
                // Alright, let's convert this to the 1.9+ format to make it easier for grim
                int vehicleID = attach.getHoldingId();
                int attachID = attach.getAttachedId();
                TrackerData trackerData = player.compensatedEntities.getTrackedEntity(attachID);

                if (trackerData != null) {
                    // 1.8 sends a vehicle ID of -1 to dismount the entity from its vehicle
                    // This is opposite of the 1.9+ format, which sends the vehicle ID and then an empty array.
                    if (vehicleID == -1) { // Dismounting
                        vehicleID = trackerData.getLegacyPointEightMountedUpon();
                        handleMountVehicle(event, vehicleID, new int[]{}); // The vehicle is empty
                    } else { // Mounting
                        trackerData.setLegacyPointEightMountedUpon(vehicleID);
                        handleMountVehicle(event, vehicleID, new int[]{attachID});
                    }
                } else {
                }
            }
        }

        else if (event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
            WrapperPlayServerDestroyEntities destroy = new WrapperPlayServerDestroyEntities(event);

            int[] destroyEntityIds = destroy.getEntityIds();

            for (int entityID : destroyEntityIds) {
                despawnedEntitiesThisTransaction.add(entityID);
                player.compensatedEntities.serverPositionsMap.remove(entityID);
                // Remove the tracked vehicle (handling tracking knockback) if despawned
                if (player.compensatedEntities.serverPlayerVehicle != null && player.compensatedEntities.serverPlayerVehicle == entityID) {
                    player.compensatedEntities.serverPlayerVehicle = null;
                }
            }

            final int destroyTransaction = player.getConnectionContainer().lastTransactionSent.get() + 1;
            player.lagCompensation.addTask(destroyTransaction, () -> {
                for (int entityId : destroyEntityIds) {
                    player.compensatedEntities.removeEntity(entityId);
                    player.compensatedEntities.entitiesRemovedThisTick.add(entityId);
                }
            });

        }
    }

    private void handleMountVehicle(PacketSendEvent event, int vehicleID, int[] passengers) {
        boolean wasInVehicle = player.compensatedEntities.serverPlayerVehicle != null && player.compensatedEntities.serverPlayerVehicle == vehicleID;
        boolean inThisVehicle = false;

        for (int passenger : passengers) {
            inThisVehicle = passenger == player.getId();
            if (inThisVehicle) break;
        }

        // Better lag compensation if we were affected by this
        if (wasInVehicle || inThisVehicle) {
            player.sendTransaction();
        }

        final boolean mounted = inThisVehicle && !wasInVehicle;
        if (mounted) {
     //       player.handleMountVehicle(vehicleID);
        }

        final boolean dismounted = !inThisVehicle && wasInVehicle;
        if (dismounted) {
         //   player.handleDismountVehicle(event);
        }

        player.lagCompensation.addTask(player.getConnectionContainer().lastTransactionSent.get(), () -> {
            PacketEntity vehicle = player.compensatedEntities.getEntity(vehicleID);

            // Vanilla likes sending null vehicles, so we must ignore those like the client ignores them
            if (vehicle == null) return;

            // Eject existing passengers for this vehicle
            for (PacketEntity passenger : new ArrayList<>(vehicle.passengers)) {
                passenger.eject();
            }

            // Add the entities as vehicles
            for (int entityID : passengers) {
                PacketEntity passenger = player.compensatedEntities.getEntity(entityID);
                if (passenger == null) continue;
                passenger.mount(vehicle);
            }

        });
    }

    private void handleMoveEntity(PacketSendEvent event, int entityId, double deltaX, double deltaY, double deltaZ, Float yaw, Float pitch, boolean isRelative, boolean hasPos) {
        TrackerData data = player.compensatedEntities.getTrackedEntity(entityId);

         player.sendTransaction();

        if (data != null) {

            // Update the tracked server's entity position
            if (isRelative) {
                // There is a bug where vehicles may start flying due to mojang setting packet position on the client
                // (Works at 0 ping but causes funny bugs at any higher ping)
                // As we don't want vehicles to fly, we need to replace it with a teleport if it is player vehicle
                //
                // Don't bother with client controlled vehicles though
                boolean vanillaVehicleFlight = player.compensatedEntities.serverPlayerVehicle != null
                        && player.compensatedEntities.serverPlayerVehicle == entityId
                        && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)
                        // TODO: https://discord.com/channels/721686193061888071/721686193515003966/1310659538831020123
                        // Why does the server now send an entity rel move packet matching the player's vehicle movement every time?
                        && PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_21_2)
                        && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9);

                if (vanillaVehicleFlight ||
                        ((Math.abs(deltaX) >= 3.9375 || Math.abs(deltaY) >= 3.9375 || Math.abs(deltaZ) >= 3.9375) && player.getClientVersion().isOlderThan(ClientVersion.V_1_9) && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9))) {
                    player.getUser().writePacket(new WrapperPlayServerEntityTeleport(entityId, new Vector3d(data.getX() + deltaX, data.getY() + deltaY, data.getZ() + deltaZ), yaw == null ? data.getXRot() : yaw, pitch == null ? data.getYRot() : pitch, false));
                    event.setCancelled(true);
                    return;
                }

                data.setX(data.getX() + deltaX);
                data.setY(data.getY() + deltaY);
                data.setZ(data.getZ() + deltaZ);
            } else {
                data.setX(deltaX);
                data.setY(deltaY);
                data.setZ(deltaZ);
            }
            if (yaw != null) {
                data.setXRot(yaw);
                data.setYRot(pitch);
            }

            // We can't hang two relative moves on one transaction
            if (data.getLastTransactionHung() == player.getConnectionContainer().lastTransactionSent.get()) {
                player.sendTransaction();
            }
            data.setLastTransactionHung(player.getConnectionContainer().lastTransactionSent.get());
        }

        int lastTrans = player.getConnectionContainer().lastTransactionSent.get();

        player.lagCompensation.addTask(lastTrans, () -> {
            PacketEntity entity = player.compensatedEntities.getEntity(entityId);
            if (entity == null) return;


            entity.onFirstTransaction(isRelative, hasPos, deltaX, deltaY, deltaZ, player);
        });

        player.lagCompensation.addTask(lastTrans + 1, () -> {
            PacketEntity entity = player.compensatedEntities.getEntity(entityId);
            if (entity == null) return;
            entity.onSecondTransaction();
        });
    }

    public void addEntity(int entityID, UUID uuid, EntityType type, Vector3d position, float xRot, float yRot, List<EntityData<?>> entityMetadata, int extraData) {
        if (despawnedEntitiesThisTransaction.contains(entityID)) {
            player.sendTransaction();
        }

        player.compensatedEntities.serverPositionsMap.put(entityID, new TrackerData(position.getX(), position.getY(), position.getZ(), xRot, yRot, type, player.getConnectionContainer().lastTransactionSent.get()));

        player.lagCompensation.addTask(player.getConnectionContainer().lastTransactionSent.get(), () -> {
            player.compensatedEntities.addEntity(entityID, uuid, type, position, xRot, extraData);


            if (entityMetadata != null) {
                player.compensatedEntities.updateEntityMetadata(entityID, entityMetadata);
            }
        });
    }

    private boolean isDirectlyAffectingPlayer(OnixUser player, int entityID) {
        return (player.compensatedEntities.serverPlayerVehicle == null && entityID == player.getId()) ||
                (player.compensatedEntities.serverPlayerVehicle != null && entityID == player.compensatedEntities.serverPlayerVehicle);
    }



    public void tickStartTick() {
        hasSentPreWavePacket.set(false);
    }

    private int maxFireworkBoostPing = 1000;


}
