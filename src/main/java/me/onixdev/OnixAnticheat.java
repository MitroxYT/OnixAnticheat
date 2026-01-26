package me.onixdev;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import dev.onixac.api.OnixAPI;
import dev.onixac.api.events.impl.OnixLoadedEvent;
import dev.onixac.api.events.impl.PlayerOnixEventCall;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import me.onixdev.commands.api.CommandManager;
import me.onixdev.events.bukkit.*;
import me.onixdev.events.packet.*;
import me.onixdev.manager.AnimationManager;
import me.onixdev.manager.CheckManager;
import me.onixdev.manager.PlayerDatamanager;
import me.onixdev.manager.ResetManager;
import me.onixdev.user.OnixUser;
import me.onixdev.util.color.Colorizer;
import me.onixdev.util.color.impl.MiniMessageColor;
import me.onixdev.util.config.ConfigManager;
import me.onixdev.util.thread.api.IThreadExecutor;
import me.onixdev.util.thread.impl.*;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

import java.nio.file.Files;

public class OnixAnticheat {
    public static OnixAnticheat INSTANCE = new OnixAnticheat();
    private OnixPlugin plugin;
    @Getter
    private IThreadExecutor alertExecutor, reloadExecuter, taskExecutor;
    private IThreadExecutor PacketProccesor, cloudCheckExecuter;
    public AnimationManager getAnimationManager() {return animationManager;}
    private AnimationManager animationManager;
    private ResetManager resetManager;
    private PlayerDatamanager playerDatamanager;
    private int ticksFromStart;
    private ConfigManager configManager;
    private Colorizer colorizer = new MiniMessageColor();

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public static boolean noSupportComponentMessage = false; //PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_16_5);

    @SuppressWarnings("UnstableApiUsage")
    public void onLoad(OnixPlugin plugin) {
        this.plugin = plugin;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this.plugin));
        PacketEvents.getAPI().getSettings().kickOnPacketException(true);
        PacketEvents.getAPI().getSettings().checkForUpdates(false);
        PacketEvents.getAPI().load();
    }

    public void onEnable() {
        try {
            Class.forName("com.viaversion.viabackwards.ViaBackwards");
            plugin.getLogger().info("Обнаружен Via Backwards Включаю поддержку");
            System.setProperty("com.viaversion.handlePingsAsInvAcknowledgements", "true");
        } catch (ClassNotFoundException ignored) {}
        reloadExecuter = new ReloadTaskExecutor();
        alertExecutor = new AlertTaskExecutor();
        taskExecutor = new TaskExecutor();
        PacketProccesor = new PacketProccesingExecuter();
        cloudCheckExecuter = new CloudCheckExecuter();
        playerDatamanager = new PlayerDatamanager();
        configManager = new ConfigManager(true);
        animationManager = new AnimationManager();
        resetManager = new ResetManager();
        animationManager.init();
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
            OnixAPI.INSTANCE.setCommandManager(handler);
        }
        OnixAPI.INSTANCE.loadCorrectly();
        // Запускаем евент спустя 5 секунд после лоада на случай если не все плагины с апи загрузились
        OnixAnticheat.INSTANCE.getPlugin().getServer().getScheduler().runTaskLater(OnixAnticheat.INSTANCE.getPlugin(), () -> {
            Bukkit.getPluginManager().callEvent(new OnixLoadedEvent());
        },20*5);
        resetManager.start();
    }
    public void reload() {
        OnixAnticheat.INSTANCE.getPlugin().getServer().getScheduler().runTaskLater(OnixAnticheat.INSTANCE.getPlugin(), () -> {
            Bukkit.getPluginManager().callEvent(new OnixLoadedEvent());
        },20*5);
    }

    public void onDisable() {
        playerDatamanager.getAllData().clear();
        alertExecutor.shutdown();
        reloadExecuter.shutdown();
        taskExecutor.shutdown();
        PacketProccesor.shutdown();
        cloudCheckExecuter.shutdown();
        resetManager.stop();
        Bukkit.getScheduler().cancelTasks(plugin);
        PacketEvents.getAPI().terminate();
    }

    private void runShedulers() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::tick, 0, 1L);
    }

    private void registerBukkitEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerClickListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerBlockListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerAttackHandler(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerReleaseUseItemState(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), plugin);
    }

    private void registerPacketEvents() {
        PacketEvents.getAPI().getEventManager().registerListeners(new JoinListener());
        PacketEvents.getAPI().getEventManager().registerListeners(new PositionListener());
        PacketEvents.getAPI().getEventManager().registerListeners(new ActionListener());
        PacketEvents.getAPI().getEventManager().registerListeners(new PlayerUsingItemStatehandler());
        PacketEvents.getAPI().getEventManager().registerListeners(new PlayerConnectionHandler());
        PacketEvents.getAPI().getEventManager().registerListeners(new PlayerFoodHealthHandler());
        PacketEvents.getAPI().getEventManager().registerListeners(new PlayerServerCooldownHandler());
        PacketEvents.getAPI().init();
        noSupportComponentMessage = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_16_5);
    }

    private void tick() {
        ticksFromStart++;
        playerDatamanager.getAllData().forEach(OnixUser::tick);
    }

    public OnixPlugin getPlugin() {
        return plugin;
    }

    public IThreadExecutor getPacketProccesor() {
        return PacketProccesor;
    }

    public int getTicksFromStart() {
        return ticksFromStart;
    }

    public PlayerDatamanager getPlayerDatamanager() {
        return this.playerDatamanager;
    }

    public IThreadExecutor getCloudCheckExecuter() {
        return cloudCheckExecuter;
    }
    public void setColorizer(Colorizer colorizer) {this.colorizer = colorizer;}
    public Colorizer getColorizer() {return colorizer;}
}
