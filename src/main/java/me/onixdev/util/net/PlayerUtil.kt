package me.onixdev.util.net

import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

object PlayerUtil {
    fun getDirection(yaw: Float, pitch: Float): Vector {
        val yawRad = Math.toRadians(yaw.toDouble())
        val pitchRad = Math.toRadians(pitch.toDouble())
        val x = -sin(yawRad) * cos(pitchRad)
        val y = -sin(pitchRad)
        val z = cos(yawRad) * cos(pitchRad)
        return Vector(x, y, z).normalize()
    }
    fun raytrace(player: Player, direction: Vector,maxDistance:Double,stepSize:Double): me.onixdev.util.math.Pair<Int, Block> {
        val eyeLocation = player.eyeLocation
        val currentPos = eyeLocation.clone()
        var currentStep = 0
        var distance = 0.0
        while (distance <= maxDistance) {
            currentPos.add(direction.clone().multiply(stepSize))
            val block = currentPos.block

            if (!block.type.isAir) {
              return me.onixdev.util.math.Pair(currentStep,block)
            }
            distance += stepSize
            currentStep++
        }
        return me.onixdev.util.math.Pair(null,null)
    }
    fun getPlayer(id: Int): Player? {
        for (entity in Bukkit.getOnlinePlayers()) {
            if (entity.entityId == id) {
                return entity
            }
        }
        return null
    }
    fun isFullBlock(block: Block): Boolean {
        val box = block.boundingBox
        val size = box.getMax().subtract(box.getMin())
        return size.getX() == 1.0 && size.getY() == 1.0 && size.getZ() == 1.0
    }
}