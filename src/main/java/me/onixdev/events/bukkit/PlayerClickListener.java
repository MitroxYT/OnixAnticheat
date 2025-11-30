package me.onixdev.events.bukkit;

import me.onixdev.OnixAnticheat;
import me.onixdev.event.impl.PlayerClickEvent;
import me.onixdev.user.OnixUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class PlayerClickListener implements Listener {
    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            UUID uuid = player.getUniqueId();
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(uuid);
            if (user == null){
                return;
            }
            PlayerClickEvent clickEvent = new PlayerClickEvent(event.getSlotType(),event.getSlot(),event.getClick(),event.getAction());
            user.handleEvent(clickEvent);
            if (clickEvent.isCancelled()) event.setCancelled(true);
        }
    }
}
