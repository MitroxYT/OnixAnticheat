package me.onixdev.user.data;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import lombok.Getter;
import me.onixdev.OnixAnticheat;
import me.onixdev.user.OnixUser;
import org.bukkit.Location;
import org.bukkit.Material;
@Getter
public class BrigingContainer {
    private final OnixUser user;
    private int BrigeTicks,lastPlaceTick;
    private boolean brige;
    public BrigingContainer(OnixUser user) {
        this.user = user;
    }
    public void handlePacket(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            ++lastPlaceTick;
            ++BrigeTicks;

            if (user.getBukkitPlayer() != null) {
                OnixAnticheat.INSTANCE.getTaskExecutor().run(()-> {
                    Location loc = user.getBukkitPlayer().getLocation().clone().subtract(0.0, 2.0, 0.0);
                    if (loc.getBlock().getType() == Material.AIR && user.getBukkitPlayer().getInventory().getItemInHand().getType().isBlock()) {
                        if (lastPlaceTick < 20) {
                            BrigeTicks = 0;
                            brige = true;
                        }
                    }
                    else {
                        brige = false;
                    }
                });

            }
        }
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            lastPlaceTick = 0;
        }
    }
    public boolean isBrige() {
        return brige && BrigeTicks < 20 && lastPlaceTick < 7;
    }
}
