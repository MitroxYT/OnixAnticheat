package me.onixdev.util.math;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data

public final class Pair<X, Y> {
    private X x;
    private Y y;
    public Pair(final X x, final Y y) {
        this.x = x;
        this.y = y;
    }
    public X getFirst() {
        return x;
    }
    public Y getSecond() {
        return y;
    }
}
