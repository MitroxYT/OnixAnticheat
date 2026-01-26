package dev.onixac.api.addons;

import org.bukkit.plugin.Plugin;

public class OnixAddon {
    private String name,version;
    private Plugin plugin;
    public OnixAddon(String name,String version,Plugin plugin) {
        this.name = name;
        this.version = version;
        this.plugin = plugin;
    }
    public Plugin getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
