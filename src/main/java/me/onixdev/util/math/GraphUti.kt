package me.onixdev.util.math


object GraphUtil {
    fun getGraph(values: List<Double>): GraphResult {
        val graph = StringBuilder()

        var largest = 0.0

        for (value in values) {
            if (value > largest) {
                largest = value
            }
        }

        val graphHeight = 2
        var positives = 0
        var negatives = 0

        var i = graphHeight - 1
        while (i > 0) {
            val sb = StringBuilder()

            for (index in values) {
                val value = graphHeight * index / (if (largest == 0.0) 1.0 else largest)

                if (value > i && value < i + 1) {
                    positives++
                    sb.append("+")
                } else {
                    negatives++
                    sb.append("-")
                }
            }

            graph.append(sb)
            i -= 1
        }

        return GraphResult(graph.toString(), positives, negatives)
    }

    class GraphResult(val graph: String, val positives: Int, val negatives: Int)
}