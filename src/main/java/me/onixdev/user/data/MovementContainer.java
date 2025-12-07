package me.onixdev.user.data;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import lombok.Getter;
import lombok.Setter;
import me.onixdev.user.OnixUser;
import me.onixdev.util.math.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MovementContainer {
    private OnixUser user;
    public double x;
    public double y;
    public double z;
    public double lastx;
    public double lasty;
    public double lastz;
    private double blockx, blocky, blockz;
    @Setter
    private int setbackticks;
    @Getter
    private double deltaX;
    @Getter
    private double deltaY;
    @Getter
    private double deltaZ;
    private double deltaXZ;
    @Getter
    @Setter
    private double calcylatedDeltaXZ;
    @Getter
    @Setter
    private int iceTiks;
    @Getter
    private double lastDeltaX;
    @Getter
    private double lastDeltaZ;
    @Getter
    private double lastDeltaY;
    private double lastDeltaXZ;
    private double lastlastz;
    private double lastlasty;
    private double lastlastx;
    private boolean clientOnGround;
    private boolean lastClientOnGround;
    private boolean lastLastClientGround;

    public MovementContainer(OnixUser user) {
        this.user = user;
    }
    public void handleFlying(final PacketReceiveEvent event, final WrapperPlayClientPlayerFlying wrapper) {
        if (this.user.getBukkitPlayer() != null) {
            this.setLastLastClientGround(this.lastClientOnGround);
            this.lastClientOnGround = this.clientOnGround;
            this.clientOnGround = wrapper.isOnGround();

            if (wrapper.hasPositionChanged()) {
                this.lastlastx = this.lastx;
                this.lastlasty = this.lasty;
                this.lastlastz = this.lastz;
                this.lastx = this.x;
                this.lasty = this.y;
                this.lastz = this.z;

                this.x = wrapper.getLocation().getX();
                this.y = wrapper.getLocation().getY();
                this.z = wrapper.getLocation().getZ();
                this.lastDeltaY = this.deltaY;
                this.lastDeltaX = this.deltaX;
                this.lastDeltaZ = this.deltaZ;
                this.deltaX = this.x - this.lastx;
                this.deltaY = this.y - this.lasty;
                this.deltaZ = this.z - this.lastz;

                this.lastDeltaXZ = this.deltaXZ;
                this.deltaXZ = MathUtil.hypot(this.deltaX, this.deltaZ);
            }
        }
    }

    public boolean isLastLastClientGround() {
        return lastLastClientGround;
    }

    public void setLastLastClientGround(boolean lastLastClientGround) {
        this.lastLastClientGround = lastLastClientGround;
    }
}
