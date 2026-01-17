package me.onixdev.animation

import org.bukkit.entity.Player

abstract class BaseAnimation(private val name: String) {
    fun getName(): String {return name}
    abstract fun execute(player: Player?, data: String?)
}