package me.onixdev.check.impl.movement.tickmanipulation;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import dev.onixac.api.check.CheckInfo;
import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.user.OnixUser;
import me.onixdev.util.net.PacketUtil;

@CheckInfo(name = "TickBase",type = "A",stage = CheckStage.EXPERIMENTAL)
public class TickBaseA extends Check {
    protected long timerBalanceRealTime = 0;

    long knownPlayerClockTime = (long) (System.nanoTime() - 6e10);
    protected long lastMovementPlayerClock = (long) (System.nanoTime() - 6e10);
    protected long clockDrift;
    private long limitAbuseOverPing;

    private boolean hasGottenMovementAfterTransaction = false;

    public TickBaseA(OnixUser player) {
        super(player);
    }

    @Override
    public void onPacketIn(PacketReceiveEvent event) {
        if (hasGottenMovementAfterTransaction && PacketUtil.INSTANCE.isTransactionOnixBased(event)) {
            knownPlayerClockTime = lastMovementPlayerClock;
            lastMovementPlayerClock = player.getConnectionContainer().getPlayerClockAtLeast();
            hasGottenMovementAfterTransaction = false;
        }

        if (!shouldCountPacketForTimer(event.getPacketType())) return;

        hasGottenMovementAfterTransaction = true;
        timerBalanceRealTime += (long) 50e6;

        doCheck(event);
    }
    public void doCheck(final PacketReceiveEvent event) {
        if (timerBalanceRealTime > System.nanoTime()) {
            int lostMS = (int) Math.abs((System.nanoTime() - timerBalanceRealTime) / 1e6);
            String info = "skipped ticking (" + lostMS + " ms)";
            if (failAndSetback(info)) {
                if (shouldCancel()) {
                    event.setCancelled(true);
                }
            }
            timerBalanceRealTime -= (long) 50e6;
        }

        long playerClock = lastMovementPlayerClock;
        if (System.nanoTime() - playerClock > limitAbuseOverPing) {
            playerClock = System.nanoTime() - limitAbuseOverPing;
        }
        timerBalanceRealTime = Math.max(timerBalanceRealTime, playerClock - clockDrift);
    }

    public boolean shouldCountPacketForTimer(PacketTypeCommon packetType) {
        return isTickPacket(packetType);
    }

    @Override
    public void reload() {
        super.reload();
        clockDrift = (long) (getCheckConfig().getDouble(getCheckPatch() + "drift", 120.0) * 1e6);
        limitAbuseOverPing = (long) (getCheckConfig().getDouble(getCheckPatch() + "ping-abuse-limit-threshold", 1000) * 1e6);
    }
}
