package me.onixdev.events.bukkit;

import me.onixdev.OnixAnticheat;
import me.onixdev.user.OnixUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerMoveListener implements Listener {
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        OnixAnticheat.INSTANCE.getTaskExecutor().run(() -> {
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(uuid);
            if (user != null) {
                user.getMovementContainer().OnBukkit(event);
            }
        });
    }
}
