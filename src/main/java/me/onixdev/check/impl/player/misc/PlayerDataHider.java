package me.onixdev.check.impl.player.misc;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.user.OnixUser;
import me.onixdev.util.net.MinecraftValues;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SuppressWarnings("all")
public class PlayerDataHider extends Check {
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
                                    float randomHealth = (float) (1 + random.nextInt(20));
                                    data.setValue(randomHealth);
                                    shouldPush = true;
                                }
                            } else if (OnixAnticheat.INSTANCE.getConfigManager().absorptionHider && data.getIndex() == MinecraftValues.ABSORPTION) {
                                setDynamicValue(data, 1000);
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
