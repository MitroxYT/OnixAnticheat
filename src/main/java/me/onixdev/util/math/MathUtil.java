package me.onixdev.util.math;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class MathUtil {


    public double calculateVariationScore(List<Double> deltas) {
        double mean = calculateMean(deltas);
        double stdDev = calculateStdDev(deltas, mean);

        double cv = (mean != 0) ? stdDev / Math.abs(mean) : 0;

        if (cv < 0.1) return 0.0;
        if (cv > 2.0) return 1.0;
        return (cv - 0.1) / 1.9;
    }

    public double calculateDistributionScore(List<Double> deltas) {

        int[] bins = new int[5];
        double min = Collections.min(deltas);
        double max = Collections.max(deltas);
        double range = max - min;

        if (range == 0) return 0.0;

        for (double value : deltas) {
            int bin = (int) ((value - min) / range * 4.999);
            bins[bin]++;
        }
        double entropy = 0.0;
        for (int count : bins) {
            if (count > 0) {
                double p = (double) count / deltas.size();
                entropy -= p * Math.log(p);
            }
        }

        double maxEntropy = Math.log(5);
        return entropy / maxEntropy;
    }

    private double calculateLinearityScore(List<Double> deltas) {

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        int n = deltas.size();

        for (int i = 0; i < n; i++) {
            double x = i;
            double y = deltas.get(i);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double meanY = sumY / n;

        double b = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double a = (sumY - b * sumX) / n;

        double ssTot = 0, ssRes = 0;
        for (int i = 0; i < n; i++) {
            double y = deltas.get(i);
            double yPred = a + b * i;
            ssTot += Math.pow(y - meanY, 2);
            ssRes += Math.pow(y - yPred, 2);
        }

        return 1 - (ssRes / (ssTot + 1e-10));
    }
    public static double getMedian(final List<Double> data) {
        if (data.size() % 2 == 0) {
            return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
        } else {
            return data.get(data.size() / 2);
        }
    }

    public static Pair<List<Double>, List<Double>> getOutliers(final Collection<? extends Number> collection) {
        final List<Double> values = new ArrayList<>();

        for (final Number number : collection) {
            values.add(number.doubleValue());
        }

        final double q1 = getMedian(values.subList(0, values.size() / 2));
        final double q3 = getMedian(values.subList(values.size() / 2, values.size()));

        final double iqr = Math.abs(q1 - q3);
        final double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        final Pair<List<Double>, List<Double>> tuple = new Pair<>(new ArrayList<>(), new ArrayList<>());

        for (final Double value : values) {
            if (value < lowThreshold) {
                tuple.getX().add(value);
            } else if (value > highThreshold) {
                tuple.getY().add(value);
            }
        }

        return tuple;
    }

    private double calculateMean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private double calculateStdDev(List<Double> values, double mean) {
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average().orElse(0);
        return Math.sqrt(variance);
    }

    public static double hypot(final double a, final double b) {
        return Math.sqrt(a * a + b * b);
    }
}
