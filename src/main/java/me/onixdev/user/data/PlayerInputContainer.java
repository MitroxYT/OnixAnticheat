package me.onixdev.user.data;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerInput;
import me.onixdev.event.impl.TickEvent;
import me.onixdev.user.OnixUser;
import me.onixdev.util.net.ClientInput;

public class PlayerInputContainer {
    private final OnixUser user;

    public PlayerInputContainer(OnixUser user) {
        this.user = user;
    }

    public void onPacket(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_INPUT) {
            WrapperPlayClientPlayerInput input = new WrapperPlayClientPlayerInput(event);
            user.Input = new ClientInput(input.isForward(), input.isBackward(), input.isLeft(), input.isRight(), input.isJump(), input.isShift(), input.isSprint(), false, 0.0, 0.0, 0);
        }
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_TICK_END)
            user.handleEvent(new TickEvent(TickEvent.Target.TICKEND));
    }
}
