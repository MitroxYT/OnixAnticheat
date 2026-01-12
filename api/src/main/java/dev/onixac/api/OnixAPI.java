package dev.onixac.api;

import dev.onixac.api.manager.ICommandManager;
import dev.onixac.api.manager.IPlayerDataManager;

public class OnixAPI {
    public static OnixAPI INSTANCE = new OnixAPI();
    private IPlayerDataManager playerDataManager;
    private ICommandManager commandManager;
    private boolean loaded;
    private OnixAPI() {loaded = false;}

    public void loadCorrectly(){loaded = true;}

    public ICommandManager getCommandManager() {if (!loaded) throw new RuntimeException("Onix Anticheat no Loaded");return commandManager;}

    public IPlayerDataManager getPlayerDataManager() {if (!loaded) throw new RuntimeException("Onix Anticheat not Loaded");return playerDataManager;}
    public void setPlayerDataManager(IPlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public void setCommandManager(ICommandManager commandManager) {
        this.commandManager = commandManager;
    }
}
