package me.onixdev.util.thread.api;

public interface IThreadExecutor {
    void run(Runnable runnable);
    void shutdown();
}
