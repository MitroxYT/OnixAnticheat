package me.onixdev.events.packet;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPickItem;
import me.onixdev.OnixAnticheat;
import me.onixdev.event.impl.PlayerCloseInventoryEvent;
import me.onixdev.event.impl.PlayerPacketClickEvent;
import me.onixdev.event.impl.PlayerPickEvent;
import me.onixdev.user.OnixUser;

public class ActionListener extends PacketListenerAbstract {
    public ActionListener() {
        super(PacketListenerPriority.HIGHEST); //нужно чтобы между Bukkit почти не было задержки
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapperPlayClientClickWindow = new WrapperPlayClientClickWindow(event);
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser().getUUID());
            if (user != null) {
                PlayerPacketClickEvent event1 = new PlayerPacketClickEvent(wrapperPlayClientClickWindow.getWindowId(),wrapperPlayClientClickWindow.getWindowClickType(),wrapperPlayClientClickWindow.getCarriedItemStack());
                user.handleEvent(event1);
                if (event1.isCancelled()) {
                    event.setCancelled(true);
                }
            }
        }
        if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            WrapperPlayClientCloseWindow wrapperPlayClientCloseWindow = new WrapperPlayClientCloseWindow(event);
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser().getUUID());
            if (user != null) {
                PlayerCloseInventoryEvent event1 = new PlayerCloseInventoryEvent(wrapperPlayClientCloseWindow.getWindowId());
                user.handleEvent(event1);
                if (event1.isCancelled()) {
                    event.setCancelled(true);
                }
            }
        }
        if (event.getPacketType() == PacketType.Play.Client.PICK_ITEM) {
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser().getUUID());
            if (user != null) {
                PlayerPickEvent event1 = new PlayerPickEvent();
                user.handleEvent(event1);
                if (event1.isCancelled()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
