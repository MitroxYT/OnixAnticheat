package me.onixdev.util.rotation

import me.onixdev.util.net.PlayerUtil
import org.bukkit.util.Vector


class Rotation {
    var yaw: Double = 0.0
    var pitch: Double = 0.0
    constructor(yaw: Double, pitch: Double){
        this.yaw = yaw
        this.pitch = pitch
    }
    fun toDirection(): Vector {
        return PlayerUtil.getDirection(yaw.toFloat(),pitch.toFloat())
    }
}