package me.onixdev.util.vec;

import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import lombok.Getter;
import me.onixdev.util.math.MathUtil;
import me.onixdev.util.math.VanillaMath;
import org.bukkit.util.Vector;


@SuppressWarnings("all")
public class Vec3 {

    public static final Vec3 ZERO = new Vec3(0.0, 0.0, 0.0);
    @Getter
    public double x;
    @Getter
    public double y;
    public double z;

    public static Vec3 fromRGB24(int pPacked) {
        double d0 = (double) (pPacked >> 16 & 0xFF) / 255.0;
        double d1 = (double) (pPacked >> 8 & 0xFF) / 255.0;
        double d2 = (double) (pPacked & 0xFF) / 255.0;
        return new Vec3(d0, d1, d2);
    }

    public static Vec3 atLowerCornerOf(Vec3i pToCopy) {
        return new Vec3((double) pToCopy.getX(), (double) pToCopy.getY(), (double) pToCopy.getZ());
    }

    public static Vec3 atLowerCornerWithOffset(Vec3i pToCopy, double pOffsetX, double pOffsetY, double pOffsetZ) {
        return new Vec3((double) pToCopy.getX() + pOffsetX, (double) pToCopy.getY() + pOffsetY, (double) pToCopy.getZ() + pOffsetZ);
    }

    public static Vec3 atCenterOf(Vec3i pToCopy) {
        return atLowerCornerWithOffset(pToCopy, 0.5, 0.5, 0.5);
    }

    public static Vec3 atBottomCenterOf(Vec3i pToCopy) {
        return atLowerCornerWithOffset(pToCopy, 0.5, 0.0, 0.5);
    }

    public static Vec3 upFromBottomCenterOf(Vec3i pToCopy, double pVerticalOffset) {
        return atLowerCornerWithOffset(pToCopy, 0.5, pVerticalOffset, 0.5);
    }

    public Vec3() {
        this.x = 0.0D;
        this.y = 0.0D;
        this.z = 0.0D;
    }

    public Vec3(double pX, double pY, double pZ) {
        this.x = pX;
        this.y = pY;
        this.z = pZ;
    }

    public static Vec3 fromPitchYaw(float pitch, float yaw) {
        float radYaw = (float) Math.toRadians(-yaw) - (float) Math.PI;
        float radPitch = (float) Math.toRadians(-pitch);

        float x = (float) (Math.sin(radYaw) * Math.cos(radPitch));
        float y = (float) (Math.sin(radPitch));
        float z = (float) (Math.cos(radYaw) * Math.cos(radPitch));

        return new Vec3(x, y, z);
    }

    public Vec3 clone() {
        return new Vec3(this.x, this.y, this.z);
    }


//    public Vec3(Vector3f pVector) {
//        this((double)pVector.x(), (double)pVector.y(), (double)pVector.z());
//    }

    public Vec3 vectorTo(Vec3 pVec) {
        return new Vec3(pVec.x - this.x, pVec.y - this.y, pVec.z - this.z);
    }

    public Vec3 normalize() {
        double d0 = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        return d0 < 1.0E-4 ? ZERO : new Vec3(this.x / d0, this.y / d0, this.z / d0);
    }

    public double dot(Vec3 pVec) {
        return this.x * pVec.x + this.y * pVec.y + this.z * pVec.z;
    }

    public Vec3 cross(Vec3 pVec) {
        return new Vec3(
                this.y * pVec.z - this.z * pVec.y,
                this.z * pVec.x - this.x * pVec.z,
                this.x * pVec.y - this.y * pVec.x
        );
    }

    public Vec3 subtract(Vec3 pVec) {
        return this.subtract(pVec.x, pVec.y, pVec.z);
    }

    public Vec3 subtract(double pX, double pY, double pZ) {
        return this.add(-pX, -pY, -pZ);
    }

    public Vec3 add(Vec3 pVec) {
        return this.add(pVec.x, pVec.y, pVec.z);
    }

    public Vec3 add(double pX, double pY, double pZ) {
        return new Vec3(this.x + pX, this.y + pY, this.z + pZ);
    }

    public boolean closerThan(Vec3 pPos, double pDistance) {
        return this.distanceToSqr(pPos.x(), pPos.y(), pPos.z()) < pDistance * pDistance;
    }

    public double distanceTo(Vec3 pVec) {
        double d0 = pVec.x - this.x;
        double d1 = pVec.y - this.y;
        double d2 = pVec.z - this.z;
        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public double distanceToSqr(Vec3 pVec) {
        double d0 = pVec.x - this.x;
        double d1 = pVec.y - this.y;
        double d2 = pVec.z - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double distanceToSqr(double pX, double pY, double pZ) {
        double d0 = pX - this.x;
        double d1 = pY - this.y;
        double d2 = pZ - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public boolean closerThan(Vec3 pPos, double pHorizontalDistance, double pVerticalDistance) {
        double d0 = pPos.x() - this.x;
        double d1 = pPos.y() - this.y;
        double d2 = pPos.z() - this.z;
        return MathUtil.lengthSquared(d0, d2) < MathUtil.square(pHorizontalDistance) && Math.abs(d1) < pVerticalDistance;
    }

    public Vec3 scale(double pFactor) {
        return this.multiply(pFactor, pFactor, pFactor);
    }

    public Vec3 reverse() {
        return this.scale(-1.0);
    }

    public Vec3 multiply(double pVec) {
        return this.multiply(pVec, pVec, pVec);
    }

    public Vec3 multiply(Vec3 pVec) {
        return this.multiply(pVec.x, pVec.y, pVec.z);
    }

    public Vec3 multiply(double pFactorX, double pFactorY, double pFactorZ) {
        return new Vec3(this.x * pFactorX, this.y * pFactorY, this.z * pFactorZ);
    }


    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSqr() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double horizontalDistance() {
        return Math.sqrt(this.x * this.x + this.z * this.z);
    }

    public double horizontalDistanceSqr() {
        return this.x * this.x + this.z * this.z;
    }

//    @Override
//    public boolean equals(Object pOther) {
//        if (this == pOther) {
//            return true;
//        } else if (pOther instanceof Vec3 vec3) {
//            if (Double.compare(vec3.x, this.x) != 0) {
//                return false;
//            } else {
//                return Double.compare(vec3.y, this.y) != 0 ? false : Double.compare(vec3.z, this.z) == 0;
//            }
//        } else {
//            return false;
//        }
//    }

    @Override
    public int hashCode() {
        long i = Double.doubleToLongBits(this.x);
        int j = (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.y);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.z);
        return 31 * j + (int) (i ^ i >>> 32);
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public Vec3 lerp(Vec3 pTo, double pDelta) {
        return new Vec3(
                MathUtil.lerp(pDelta, this.x, pTo.x),
                MathUtil.lerp(pDelta, this.y, pTo.y),
                MathUtil.lerp(pDelta, this.z, pTo.z)
        );
    }

    public Vec3 xRot(float pPitch) {
        float f = VanillaMath.cos(pPitch);
        float f1 = VanillaMath.sin(pPitch);
        double d0 = this.x;
        double d1 = this.y * (double) f + this.z * (double) f1;
        double d2 = this.z * (double) f - this.y * (double) f1;
        return new Vec3(d0, d1, d2);
    }

    public Vec3 yRot(float pYaw) {
        float f = VanillaMath.cos(pYaw);
        float f1 = VanillaMath.sin(pYaw);
        double d0 = this.x * (double) f + this.z * (double) f1;
        double d1 = this.y;
        double d2 = this.z * (double) f - this.x * (double) f1;
        return new Vec3(d0, d1, d2);
    }

    public Vec3 zRot(float pRoll) {
        float f = VanillaMath.cos(pRoll);
        float f1 = VanillaMath.sin(pRoll);
        double d0 = this.x * (double) f + this.y * (double) f1;
        double d1 = this.y * (double) f - this.x * (double) f1;
        double d2 = this.z;
        return new Vec3(d0, d1, d2);
    }

    public Vec3 setY(double pY) {
        this.y = pY;
        return this;
    }
//    public static Vec3 directionFromRotation(Vec2 pVec) {
//        return directionFromRotation(pVec.x, pVec.y);
//    }

    public static Vec3 directionFromRotation(float pPitch, float pYaw) {
        float f = VanillaMath.cos(-pYaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f1 = VanillaMath.sin(-pYaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f2 = -VanillaMath.cos(-pPitch * (float) (Math.PI / 180.0));
        float f3 = VanillaMath.sin(-pPitch * (float) (Math.PI / 180.0));
        return new Vec3((double) (f1 * f2), (double) f3, (double) (f * f2));
    }


    public final double x() {
        return this.x;
    }


    public final double y() {
        return this.y;
    }

    public final double z() {
        return this.z;
    }

    public Vector3f toVector3f() {
        return new Vector3f((float) this.x, (float) this.y, (float) this.z);
    }

    public Vector toVector() {
        return new Vector((float) this.x, (float) this.y, (float) this.z);
    }


    public Vector3d toVectorD() {
        return new Vector3d(x, y, z);
    }

    public double getZ() {
        return z;
    }

    public void setX(double v) {
        this.x = v;
    }

    public void setZ(double v) {
        this.z = v;
    }
}