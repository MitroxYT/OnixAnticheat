package me.onixdev.events.packet;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import me.onixdev.OnixAnticheat;
import me.onixdev.user.OnixUser;

public class PlayerConnectionHandler extends PacketListenerAbstract {
    public PlayerConnectionHandler() {
        super(PacketListenerPriority.LOWEST);
    }
    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PONG || event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
            if (user != null) {
                user.getConnectionContainer().handlein(event);
            }
        }
    }
    @Override
    public void onPacketSend(final PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PING || event.getPacketType() == PacketType.Play.Server.WINDOW_CONFIRMATION) {
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
            if (user != null) {
                user.getConnectionContainer().handleout(event);
            }
        }
    }
}
