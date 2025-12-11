package me.onixdev;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import dev.onixac.api.OnixAPI;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import me.onixdev.commands.api.CommandManager;
import me.onixdev.events.bukkit.PlayerClickListener;
import me.onixdev.events.bukkit.PlayerMoveListener;
import me.onixdev.events.bukkit.PlayerReleaseUseItemState;
import me.onixdev.events.packet.*;
import me.onixdev.manager.CheckManager;
import me.onixdev.manager.PlayerDatamanager;
import me.onixdev.user.OnixUser;
import me.onixdev.util.config.ConfigManager;
import me.onixdev.util.thread.api.IThreadExecutor;
import me.onixdev.util.thread.impl.AlertTaskExecutor;
import me.onixdev.util.thread.impl.ReloadTaskExecutor;
import me.onixdev.util.thread.impl.TaskExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class OnixAnticheat {
    public static OnixAnticheat INSTANCE = new OnixAnticheat();
    private OnixPlugin plugin;
    @Getter
    private IThreadExecutor alertExecutor,reloadExecuter,taskExecutor;
    @Getter
    private PlayerDatamanager playerDatamanager;
    @Getter
    private ConfigManager configManager;
    public static boolean noSupportComponentMessage =false; //PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_16_5);
    public void onLoad(OnixPlugin plugin) {
        this.plugin = plugin;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this.plugin));
        PacketEvents.getAPI().load();
    }
    public void onEnable() {
        reloadExecuter = new ReloadTaskExecutor();
        alertExecutor = new AlertTaskExecutor();
        taskExecutor = new TaskExecutor();
        playerDatamanager = new PlayerDatamanager();
        configManager = new ConfigManager(true);
        CheckManager.setup();
        OnixAPI.INSTANCE.setPlayerDataManager(playerDatamanager);
        registerPacketEvents();
        registerBukkitEvents();
        runShedulers();
        PluginCommand pCommand = plugin.getCommand("onix");
        if (pCommand != null) {
            CommandManager handler = new CommandManager();
            pCommand.setExecutor(handler);
            pCommand.setTabCompleter(handler);
        }
    }
    public void onDisable() {
        playerDatamanager.getAllData().clear();
        alertExecutor.shutdown();
        reloadExecuter.shutdown();
        taskExecutor.shutdown();
        Bukkit.getScheduler().cancelTasks(plugin);
    }
    private void runShedulers() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,this::tick,0,1L);
    }
    private void registerBukkitEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerClickListener(),plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerReleaseUseItemState(),plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(),plugin);
    }
    private void registerPacketEvents() {
        PacketEvents.getAPI().getEventManager().registerListeners(new JoinListener());
        PacketEvents.getAPI().getEventManager().registerListeners(new PositionListener());
        PacketEvents.getAPI().getEventManager().registerListeners(new ActionListener());
        PacketEvents.getAPI().getEventManager().registerListeners(new PlayerUsingItemStatehandler());
        PacketEvents.getAPI().getEventManager().registerListeners(new PlayerConnectionHandler());
        PacketEvents.getAPI().init();
       noSupportComponentMessage= PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_16_5);
    }
    private void tick() {
        playerDatamanager.getAllData().forEach(OnixUser::tick);
    }

    public OnixPlugin getPlugin() {
        return plugin;
    }
}
