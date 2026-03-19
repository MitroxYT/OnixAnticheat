package me.onixdev.messenger.impl;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.onixdev.OnixAnticheat;
import me.onixdev.messenger.util.packets.base.PacketFactory;
import me.onixdev.messenger.util.packets.impl.OnixAlertPacket;
import me.onixdev.user.OnixUser;
import me.onixdev.util.color.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.json.JSONObject;
import org.jspecify.annotations.NonNull;

public class MessageManager implements PluginMessageListener {
    private final String CHANNEL = "onixac:msg";

    public MessageManager() {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(OnixAnticheat.INSTANCE.getPlugin(), CHANNEL);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(OnixAnticheat.INSTANCE.getPlugin(), CHANNEL, this);
    }

    @Override
    public void onPluginMessageReceived(@NonNull String channel, @NonNull Player player, @NonNull byte[] message) {
        if (channel.equals(CHANNEL)) {
            var messager = new String(message);
            var json = new JSONObject(messager);
            var packet = PacketFactory.INSTANCE.getPacket(json);
            if (packet instanceof OnixAlertPacket alertPacket) {
                for (OnixUser user : OnixAnticheat.INSTANCE.getPlayerDatamanager().getAllData()) {
                    if (user.isAlertsEnabled()) {
                        MessageUtil.sendMessage(user.getBukkitPlayer(), MessageUtil.miniMessage(alertPacket.getAlert()));
                    }
                }
            }
        }
    }

    public void sendBungeeMessage(Player sender, String message) {
        ByteArrayDataOutput byteArrayOutputStream = ByteStreams.newDataOutput();
        byteArrayOutputStream.writeUTF(message);
        sender.sendPluginMessage(OnixAnticheat.INSTANCE.getPlugin(), CHANNEL, byteArrayOutputStream.toByteArray());
    }

    public void disable() {
        Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(OnixAnticheat.INSTANCE.getPlugin());
        Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(OnixAnticheat.INSTANCE.getPlugin());
    }
}
