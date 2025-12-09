package me.onixdev.event.impl;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import lombok.Getter;
import dev.onixac.api.events.api.BaseEvent;

@Getter
public class PlayerPacketClickEvent extends BaseEvent {
    private int id;
    private WrapperPlayClientClickWindow.WindowClickType clickType;
    private ItemStack item;
    public PlayerPacketClickEvent(final int id, WrapperPlayClientClickWindow.WindowClickType clickType, ItemStack item) {
        this.id = id;
        this.clickType = clickType;
        this.item = item;
    }

    public boolean isClient() {
        return id == 0;
    }
}
