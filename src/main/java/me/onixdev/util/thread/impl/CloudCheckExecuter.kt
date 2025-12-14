package me.onixdev.util.thread.impl

import com.google.common.util.concurrent.ThreadFactoryBuilder
import me.onixdev.util.thread.api.IThreadExecutor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CloudCheckExecuter : IThreadExecutor {
    private var executor: ExecutorService? = null
    init {
        executor = Executors.newFixedThreadPool(3, ThreadFactoryBuilder().setNameFormat("Onix-CloudExecuter %d").build())
    }

    override fun run(runnable: Runnable?) {
        executor!!.execute(runnable)
    }

    override fun shutdown() {
        executor!!.shutdown()
    }
}