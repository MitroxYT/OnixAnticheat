package me.onixdev.util.position

import me.onixdev.util.vec.Vec3
import kotlin.math.abs

class PlayerPositionMotion(val from: Vec3,val to: Vec3,val isOnGround: Boolean) {
    fun getXDelta() : Double {
        return abs(from.x()-to.x())
    }
    fun getYDelta() : Double {
        return (from.y()-to.y())
    }
    fun getZDelta() : Double {
        return abs(from.z()-to.z())
    }
}