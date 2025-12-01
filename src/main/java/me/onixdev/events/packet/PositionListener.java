package me.onixdev.events.packet;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import me.onixdev.OnixAnticheat;
import me.onixdev.event.impl.TickEvent;
import me.onixdev.user.OnixUser;

public class PositionListener extends PacketListenerAbstract {
    public PositionListener() {
        super(PacketListenerPriority.LOWEST);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
            if (user != null && user.hasConfirmPlayState()) {
            Location location = flying.getLocation();
            boolean rotation = flying.hasRotationChanged();
            boolean position = flying.hasPositionChanged();
            if (rotation) user.getRotationContainer().handle(location.getYaw(),location.getPitch());
            user.handleEvent(new TickEvent(TickEvent.Target.FLYING));
            user.currentTick++;
            user.lastHitTime++;
            }
        }
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity use = new WrapperPlayClientInteractEntity(event);
            if (use.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
                if (user != null && user.hasConfirmPlayState()) {
                    user.lastHitTime = 0;
                }
            }
        }
    }
}
