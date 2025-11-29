package me.onixdev.util.thread.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.onixdev.util.thread.api.IThreadExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlertTaskExecutor implements IThreadExecutor {
    private final ExecutorService  executor;
    public AlertTaskExecutor() {
        executor = Executors.newFixedThreadPool(1,new ThreadFactoryBuilder().setNameFormat("Onix-AlertExecuter %d").build());
    }
    @Override
    public void run(Runnable runnable) {
        executor.execute(runnable);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }
}
