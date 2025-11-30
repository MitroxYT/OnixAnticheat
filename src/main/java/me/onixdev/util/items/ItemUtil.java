package me.onixdev.util.items;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class ItemUtil {
    private boolean support19 = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9);
    public ItemStack fromUsingHand(InteractionHand hand, Player player) {
        switch (hand) {
            case MAIN_HAND -> {
                return player.getInventory().getItemInMainHand();
            }
            case OFF_HAND -> {
                if (support19) return player.getInventory().getItemInOffHand();
                else return player.getInventory().getItemInMainHand();
            }

        }
        return null;
    }
}
