package me.onixdev.events.bukkit;

import me.onixdev.OnixAnticheat;
import me.onixdev.user.OnixUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.UUID;

public class PlayerReleaseUseItemState implements Listener {
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(uuid);
        if (user == null){
            return;
        }
        user.setUsingItem(false);
    }
}
