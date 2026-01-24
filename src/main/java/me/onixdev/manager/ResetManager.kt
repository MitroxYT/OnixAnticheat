package me.onixdev.manager

import me.onixdev.OnixAnticheat
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask


class ResetManager : Runnable {
    fun start() {
        assert(task == null) { "ResetProcessor has already been started!" }
        val RESET_INTERVAL: Double = OnixAnticheat.INSTANCE.configManager.resetVl
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(
            OnixAnticheat.INSTANCE.plugin,
            this,
            RESET_INTERVAL.toLong() * 1200,
            RESET_INTERVAL.toLong() * 1200
        )
    }

    fun stop() {
        if (task == null) {
            return
        }
        task!!.cancel()
        task = null
    }

    override fun run() {
        reset()
    }

    companion object {
        private var task: BukkitTask? = null

        fun reset() {
            // if (Config.VIOLATION_RESET) {

            OnixAnticheat.INSTANCE.playerDatamanager.allData.parallelStream().forEach { data ->

                /*data.setMovementViolations(0);
                data.setPlayerViolations(0);
                data.setAutoClickerViolations(0);
                data.setScaffoldViolations(0);
                data.setTimerViolations(0);
                data.ansoft = 100;
                data.soft = 0;
                data.checkced = false;*/
                for (check in data.checks) check.vl = 0.0
            }

            //   VlProtect.resetflag();
            //  }
        }
    }
}