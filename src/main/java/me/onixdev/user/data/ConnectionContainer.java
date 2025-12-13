package me.onixdev.user.data;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPong;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import lombok.Getter;
import me.onixdev.check.impl.player.badpackets.BadPacketC;
import me.onixdev.event.impl.TickEvent;
import me.onixdev.user.OnixUser;
import me.onixdev.util.math.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionContainer {
    private final OnixUser user;
    private final boolean ping = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17_1);
    private long transactionPing,transactionPingMs;
    public long lastTransSent = 0;
    public long lastTransReceived = 0;

    public ConnectionContainer(OnixUser user) {
        this.user = user;
    }
    @Getter
    public long playerClockAtLeast = System.nanoTime();
    public long lastPlayerClockAtLeast = System.nanoTime();
    public final Queue<Pair<Short, Long>> transactionsSent = new ConcurrentLinkedQueue<>();
    public final Set<Short> didWeSendThatTrans = ConcurrentHashMap.newKeySet();
    private final AtomicInteger transactionIDCounter = new AtomicInteger(0);
    public final AtomicInteger lastTransactionSent = new AtomicInteger(0);
    public final AtomicInteger lastTransactionReceived = new AtomicInteger(0);



    public void handlein(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            WrapperPlayClientWindowConfirmation transaction = new WrapperPlayClientWindowConfirmation(event);
            short id = transaction.getActionId();

            if (id >= 0 && TransactionResponse(id)) ;
        }

        if (event.getPacketType() == PacketType.Play.Client.PONG) {
            WrapperPlayClientPong pong = new WrapperPlayClientPong(event);


            int id = pong.getId();
            if (id == (short) id) {
                short shortID = ((short) id);
                if (TransactionResponse(shortID));
            }
        }

    }

    public void handleout(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_CONFIRMATION) {
            WrapperPlayServerWindowConfirmation confirmation = new WrapperPlayServerWindowConfirmation(event);
            short id = confirmation.getActionId();
            if (id >= 0) {
                if (didWeSendThatTrans.remove(id)) {
                    transactionsSent.add(new Pair<>(id, System.nanoTime()));
                    lastTransactionSent.getAndIncrement();
                }
            }
        }

        if (event.getPacketType() == PacketType.Play.Server.PING) {
            WrapperPlayServerPing pong = new WrapperPlayServerPing(event);
            int id = pong.getId();
            if (id == (short) id) {
                Short shortID = ((short) id);
                if (didWeSendThatTrans.remove(shortID)) {
                    transactionsSent.add(new Pair<>(shortID, System.nanoTime()));
                    lastTransactionSent.getAndIncrement();
                }
            }
        }
    }
    public void sendTransaction() {
        sendTransaction(false);
    }

    public void sendTransaction(boolean async) {

        if ((System.nanoTime() - getPlayerClockAtLeast()) > 15e9) {
            return;
        }

        lastTransSent = System.currentTimeMillis();
        short transactionID = (short) ((transactionIDCounter.getAndIncrement() & 0x7FFF));
        try {

            PacketWrapper<?> packet;
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17)) {
                packet = new WrapperPlayServerPing(transactionID);
            } else {
                packet = new WrapperPlayServerWindowConfirmation((byte) 0, transactionID, false);
            }

            if (async) {
                ChannelHelper.runInEventLoop(user.getUser().getChannel(), () -> {
                    addTransactionSend(transactionID);
                    user.getUser().writePacket(packet);
                });
            } else {
                addTransactionSend(transactionID);
                user.getUser().writePacket(packet);
            }
        } catch (Exception ignored) {
        }
    }

    public void addTransactionSend(short id) {
        didWeSendThatTrans.add(id);
    }

    public boolean TransactionResponse(short id) {
        Pair<Short, Long> data = null;
        boolean hasID = false;
        int skipped = 0;
        for (Pair<Short, Long> iterator : transactionsSent) {
            if (iterator.getX() == id) {
                hasID = true;
                break;
            }
            skipped++;
        }

        if (hasID) {
            if (skipped >  0 && user.getServerTickSinceJoin() > 20) user.getCheck(BadPacketC.class).fail("invalid: " + skipped);

            do {
                data = transactionsSent.poll();
                if (skipped == 0) user.handleEvent(new TickEvent(TickEvent.Target.TRANSACTION));
                if (data == null)
                    break;
                lastTransactionReceived.incrementAndGet();
                lastTransReceived = System.currentTimeMillis();
                transactionPing = (System.nanoTime() - data.getY());
                lastPlayerClockAtLeast = playerClockAtLeast;
                playerClockAtLeast = data.getY();
                transactionPingMs = (playerClockAtLeast - lastPlayerClockAtLeast) / 1000000L;
            } while (data.getX() != id);

        }

        return data != null;
    }

}
