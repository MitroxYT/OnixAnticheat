package me.onixdev.util.position

import me.onixdev.util.vec.Vec3

class PlayerPositionMotion(val from: Vec3,val to: Vec3,val isOnGround: Boolean) {
    fun getXDelta() : Double {
        return (from.x()-to.x())
    }
    fun getYDelta() : Double {
        return (from.y()-to.y())
    }
    fun getZDelta() : Double {
        return (from.z()-to.z())
    }
}