package me.onixdev.events.packet;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
import me.onixdev.OnixAnticheat;
import me.onixdev.user.OnixUser;
import me.onixdev.util.items.ItemUtil;
import me.onixdev.util.items.MaterialsUtil;
import org.bukkit.inventory.ItemStack;

public class PlayerUsingItemStatehandler extends PacketListenerAbstract {
    public PlayerUsingItemStatehandler() {
        super(PacketListenerPriority.LOW);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
            WrapperPlayClientUseItem useItem = new WrapperPlayClientUseItem(event);
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
            if (user != null) {
                ItemStack itemStack = ItemUtil.fromUsingHand(useItem.getHand(), user.getBukkitPlayer());
                if (itemStack != null) {
                    boolean usable = MaterialsUtil.isUsable(itemStack,user.food);
                    user.sendTransaction();
                    user.getConnectionContainer().confirmPre(()-> {
                        user.setUsingHand(useItem.getHand());
                        user.setUsingItem(usable);
                    });
                }
            }
        }
    }
}
