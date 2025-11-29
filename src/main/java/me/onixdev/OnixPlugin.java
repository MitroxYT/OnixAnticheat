package me.onixdev;

import org.bukkit.plugin.java.JavaPlugin;

public final class OnixPlugin extends JavaPlugin {
    @Override
    public void onLoad() {
        OnixAnticheat.INSTANCE.onLoad(this);
    }

    @Override
    public void onEnable() {
        OnixAnticheat.INSTANCE.onEnable();
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        OnixAnticheat.INSTANCE.onDisable();
        // Plugin shutdown logic
    }
}
