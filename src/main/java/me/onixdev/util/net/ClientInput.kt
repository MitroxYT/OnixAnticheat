package me.onixdev.util.net

import dev.onixac.api.user.IClientInput

class ClientInput @JvmOverloads constructor(
    private var forward: Boolean = false,
    private var backward: Boolean = false,
    private var left: Boolean = false,
    private var right: Boolean = false,
    private var jump: Boolean = false,
    private var shift: Boolean = false,
    private var sprint: Boolean = false,
    private val slowing: Boolean = false,
    private var forwardMotion: Double = 0.0,
    private var strafe: Double = 0.0,
    private val motion: Double = 0.0
) : IClientInput {
    override fun isForward(): Boolean {
        return forward
    }

    override fun isBackward(): Boolean {
        return backward
    }

    override fun isLeft(): Boolean {
        return left
    }

    override fun isRight(): Boolean {
        return right
    }

    override fun isJump(): Boolean {
        return jump
    }

    override fun isShift(): Boolean {
        return shift
    }

    override fun isSprint(): Boolean {
        return sprint
    }

    override fun hasInput(): Boolean {
        return forward || backward || left || right
    }


    override fun getForwardMotion(): Double {
        return forwardMotion
    }

    override fun getStrafe(): Double {
        return strafe
    }

    override fun isSlowing(): Boolean {
        return slowing
    }

    override fun reset() {
        sprint = false
        shift = sprint
        jump = shift
        right = jump
        left = right
        backward = left
        forward = backward
        strafe = 0.0
        forwardMotion = strafe
    }


    override fun getMotion(): Double {
        return motion
    }

    override fun toString(): String {
        return "ClientKeyInput{" +
                "forward=" + forward +
                ", backward=" + backward +
                ", left=" + left +
                ", right=" + right +
                ", jump=" + jump +
                ", shift=" + shift +
                ", sprint=" + sprint +
                '}'
    }
}