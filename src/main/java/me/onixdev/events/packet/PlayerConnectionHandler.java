package me.onixdev.events.packet;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
import me.onixdev.user.OnixUser;
import me.onixdev.util.net.KickTypes;
import me.onixdev.util.net.PacketUtil;
import me.onixdev.util.net.PlayerConnectionStep;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerConnectionHandler extends PacketListenerAbstract {
    public PlayerConnectionHandler() {
        super(PacketListenerPriority.LOWEST);
    }
    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        try {
             OnixAnticheat.INSTANCE.getPacketProccesor().run(() -> {
                OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
                if (user != null) {
                    if (event.getPacketType() == PacketType.Configuration.Client.CONFIGURATION_END_ACK) {
                        user.ServerconnectionStage = PlayerConnectionStep.LOGIN_SUCCESS_ACK;
                        user.connectionStage = PlayerConnectionStep.LOGIN_SUCCESS_ACK;
                    }
                    if (event.getPacketType() == PacketType.Configuration.Client.CLIENT_SETTINGS) {
                        user.ServerconnectionStage = PlayerConnectionStep.CLIENT_SETTINGS;
                        if (user.connectionStage == PlayerConnectionStep.LOGIN_SUCCESS_ACK) user.connectionStage = PlayerConnectionStep.CLIENT_SETTINGS;
                    }
                    if (event.getPacketType() == PacketType.Configuration.Client.SELECT_KNOWN_PACKS) {
                        user.ServerconnectionStage = PlayerConnectionStep.SELECT_KNOWN_PACKS;
                        if (user.connectionStage == PlayerConnectionStep.CLIENT_SETTINGS) user.connectionStage = PlayerConnectionStep.SELECT_KNOWN_PACKS;
                    }
                    if (event.getPacketType() == PacketType.Play.Client.PLAYER_LOADED) {
                        user.ServerconnectionStage = PlayerConnectionStep.PLAYER_LOADED;
                        if (user.connectionStage == PlayerConnectionStep.SELECT_KNOWN_PACKS) user.connectionStage = PlayerConnectionStep.PLAYER_LOADED;
                    }
                    if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
                        user.ServerconnectionStage = PlayerConnectionStep.PLUGIN_MESSAGE;
                        if (user.connectionStage == PlayerConnectionStep.PLAYER_LOADED) user.connectionStage = PlayerConnectionStep.PLUGIN_MESSAGE;
                    }
                    if (event.getPacketType() == PacketType.Play.Client.TELEPORT_CONFIRM) {
                        user.ServerconnectionStage = PlayerConnectionStep.TELEPORT_CONFIRM;
                        if (user.connectionStage == PlayerConnectionStep.PLUGIN_MESSAGE) user.connectionStage = PlayerConnectionStep.TELEPORT_CONFIRM;
                    }
                    if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
                        user.ServerconnectionStage = PlayerConnectionStep.HELD_ITEM_CHANGE;
                        if (user.connectionStage == PlayerConnectionStep.TELEPORT_CONFIRM) user.connectionStage = PlayerConnectionStep.HELD_ITEM_CHANGE;
                    }
                    for (Check check : user.getChecks()) check.onPacketIn(event);
                }
            });
        } catch (Exception e) {
            OnixAnticheat.INSTANCE.getPlugin().getLogger().warning("При обработке пакета: " + event.getPacketType().getName() + " для игрока: " + event.getUser().getName() + " error: " + e.getMessage());
//            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
//            if (user != null) {
//                user.disconnect(KickTypes.InvalidPacket,"<lang:disconnect.timeout>");
//            }
//            else {
//                event.setCancelled(true);
//                event.getUser().sendPacket(new WrapperPlayServerDisconnect(MiniMessage.miniMessage().deserialize("<red>Invalid Packet Received").compact()));
//            }
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
