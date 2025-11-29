package me.onixdev.events.packet;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import me.onixdev.OnixAnticheat;

public class JoinListener extends PacketListenerAbstract {
    public JoinListener() {
        super(PacketListenerPriority.LOWEST);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS) {
            event.getTasksAfterSend().add(() -> OnixAnticheat.INSTANCE.getPlayerDatamanager().add(event.getUser()));
        }
        if (event.getPacketType() == PacketType.Play.Server.DISCONNECT) {
            User user = event.getUser();
            OnixAnticheat.INSTANCE.getPlayerDatamanager().remove(user.getUUID());
        }
    }
}
