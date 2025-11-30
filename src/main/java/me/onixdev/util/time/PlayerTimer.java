package me.onixdev.util.time;


import me.onixdev.user.OnixUser;

public class PlayerTimer {
    private OnixUser player;
    public int startTime;
    public int resetStreak;

    public PlayerTimer(OnixUser player) {
        this.player = player;
        this.reset();
    }

    public boolean wasReset() {
        return this.startTime == player.currentTick;
    }

    public boolean wasNotReset() {
        return this.startTime != player.currentTick;
    }

    public void reset() {
        if (getPassed() == 1) resetStreak++;
        else resetStreak = 0;
        this.startTime = player.currentTick;
    }

    public int getResetStreak() {
        return wasNotReset() ? 0 : resetStreak;
    }

    public long getPassed() {
        return player.currentTick - this.startTime;
    }

    public void add(int amount) {
        this.startTime -= amount;
    }

    public boolean hasPassed(long toPass) {
        return this.getPassed() >= toPass;
    }

    public boolean hasNotPassed(long toPass) {
        return this.getPassed() < toPass;
    }

    public boolean hasPassed(long toPass, boolean reset) {
        boolean passed = this.getPassed() >= toPass;
        if (passed && reset) reset();
        return passed;
    }

    public static boolean hasPassed(long startTime, long toPass) {
        return (System.currentTimeMillis() - startTime) >= toPass;
    }
}
