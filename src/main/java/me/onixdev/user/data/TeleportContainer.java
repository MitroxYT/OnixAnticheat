package me.onixdev.user.data;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTeleportConfirm;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import me.onixdev.user.OnixUser;
import me.onixdev.util.math.Pair;
import me.onixdev.util.net.PlayerTeleportTask;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TeleportContainer {
    private OnixUser user;
    private final List<PlayerTeleportTask> tacks = new ArrayList<>();
    public final Queue<Pair<Integer, Long>> teleportConfQueve = new ConcurrentLinkedQueue<>();
    public TeleportContainer(OnixUser user) {
        this.user = user;
    }
    public void handleIn(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.TELEPORT_CONFIRM) {
            WrapperPlayClientTeleportConfirm teleportConfirm = new WrapperPlayClientTeleportConfirm(event);
            int Unconfirmed = 0;
            for (Pair<Integer,Long> steOne : teleportConfQueve) {
                Integer task = steOne.getX();
                ++Unconfirmed;
                if (task == teleportConfirm.getTeleportId()) {
                    for (PlayerTeleportTask tack1: tacks) {
                        if (tack1.getTeleportId() == teleportConfirm.getTeleportId()) {
                            tack1.setConfirmTeleport(true);
                            Unconfirmed=0;
                            break;
                        }
                    }
                }
            }
            if (Unconfirmed > 0) {
                user.sendMessage("invalid action Detected");
            }
        }
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
            com.github.retrooper.packetevents.protocol.world.Location loc = flying.getLocation();
            if (tacks.isEmpty()) return;
            for (PlayerTeleportTask tack1: tacks) {
                double diffX = Math.abs(loc.getX() - tack1.getLocation().getX());
                double diffY = Math.abs(loc.getY() - tack1.getLocation().getY());
                double diffZ = Math.abs(loc.getZ() - tack1.getLocation().getZ());
                double diffYaw = Math.abs(loc.getYaw() - tack1.getLocation().getYaw());
                double diffPitch = Math.abs(loc.getPitch() - tack1.getLocation().getPitch());
//                user.debug("tpid: " + tack1.getTransaction() + " rec: " +  user.getConnectionContainer().getTransReceived().get());
                if (diffX < 0.03 && diffY < 0.03 && diffZ < 0.03 && user.getConnectionContainer().getTransReceived().get() >=tack1.getTransaction()) {
                    user.debug(" diffYaw: " + diffYaw + " diffPitch: " + diffPitch);
                    if (diffYaw > 10 || diffPitch > 10) {
                        user.debug("missed diffYaw: " + diffYaw + " diffPitch: " + diffPitch);
                    }
                    tack1.setConfirmTransaction(true);
                    tacks.remove(tack1);
                }

            }
        }
    }
    public void handleOut(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK) {
            WrapperPlayServerPlayerPositionAndLook teleport = new WrapperPlayServerPlayerPositionAndLook(event);
            int teleportid = teleport.getTeleportId();
            Location teleportloc = new Location(null, teleport.getX(), teleport.getY(),teleport.getZ(), teleport.getYaw(), teleport.getPitch());
            user.sendTransaction();
            int transaction = user.getConnectionContainer().getLastTransactionSent();
            tacks.add(new PlayerTeleportTask(transaction,teleportid,teleportloc));
        }
    }
}
