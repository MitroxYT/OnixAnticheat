package me.onixdev.util.math

import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class RotList {
    private lateinit var values: FloatArray
    private var head = 0
    private var count = 0
    val EMPTY_MARKER: Float = -114.514f
    constructor(capacity: Int) {
        if (capacity <= 1) {
            throw IllegalArgumentException("Buffer size must be greater than 1");
        }

        this.values = FloatArray(capacity)
        Arrays.fill(this.values, EMPTY_MARKER);
    }

    fun add(value: Float) {
        if (value == EMPTY_MARKER) {
            println("Cannot add the reserved marker value")
            return
        }

        values[head] = value
        head = (head + 1) % values.size

        if (count < values.size) {
            count++
        }
    }
    fun getValues(): FloatArray {
        val result = FloatArray(count)
        for (i in 0..<count) {
            result[i] = values[(head - count + i + values.size) % values.size]
        }
        return result
    }
    fun clear() {
        Arrays.fill(this.values, EMPTY_MARKER)
        head = 0
        count = 0
    }

    fun getAverage(): Float {
        if (count == 0) return 0.0f

        var sum = 0.0f
        for (value in getValues()) {
            sum += value
        }
        return sum / count
    }

    fun calculateVariance(): Float {
        if (count == 0) return 0.0f

        val avg = getAverage()
        var sumSquaredDiffs = 0.0f

        for (value in getValues()) {
            sumSquaredDiffs += (value - avg).toDouble().pow(2.0).toFloat()
        }

        return sumSquaredDiffs / count
    }

    fun getStandardDeviation(): Float {
        return sqrt(calculateVariance().toDouble()).toFloat()
    }


    fun isFull(): Boolean {
        return count == values.size
    }
}