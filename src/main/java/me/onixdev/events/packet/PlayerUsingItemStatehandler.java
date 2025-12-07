package me.onixdev.events.packet;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.impl.player.badpackets.BadPacketA;
import me.onixdev.check.impl.player.badpackets.BadPacketB;
import me.onixdev.user.OnixUser;
import me.onixdev.util.items.ItemUtil;
import me.onixdev.util.items.MaterialsUtil;
import me.onixdev.util.net.BukkitNms;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;

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
                    BadPacketB badPacketB = user.getCheck(BadPacketB.class);
                    if (badPacketB != null) {
                        if (!badPacketB.validdate() && user.getBukkitPlayer() != null) {
                            BukkitNms.resetBukkitItemUsage(user.getBukkitPlayer());
                            return;
                        }
                    }
                    user.getConnectionContainer().confirmPre(()-> {
                        user.setUsingHand(useItem.getHand());
                        user.setUsingItem(usable);
                    });
                }
            }
        }
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            WrapperPlayClientHeldItemChange useItem = new WrapperPlayClientHeldItemChange(event);
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
            if (user != null && user.getUsingHand() != InteractionHand.OFF_HAND) {
               user.setUsingItem(false);
            }
        }
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging digging = new WrapperPlayClientPlayerDigging(event);
            if (digging.getAction() == DiggingAction.FINISHED_DIGGING || digging.getAction() == DiggingAction.START_DIGGING || digging.getAction() == DiggingAction.CANCELLED_DIGGING) return;
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(event.getUser());
            if (user != null) {
                BadPacketA validate = user.getCheck(BadPacketA.class);
                if (validate != null && validate.isValid(digging)) {
                    user.setUsingItem(false);
                }
            }
        }
    }
}
