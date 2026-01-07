package me.onixdev.util.time

import me.onixdev.user.OnixUser


class PlayerTimer(private val player: OnixUser) {
    var startTime: Int = 0
    var resetStreak1: Int = 0

    init {
        this.reset()
    }

    fun wasReset(): Boolean {
        return this.startTime == player.currentTick
    }

    fun wasNotReset(): Boolean {
        return this.startTime != player.currentTick
    }

    fun reset() {
        if (this.passed == 1L) resetStreak1++
        else resetStreak1 = 0
        this.startTime = player.currentTick
    }

    fun getResetStreak(): Int {
        return if (wasNotReset()) 0 else resetStreak1
    }

    val passed: Long
        get() = (player.currentTick - this.startTime).toLong()

    fun add(amount: Int) {
        this.startTime -= amount
    }

    fun hasPassed(toPass: Long): Boolean {
        return this.passed >= toPass
    }

    fun hasNotPassed(toPass: Long): Boolean {
        return this.passed < toPass
    }

    fun hasPassed(toPass: Long, reset: Boolean): Boolean {
        val passed = this.passed >= toPass
        if (passed && reset) reset()
        return passed
    }

}