package me.onixdev.events.packet;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import me.onixdev.OnixAnticheat;
import me.onixdev.event.impl.PlayerUseEntityEvent;
import me.onixdev.event.impl.TickEvent;
import me.onixdev.user.OnixUser;

public class PositionListener extends PacketListenerAbstract {
    public PositionListener() {
        super(PacketListenerPriority.NORMAL);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.TELEPORT_CONFIRM) {
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
        }
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
            if (user != null && user.hasConfirmPlayState()) {
                Location location = flying.getLocation();
                boolean rotation = flying.hasRotationChanged();
                if (rotation) user.getRotationContainer().handle(location.getYaw(), location.getPitch());
                user.getMovementContainer().handleFlying(event, flying);
                user.handleEvent(new TickEvent(TickEvent.Target.FLYING));
                user.currentTick++;
                user.lastHitTime++;
                if (user.isUsingItem()) user.ItemUseTime++;
                else user.ItemUseTime = 0;
                user.getMovementContainer().registerIncomingPreHandler(event);
            }
        }
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
            if (user != null && user.hasConfirmPlayState()) {
                WrapperPlayClientEntityAction action = new WrapperPlayClientEntityAction(event);
            }
        }
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity use = new WrapperPlayClientInteractEntity(event);
            if (use.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
                if (user != null && user.hasConfirmPlayState()) {
                    user.lastHitTime = 0;
                    PlayerUseEntityEvent event1 = new PlayerUseEntityEvent(use.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK ? PlayerUseEntityEvent.UseType.ATTACK : PlayerUseEntityEvent.UseType.INTERACT, use.getEntityId());
                    user.handleEvent(event1);
                    if (user.shouldMitigate() && user.getMitigateType().equals("canceldamage") || event1.isCancelled())
                        event.setCancelled(true);
                }
            }
        }
    }
}
