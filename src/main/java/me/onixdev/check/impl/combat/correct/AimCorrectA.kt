package me.onixdev.check.impl.combat.correct

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser
import me.onixdev.util.grimentity.boxes.SimpleCollisionBox
import me.onixdev.util.vec.Vec3
import kotlin.math.abs

@CheckInfo(name = "AimCorrect", type = "A")
class AimCorrectA(user: OnixUser) : Check(user) {
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerRotationEvent) {
            if (event.isPost) {
                if (player.combatData.hasAttackedSince(100L)) {
                    if (player.combatData.onixTarget != null) {
                        val lookVec3 = Vec3(player.movementContainer.x,0.0,player.movementContainer.z)
                        val lookBox = SimpleCollisionBox(lookVec3)
                        val movementContainerT = player.combatData.onixTarget?.movementContainer ?: return
                        val targetBox = SimpleCollisionBox(Vec3(movementContainerT.x,0.0,movementContainerT.z))
                        val firstStr = lookBox.toString()
                        val twoStr = targetBox.toString()
                        player.debug("1: $firstStr")
                        player.debug("2: $twoStr")
                        val collX= abs(lookBox.minX -  targetBox.minX)
                        val collY= abs(lookBox.minY -  targetBox.minY)
                        val collZ= abs(lookBox.minZ -  targetBox.minZ)
                        val debug = " cx: $collX cy: $collY cz: $collZ"
                        val distBox = Vec3.fromVector(lookBox.min()).normal().distanceTo(Vec3.fromVector(targetBox.min()).normal())
                        player.debug("dddd $debug $distBox")
                    }
                }
            }
        }
    }
}