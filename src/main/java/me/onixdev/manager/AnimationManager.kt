package me.onixdev.manager

import dev.onixac.api.manager.IAnimationManager
import me.onixdev.animation.BaseAnimation
import me.onixdev.animation.impl.ThunderAnimation
import org.bukkit.entity.Player

class AnimationManager : IAnimationManager {
    private var animations = arrayListOf<BaseAnimation>()
    fun init() {
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