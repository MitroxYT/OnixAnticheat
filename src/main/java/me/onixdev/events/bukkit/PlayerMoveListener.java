package me.onixdev.events.bukkit;

import me.onixdev.OnixAnticheat;
import me.onixdev.user.OnixUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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
    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        try {
            final OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(player.getUniqueId());
            if (user != null) {
                user.lastTeleportTime = 0;
            }
        } catch (Exception ignored) {
        }
    }
}
