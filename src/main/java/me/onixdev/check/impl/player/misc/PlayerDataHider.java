package me.onixdev.check.impl.player.misc;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.user.OnixUser;
import me.onixdev.util.grimentity.entity.PacketEntity;
import me.onixdev.util.net.MinecraftValues;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public class PlayerDataHider extends Check {
    private final Set<String> healthObjectives = ConcurrentHashMap.newKeySet();
    public PlayerDataHider(OnixUser player) {
        super(player,CheckBuilder.create().setCheckName("aaaaa").setType("a").build());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPacketOut(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata(event);
            int entityId = wrapper.getEntityId();
            if (event.getUser().getEntityId() != entityId) {
                PacketEntity entity = player.compensatedEntities.getEntity(entityId);
                if (entity == null ||  entity.type != EntityTypes.PLAYER) return;
                    List<? extends EntityData> entityMetaData = wrapper.getEntityMetadata();

                    boolean shouldPush = false;
                    Random random = new Random();
                    Iterator iterator = entityMetaData.iterator();

                    while (true) {
                        while (iterator.hasNext()) {

                            EntityData data = (EntityData) iterator.next();
                            if (OnixAnticheat.INSTANCE.getConfigManager().healthHider && data.getIndex() == MinecraftValues.HEALTH) {
                                float health = Float.parseFloat(String.valueOf(data.getValue()));
                                if (health > 0.0F) {
                                    int randomHealth =  (1 + random.nextInt(20));
                                    setDynamicValue(data, randomHealth);
                                  //  data.setValue(randomHealth);
                                    shouldPush = true;
                                }
                            } else if (OnixAnticheat.INSTANCE.getConfigManager().absorptionHider && data.getIndex() == MinecraftValues.ABSORPTION) {
//                                setDynamicValue(data, 1000);
                                int randomHealth =  (1 + random.nextInt(20));
                                setDynamicValue(data, randomHealth);
                                shouldPush = true;
                            }

                            if (OnixAnticheat.INSTANCE.getConfigManager().xpHider && data.getIndex() == MinecraftValues.XP) {
                                setDynamicValue(data, 1);
                                shouldPush = true;
                            }
                        }

                        if (shouldPush) {
                            push(event, wrapper.getEntityId(), entityMetaData);
                        }

                        return;
                    }
            }
        }
        if (event.getPacketType().equals(PacketType.Play.Server.SCOREBOARD_OBJECTIVE)) {
            if (OnixAnticheat.INSTANCE.getConfigManager().fixHeathBypass) handleScoreboardObjective(event);
        }

        if (event.getPacketType().equals(PacketType.Play.Server.UPDATE_SCORE)) {
            if (OnixAnticheat.INSTANCE.getConfigManager().fixHeathBypass)  handleUpdateScore(event);
        }
    }
    private void handleScoreboardObjective(PacketSendEvent event) {
        WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective(event);
        WrapperPlayServerScoreboardObjective.ObjectiveMode mode = packet.getMode();
        String objectiveName = packet.getName();

        if (mode == WrapperPlayServerScoreboardObjective.ObjectiveMode.REMOVE) {
            healthObjectives.remove(objectiveName);
            return;
        }

        boolean isHeartsRenderType = packet.getRenderType() == WrapperPlayServerScoreboardObjective.RenderType.HEARTS;

        if (mode == WrapperPlayServerScoreboardObjective.ObjectiveMode.UPDATE || isHeartsRenderType) {
            if (isHeartsRenderType) {
                healthObjectives.add(objectiveName);
            } else {
                healthObjectives.remove(objectiveName);
            }
        }
    }

    private void handleUpdateScore(PacketSendEvent event) {
        WrapperPlayServerUpdateScore packet = new WrapperPlayServerUpdateScore(event);

        if (healthObjectives.contains(packet.getObjectiveName()) && packet.getValue().isPresent()) {
            packet.setValue(Optional.of(-1));
            event.markForReEncode(true);
        }
    }
    @SuppressWarnings("rawtypes")
    void push(PacketSendEvent event, int entityId, List<? extends EntityData> dataList) {
        event.setCancelled(true);
        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(entityId, (List) dataList);
        ChannelHelper.runInEventLoop(player.getUser().getChannel(), () -> {
            player.getUser().sendPacketSilently(metadata);
        });
    }

    @SuppressWarnings("unchecked")
    private void setDynamicValue(EntityData obj, int spoofValue) {
        Object value = obj.getValue();
        if (value instanceof Integer) {
            obj.setValue(spoofValue);
        } else if (value instanceof Short) {
            obj.setValue((short) spoofValue);
        } else if (value instanceof Byte) {
            obj.setValue((byte) spoofValue);
        } else if (value instanceof Long) {
            obj.setValue(spoofValue);
        } else if (value instanceof Float) {
            obj.setValue((float) spoofValue);
        } else if (value instanceof Double) {
            obj.setValue(spoofValue);
        }
    }
}
