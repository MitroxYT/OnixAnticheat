package me.onixdev.events.packet;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import me.onixdev.OnixAnticheat;
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
            }
        }
    }
}
