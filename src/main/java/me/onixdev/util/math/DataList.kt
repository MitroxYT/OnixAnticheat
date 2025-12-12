package me.onixdev.util.math;
import java.util.*

class DataList<T> : LinkedList<T> {
    val maxSize: Int
    private val update: Boolean

    constructor(sampleSize: Int) {
        this.maxSize = sampleSize
        this.update = false
    }

    constructor(sampleSize: Int, update: Boolean) {
        this.maxSize = sampleSize
        this.update = update
    }

    override fun add(element: T): Boolean {
        if (isCollected) {
            if (this.update) {
                super.removeFirst()
            } else super.clear()
        }

        return super.add(element)
    }

    val isCollected: Boolean
        get() = super.size >= this.maxSize
}
