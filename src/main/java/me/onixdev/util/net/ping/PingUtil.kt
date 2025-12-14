package me.onixdev.util.net.ping

import com.github.retrooper.packetevents.netty.channel.ChannelHelper
import me.onixdev.user.OnixUser
import java.util.*

class PingUtil(private var player: OnixUser) {
    private val transactionMap = LinkedList<Pair<Int, Runnable>>()
    private val tasksToRun = ArrayList<Runnable>()
    fun addTask(transaction: Int, runnable: Runnable) {
        addTask(transaction, false, runnable)
    }

    fun addTaskAsync(transaction: Int, runnable: Runnable) {
        addTask(transaction, true, runnable)
    }

    fun addTask(transaction: Int, async: Boolean, runnable: Runnable) {
        if (player.connectionContainer.lastTransactionReceived.get() >= transaction) {
            if (async) {
                ChannelHelper.runInEventLoop(player.user.getChannel(), runnable)
            } else {
                runnable.run()
            }
            return
        }
        synchronized(this) {
            transactionMap.add(Pair(transaction, runnable))
        }
    }

    fun handleNettySyncTransaction(transaction: Int) {
        synchronized(this) {
            tasksToRun.clear()
            val iterator =
                transactionMap.listIterator()
            while (iterator.hasNext()) {
                val pair = iterator.next()
                if (transaction + 1 < pair.first) break
                if (transaction == pair.first - 1) continue

                tasksToRun.add(pair.second)
                iterator.remove()
            }
            for (runnable in tasksToRun) {
                try {
                    runnable.run()
                } catch (_: Exception) {
                }
            }
        }
    }

}