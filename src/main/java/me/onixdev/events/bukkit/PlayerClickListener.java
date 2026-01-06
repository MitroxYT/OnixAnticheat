package me.onixdev.events.bukkit;

import me.onixdev.OnixAnticheat;
import me.onixdev.event.impl.PlayerClickEvent;
import me.onixdev.user.OnixUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.UUID;

public class PlayerClickListener implements Listener {
    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            UUID uuid = player.getUniqueId();
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(uuid);
            if (user == null) {
                return;
            }
            try {
                if (event.getClickedInventory() == null) return;
                PlayerClickEvent clickEvent = new PlayerClickEvent(event.getSlotType(), event.getSlot(), event.getClick(), event.getAction(), event.getCurrentItem(), event.getClickedInventory().getType() == InventoryType.PLAYER);
                user.handleEvent(clickEvent);
                if (clickEvent.isCancelled()) event.setCancelled(true);
            } catch (NullPointerException ignored){}
        }
    }
}
