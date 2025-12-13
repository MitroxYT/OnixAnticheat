package me.onixdev.events.packet;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
import me.onixdev.user.OnixUser;
import me.onixdev.util.net.KickTypes;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerConnectionHandler extends PacketListenerAbstract {
    public PlayerConnectionHandler() {
        super(PacketListenerPriority.LOWEST);
    }
    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        try {
           // OnixAnticheat.INSTANCE.getPacketProccesor().run(() -> {
                OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
                if (user != null) {
                    for (Check check : user.getChecks()) check.onPacketIn(event);
                }
           // });
        } catch (Exception e) {
            OnixAnticheat.INSTANCE.getPlugin().getLogger().warning("При обработке пакета: " + event.getPacketType().getName() + " для игрока: " + event.getUser().getName() + " error: " + e.getMessage());
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
            if (user != null) {
                user.disconnect(KickTypes.InvalidPacket,"<lang:disconnect.timeout>");
            }
            else {
                event.setCancelled(true);
                event.getUser().sendPacket(new WrapperPlayServerDisconnect(MiniMessage.miniMessage().deserialize("<red>Invalid Packet Received").compact()));
            }
      //      e.printStackTrace();
        }
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
