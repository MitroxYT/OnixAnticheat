package me.onixdev.util.versions

import me.onixdev.OnixAnticheat


class NmsManager {
    val nmsInstance: NmsInstance

    init {
        when (OnixAnticheat.INSTANCE.serverVersion) {
            else -> this.nmsInstance = InstanceDefault()
        }
    }
}
