package dev.onixac.api;

import dev.onixac.api.manager.IPlayerDataManager;

public class OnixAPI {
    public static OnixAPI INSTANCE = new OnixAPI();
    private IPlayerDataManager playerDataManager;
    private OnixAPI() {}
    public IPlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    public void setPlayerDataManager(IPlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }
}
