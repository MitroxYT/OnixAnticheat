package me.onixdev.events.bukkit;

import me.onixdev.OnixAnticheat;
import me.onixdev.check.impl.player.inventory.InventoryD;
import me.onixdev.event.impl.PlayerClickEvent;
import me.onixdev.user.OnixUser;
import me.onixdev.util.math.MathUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.UUID;

public class PlayerClickListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onClickEvent(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player p) {
            UUID uuid = p.getUniqueId();
            OnixUser player = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(uuid);
            if (player == null) return;
            final InventoryD inv = player.getCheck(InventoryD.class);

            if (p.getGameMode().equals(GameMode.CREATIVE)) return;
            if (e.getClick().isKeyboardClick()) return;
            if (e.getCurrentItem() == null) return;

            if (inv.lastClickedItemStack.getType().equals(e.getCurrentItem().getType()) && e.getClick().isShiftClick()) {
                return;
            }

            final int slot = e.getSlot();
            final int lastSlot = inv.lastClickedSlot;
            final double distance = MathUtil.distanceBetween(slot, lastSlot);
            final double min_time = distance * 40.0;
            final long time = System.currentTimeMillis() - inv.lastClickInv;
            final int vl = (time < min_time) ? ((time < min_time / 2.0) ? 8 : 4) : 0;
            final int itemStealerVL = inv.itemStealerVL + vl;
            inv.itemStealerVL = itemStealerVL;
            if (vl < 2 && itemStealerVL > 2) {
                inv.itemStealerVL -= ((time * 2L > min_time) ? 2 : 1);
            }

            final double speedAttr = time / distance;
            if (itemStealerVL > 4 && vl > 2 && time > 0L) {
                inv.fail("type=fast, " + (int) speedAttr + " ms/slot");
            }
            inv.lastClickedSlot = slot;
            inv.lastClickInv = System.currentTimeMillis();
            inv.lastClickedItemStack = e.getCurrentItem();
        }
    }

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
            } catch (NullPointerException ignored) {
            }
        }
    }
}
