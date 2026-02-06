package me.onixdev.util.world.utils.collisions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@SuppressWarnings("all")
public class CollisionBuilder {
    private double minx, miny, minz;
    private double maxx, maxy, maxz;
    private double y;
    private double width, height;
    private boolean minusy, usewidth, useheight, usecustomY = false;

    public static CollisionBuilder create() {
        return new CollisionBuilder();
    }

    public CollisionBuilder setMinx(double minx) {
        this.minx = minx;
        return this;
    }

    public CollisionBuilder setMiny(double miny) {
        this.miny = miny;
        return this;
    }

    public CollisionBuilder setMinz(double minz) {
        this.minz = minz;
        return this;
    }

    public CollisionBuilder setMaxx(double maxx) {
        this.maxx = maxx;
        return this;
    }

    public CollisionBuilder minusy(boolean minusy) {
        this.minusy = minusy;
        return this;
    }

    public CollisionBuilder setMaxy(double maxy) {
        this.maxy = maxy;
        return this;
    }

    public CollisionBuilder setMaxz(double maxz) {
        this.maxz = maxz;
        return this;
    }

    public CollisionBuilder expand(double x, double y, double z) {
        this.minx += x;
        this.maxx += x;
        this.miny += y;
        this.maxy += y;
        this.minz += z;
        this.maxz += z;
        return this;
    }

    public CollisionBuilder expand(double val) {
        return expand(val, val, val);
    }

    public CollisionBuilder build() {
        return this;
    }

    public CollisionBuilder setUsecustomY(boolean b) {
        this.usecustomY = b;
        return this;
    }

    public CollisionBuilder setY(double i) {
        this.y = i;
        return this;
    }
}
