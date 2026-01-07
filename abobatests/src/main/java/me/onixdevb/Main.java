package me.onixdevb;


import dev.onixac.api.OnixAPI;
import dev.onixac.api.check.ICheck;
import dev.onixac.api.check.custom.CheckMaker;
import dev.onixac.api.events.impl.PlayerOnixEventCall;
import dev.onixac.api.user.IOnixUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Main extends JavaPlugin implements Listener {
    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        // Plugin startup logic

    }
    @EventHandler
    public void onOnixEvent(PlayerOnixEventCall eventCall) {}
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        IOnixUser user = OnixAPI.INSTANCE.getPlayerDataManager().getUser(uuid);
        /*пока вместо класса будет null  позже это удалю данный парамерт*/
        user.registerCheck(CheckMaker.create().setCheckName("Test").setType("A").build());
        ICheck check = user.getCheck("Test","A");
        if (check != null) {
            check.fail("aaaa tests");
            user.sendMessage("AAAAAA API WORKAET");
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}