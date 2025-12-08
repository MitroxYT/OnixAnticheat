package me.onixdev.user.data;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPong;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import lombok.Getter;
import me.onixdev.event.impl.TickEvent;
import me.onixdev.user.OnixUser;
import me.onixdev.util.net.LagTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionContainer {
    private final OnixUser user;
    private final boolean ping = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17_1);
    public ConnectionContainer(OnixUser user) {
        this.user = user;
    }
    private int transaction = ThreadLocalRandom.current().nextInt(150,900);
    @Getter
    private final AtomicInteger transSent = new AtomicInteger(0);
    @Getter
    private final AtomicInteger transReceived = new AtomicInteger(0);
    private final List<LagTask> transTasks = new ArrayList<>();
    private long lastSent, lastReceived;

    @Getter
    private final Deque<Long> transSentTimes = new ArrayDeque<>();

    public void sendTransaction(boolean runInEventLoop) {
        if (user.getUser().getDecoderState() != ConnectionState.PLAY) return;
        if (runInEventLoop) {
            runInEventLoop(() -> user.getUser().writePacket(ping ? new WrapperPlayServerPing(transaction) :new WrapperPlayServerWindowConfirmation(0, (short) transaction, false)));
        } else {
            user.getUser().writePacket(ping ? new WrapperPlayServerPing(transaction) :new WrapperPlayServerWindowConfirmation(0, (short) transaction, false));
        }
    }


    public void runInEventLoop(Runnable runnable) {
        ChannelHelper.runInEventLoop(user.getUser().getChannel(),runnable);
    }



    public void handleOut(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_CONFIRMATION) {
            handleServerTransaction(new WrapperPlayServerWindowConfirmation(event));
            lastSent = System.currentTimeMillis();
        }
        if (event.getPacketType() == PacketType.Play.Server.PING) {
            handleServerTransaction(new WrapperPlayServerPing(event));
            lastSent = System.currentTimeMillis();
        }
    }

    public void handleIn(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            Long lastSentTime = transSentTimes.peek();
        }

        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            handleClientTransaction(new WrapperPlayClientWindowConfirmation(event));
            lastReceived = System.currentTimeMillis();
        }
        if (event.getPacketType() == PacketType.Play.Client.PONG) {
            handleClientTransaction(new WrapperPlayClientPong(event));
            lastReceived = System.currentTimeMillis();
        }
    }

    public void sendTransaction() {
        this.sendTransaction(false);
    }
    public void handleServerTransaction(WrapperPlayServerPing wrapper) {
        if (wrapper.getId() != transaction) {
            return;
        }
        transSent.incrementAndGet();
        transSentTimes.add(System.currentTimeMillis());
        user.debug("srvping");
    }

    public void handleServerTransaction(WrapperPlayServerWindowConfirmation wrapper) {
        if (wrapper.getActionId() != transaction) {
            return;
        }
        transSent.incrementAndGet();
        transSentTimes.add(System.currentTimeMillis());
    }
    public void handleClientTransaction(WrapperPlayClientPong wrapper) {
        if (wrapper.getId() != transaction) {
            return;
        }

        Long sentTime = transSentTimes.poll();
        if (sentTime != null) {
            //long responseTime = System.currentTimeMillis() - sentTime;
        }


        int currentTrans = transReceived.incrementAndGet();
        if (!transTasks.isEmpty()) {
            for (LagTask task : transTasks) {
                if (task.getTransaction() == currentTrans) {
                    task.getTask().run();
                    transTasks.remove(task);
                }
            }
        }
        user.handleEvent(new TickEvent(TickEvent.Target.TRANSACTION));
    }
    public void handleClientTransaction(WrapperPlayClientWindowConfirmation wrapper) {
        if (wrapper.getActionId() != transaction) {
            return;
        }

        Long sentTime = transSentTimes.poll();
        int currentTrans = transReceived.incrementAndGet();
        if (!transTasks.isEmpty()) {
            for (LagTask task : transTasks) {
                if (task.getTransaction() == currentTrans) {
                    task.getTask().run();
                    transTasks.remove(task);
                }
            }
        }
        user.handleEvent(new TickEvent(TickEvent.Target.TRANSACTION));
    }

    public void scheduleTrans(int offset, Runnable runnable) {
        int scheduledTrans = transSent.get() + offset;

        if (transReceived.get() >= scheduledTrans) {
            runnable.run();
            return;
        }

        transTasks.add(new LagTask(scheduledTrans, runnable));
    }

    public void confirmPre(Runnable runnable) {
        this.scheduleTrans(0, runnable);
    }
    public void confirmPost(Runnable runnable) {
        this.scheduleTrans(1, runnable);
    }

    public int getLastTransactionSent() {
        return transSent.get();
    }

}
