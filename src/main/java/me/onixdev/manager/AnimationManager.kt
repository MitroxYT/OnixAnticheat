package me.onixdev.manager

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import dev.onixac.api.manager.IAnimationManager
import me.onixdev.animation.BaseAnimation
import me.onixdev.animation.impl.ThunderAnimation
import org.bukkit.entity.Player

class AnimationManager : IAnimationManager {
    private var animations = arrayListOf<BaseAnimation>()
    fun init() {
        if (PacketEvents.getAPI().serverManager.version.isOlderThanOrEquals(ServerVersion.V_1_12_2)) return
        animations.add(ThunderAnimation())
    }
    override fun startPunishment(name: String?, player: Player?, data: String?) {
        for (animation in animations) {
            if (name == null || data == null) continue
            if (animation.getName().contentEquals(name)) {
                animation.execute(player,data)
            }
        }
    }
}