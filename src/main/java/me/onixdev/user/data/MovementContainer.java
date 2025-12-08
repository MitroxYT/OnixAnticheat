package me.onixdev.user.data;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import lombok.Getter;
import lombok.Setter;
import me.onixdev.OnixAnticheat;
import me.onixdev.user.OnixUser;
import me.onixdev.util.math.MathUtil;
import me.onixdev.util.math.VanillaMath;
import me.onixdev.util.net.ClientInput;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
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
    private float moveForward, moveStrafe;

    private double motionX, motionZ;
    private double distance = Double.MAX_VALUE;
    @Getter
    private Location setbackLocation;
    private boolean usingItem, sprinting, jump, slowdown, fastMath;
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
    public void OnBukkit(PlayerMoveEvent event) {
        if (this.user.getBukkitPlayer() != null) {
            boolean onGround = clientOnGround;
            Location setBackLocation = event.getFrom();
            if (onGround&&setbackticks > 5) {
                setbackLocation = setBackLocation;
            }
            ++setbackticks;
        }
    }

    public boolean isLastLastClientGround() {
        return lastLastClientGround;
    }
    public void setback() {
        if (setbackLocation != null) {
            Bukkit.getScheduler().runTask(OnixAnticheat.INSTANCE.getPlugin(), ()-> user.getBukkitPlayer().teleport(setbackLocation));
            setbackticks = 0;
        }
    }

    public void setLastLastClientGround(boolean lastLastClientGround) {
        this.lastLastClientGround = lastLastClientGround;
    }
    public boolean GetLastClientOnGround() {
        return lastClientOnGround;
    }

    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            Vector startMotion = new Vector(user.getMovementContainer().getDeltaX(), 0.0, user.getMovementContainer().getDeltaZ());
            this.distance = Double.MAX_VALUE;
            iteration:
            {
                for (int f = -1; f < 2; f++) {
                    for (int s = -1; s < 2; s++) {
                        for (int sp = 0; sp < 2; sp++) {
                            for (int jp = 0; jp < 2; jp++) {
                                for (int ui = 0; ui < 2; ui++) {
                                    for (int hs = 0; hs < 2; hs++) {
                                        for (int fm = 0; fm < 2; fm++) {
                                            boolean sprinting = sp == 0;
                                            boolean jump = jp == 0;
                                            boolean usingItem = ui == 0;
                                            boolean slowdown = user.lastHitTime == 1;//player.isAttacking;//hs == 0;
                                            boolean fastMath = fm == 1;
                                            final boolean ground = GetLastClientOnGround();
                                            final boolean sneaking = false;

                                            if (f <= 0.0F && sprinting && ground) continue;

                                            float moveForward = f;
                                            float moveStrafe = s;

                                            if (usingItem) {
                                                moveForward *= 0.2F;
                                                moveStrafe *= 0.2F;
                                              //  user.debug("us pre");
                                            }

                                            if (sneaking) {
                                                moveForward *= (float) 0.3D;
                                                moveStrafe *= (float) 0.3D;
                                            }
                                            if (slowdown) {
                                                moveForward *= 0.6F;
                                                moveStrafe *= 0.6F;
                                            }

                                            moveForward *= 0.98F;
                                            moveStrafe *= 0.98F;

                                            double motionX = user.getMovementContainer().getLastDeltaX();
                                            double motionZ = user.getMovementContainer().getLastDeltaZ();

                                            if (user.getMovementContainer().isLastLastClientGround()) {
                                                motionX *= 0.6F * 0.91F;
                                                motionZ *= 0.6F * 0.91F;
                                            } else {
                                                motionX *= 0.91F;
                                                motionZ *= 0.91F;
                                            }

                                            if (slowdown) {
                                                motionX *= 0.6;
                                                motionZ *= 0.6;
                                                //*player.debug("slowdowned");
                                            }

                                            if (Math.abs(motionX) < 0.005D) motionX = 0.0;
                                            if (Math.abs(motionZ) < 0.005D) motionZ = 0.0;

                                            if (jump && sprinting) {
                                                final float radians = (float) (user.getRotationContainer().getYaw() * 0.017453292F);

                                                if (fastMath) {
                                                    motionX -= VanillaMath.sin(radians) * 0.2F;
                                                    motionZ +=  VanillaMath.cos(radians) * 0.2F;
                                                } else {
                                                    motionX -= VanillaMath.sin(radians) * 0.2F;
                                                    motionZ += VanillaMath.cos(radians) * 0.2F;
                                                }
                                            }

                                            float friction = 0.91F;
                                            if (user.getMovementContainer().GetLastClientOnGround()) friction *= 0.6F;

                                            final float moveSpeed = (float) user.getMoveSpeed(sprinting,false);
                                            final float moveFlyingFriction;

                                            if (ground) {
                                                final float moveSpeedMultiplier = 0.16277136F / (friction * friction * friction);

                                                moveFlyingFriction = moveSpeed * moveSpeedMultiplier;
                                            } else {
                                                moveFlyingFriction = (float) (sprinting
                                                        ? ((double) 0.02F + (double) 0.02F * 0.3D)
                                                        : 0.02F);
                                            }

                                            final float[] moveFlying = this.moveFlying(moveForward, moveStrafe, moveFlyingFriction, fastMath);

                                            motionX += moveFlying[0];
                                            motionZ += moveFlying[1];

                                            Vector motion = new Vector(motionX, 0.0, motionZ);

                                            final double distance = startMotion.distanceSquared(motion);
                                            if (distance < this.distance) {
                                                this.distance = distance;
                                                this.motionX = motionX;
                                                this.motionZ = motionZ;
                                                this.moveStrafe = moveStrafe;
                                                this.moveForward = moveForward;

                                                this.sprinting = sprinting;
                                                this.jump = getDeltaY() > 0.2 && GetLastClientOnGround();
                                                this.usingItem = usingItem;
                                                this.slowdown = slowdown;
                                                this.fastMath = fastMath;
                                                user.theoreticalInput = new ClientInput(moveForward >0.0,moveForward < 0.0,moveStrafe == 0.98F,moveStrafe == -0.98F,jump,sneaking,sprinting,slowdown,moveForward,moveStrafe,distance);
                                                if (distance < 1e-14) break iteration;
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private float[] moveFlying(final float moveForward, final float moveStrafe, final float friction, final boolean fastMath) {
        float diagonal = moveStrafe * moveStrafe + moveForward * moveForward;

        float moveFlyingFactorX = 0.0F;
        float moveFlyingFactorZ = 0.0F;

        if (diagonal >= 1.0E-4F) {
            diagonal = MathUtil.sqrt_float(diagonal);

            if (diagonal < 1.0F) {
                diagonal = 1.0F;
            }

            diagonal = friction / diagonal;

            final float strafe = moveStrafe * diagonal;
            final float forward = moveForward * diagonal;

            final float rotationYaw = (float) user.getRotationContainer().getYaw();

            final float f1 = sin(fastMath, rotationYaw * (float) Math.PI / 180.0F);
            final float f2 = cos(fastMath, rotationYaw * (float) Math.PI / 180.0F);

            final float factorX = strafe * f2 - forward * f1;
            final float factorZ = forward * f2 + strafe * f1;

            moveFlyingFactorX = factorX;
            moveFlyingFactorZ = factorZ;
        }

        return new float[] {
                moveFlyingFactorX,
                moveFlyingFactorZ
        };
    }

    private float sin(final boolean fastMath, final float yaw) {
        return VanillaMath.sin(yaw);
    }

    private float cos(final boolean fastMath, final float yaw) {
        return VanillaMath.cos(yaw);
    }
}
