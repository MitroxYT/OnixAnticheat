package me.onixdev.util.math;


import java.util.LinkedList;

public final class DataList<T> extends LinkedList<T> {

    private final int sampleSize;
    private final boolean update;

    public DataList(int sampleSize) {
        this.sampleSize = sampleSize;
        this.update = false;
    }

    public DataList(int sampleSize, boolean update) {
        this.sampleSize = sampleSize;
        this.update = update;
    }

    @Override
    public boolean add(T t) {
        if (isCollected()) {
            if (this.update) {
                super.removeFirst();
            } else super.clear();
        }

        return super.add(t);
    }

    public int getMaxSize() {
        return sampleSize;
    }

    public boolean isCollected() {
        return super.size() >= this.sampleSize;
    }
}
