package dev.onixac.api.manager;

import dev.onixac.api.user.IOnixUser;

import java.util.UUID;

public interface IPlayerDataManager {
    IOnixUser getUser(UUID uuid);
}
