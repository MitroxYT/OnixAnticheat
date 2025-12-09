package me.onixdev.manager;

import com.github.retrooper.packetevents.protocol.player.User;
import dev.onixac.api.manager.IPlayerDataManager;
import dev.onixac.api.user.IOnixUser;
import me.onixdev.user.OnixUser;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDatamanager implements IPlayerDataManager {
    private ConcurrentHashMap<UUID, OnixUser> data = new ConcurrentHashMap<>();
    public void add(User user) {
        data.put(user.getUUID(),new OnixUser(user));
    }
    public OnixUser get(UUID uuid) {
        return data.get(uuid);
    }
    public OnixUser get(User user) {
        return data.get(user.getUUID());
    }
    public void remove(UUID uuid) {
        data.remove(uuid);
    }
    public Collection<OnixUser> getAllData() {
        return data.values();
    }

    @Override
    public IOnixUser getUser(UUID uuid) {
        return data.get(uuid);
    }
}
