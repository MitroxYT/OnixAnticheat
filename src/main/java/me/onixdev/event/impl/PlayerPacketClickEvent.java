package me.onixdev.event.impl;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import lombok.Generated;
import lombok.Getter;
import dev.onixac.api.events.api.BaseEvent;

public class PlayerPacketClickEvent extends BaseEvent {
    private final int id;
    private final WrapperPlayClientClickWindow.WindowClickType clickType;
    private final ItemStack item;
    public PlayerPacketClickEvent(final int id, WrapperPlayClientClickWindow.WindowClickType clickType, ItemStack item) {
        this.id = id;
        this.clickType = clickType;
        this.item = item;
    }

    public boolean isClient() {
        return id == 0;
    }
    public int getId() {
        return this.id;
    }

    public WrapperPlayClientClickWindow.WindowClickType getClickType() {
        return this.clickType;
    }
    
    public ItemStack getItem() {
        return this.item;
    }
}
