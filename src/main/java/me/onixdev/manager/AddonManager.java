package me.onixdev.manager;

import dev.onixac.api.addons.OnixAddon;
import dev.onixac.api.manager.IAddonsManager;
import me.onixdev.OnixAnticheat;
import me.onixdev.util.net.BukkitNms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddonManager implements IAddonsManager {
    private List<OnixAddon> addons = new ArrayList<>();
    private int count;
    public void init() {
        File addonsFolder = new File(OnixAnticheat.INSTANCE.getPlugin().getDataFolder() + "/addons");
        if (!addonsFolder.exists()) addonsFolder.mkdirs();
        File[] jarFiles = addonsFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jarFiles != null) {
            for (File jarFile : jarFiles) {
                try {
                 Plugin plugin = Bukkit.getPluginManager().loadPlugin(jarFile);
                 Bukkit.getPluginManager().enablePlugin(plugin);
                 OnixAddon addon = new OnixAddon(plugin.getName(), plugin.getDescription().getVersion(), plugin);
                 OnixAnticheat.INSTANCE.getPlugin().getLogger().info("Загружаю: " + addon.getName() + " " + addon.getVersion());
                 addons.add(addon);
                } catch (InvalidPluginException | InvalidDescriptionException e) {
                    throw new RuntimeException(e);
                }
                ++count;
            }
            OnixAnticheat.INSTANCE.getPlugin().getLogger().info("Загружено: " + count+ " Аддонов");
        }
    }
    public void onDisable() {
        if (!addons.isEmpty()) {
            for (OnixAddon addon : addons) {
                if (OnixAnticheat.INSTANCE.getCompatibilityManager().isHasPlugman()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"plugman unload " + addon.getName());
                }else Bukkit.getPluginManager().disablePlugin(addon.getPlugin());
            }
            addons.clear();
        }
    }
    @Override
    public int getAddonsCount() {
        return count;
    }
}
