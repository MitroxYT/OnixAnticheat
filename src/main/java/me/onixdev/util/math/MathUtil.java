package me.onixdev.util.math;

import lombok.experimental.UtilityClass;

import java.util.*;

@SuppressWarnings("all")
@UtilityClass
public class MathUtil {


    public static final double EXPANDER = Math.pow(2.0D, 24.0D);

    public static double calculateVariationScore(List<Double> deltas) {
        double mean = calculateMean(deltas);
        double stdDev = calculateStdDev(deltas, mean);

        double cv = (mean != 0) ? stdDev / Math.abs(mean) : 0;

        if (cv < 0.1) return 0.0;
        if (cv > 2.0) return 1.0;
        return (cv - 0.1) / 1.9;
    }

    public static double calculateDistributionScore(List<Double> deltas) {

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

    public static double calculateLinearityScore(List<Double> deltas) {

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

    public static double calculateStdDev(List<Double> values) {
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - 2, 2))
                .average().orElse(0);
        return Math.sqrt(variance);
    }

    public double calculateStdDev(List<Double> values, double mean) {
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average().orElse(0);
        return Math.sqrt(variance);
    }

    public static double hypot(final double a, final double b) {
        return Math.sqrt(a * a + b * b);
    }

    public static float sqrt_float(float value) {
        return (float) Math.sqrt(value);
    }

    public static long getGcd(long current, long previous) {
        return previous <= 16384L ? current : getGcd(previous, current % previous);
    }

    public static double getGcd(double a, double b) {
        if (a < b) {
            return getGcd(b, a);
        } else {
            return Math.abs(b) < 0.001D ? a : getGcd(b, a - Math.floor(a / b) * b);
        }
    }

    public static float calculateGCDError(float delta) {
        if (Math.abs(delta) < 0.0001f) {
            return 0.0f;
        }

        float gcd = calculateGCD(Math.abs(delta));
        float remainder = Math.abs(delta) % gcd;
        return remainder / gcd;
    }

    private static float calculateGCD(float a) {
        a = Math.abs(a);
        float b = 0.1f;

        while (b > 0.0001f) {
            float temp = a % b;
            a = b;
            b = temp;
        }

        return a;
    }

    public static double getSE(final Collection<? extends Number> numberSet) {
        if (numberSet == null || numberSet.isEmpty()) return 0.0;

        Map<Double, Integer> counts = new HashMap<>();
        for (Number n : numberSet) {
            double v = n.doubleValue();
            counts.put(v, counts.getOrDefault(v, 0) + 1);
        }
        double n = numberSet.size();
        double result = 0.0;

        for (int c : counts.values()) {
            double frequency = c / n;
            result -= frequency * (Math.log(frequency) / Math.log(2));
        }

        return result;
    }


    public static double jerk(List<Double> data) {
        if (data.size() < 4) {
            return 0.0D;
        }
        double total = 0.0D;

        for (int i = 3; i < data.size(); i++) {
            total += Math.abs(
                    data.get(i)
                            - 3.0F * data.get(i - 1)
                            + 3.0F * data.get(i - 2)
                            - data.get(i - 3)
            );
        }
        return total / (data.size() - 3);
    }

    public static int getMode(Collection<? extends Number> array) {
        int mode = (Integer) array.toArray()[0];
        int maxCount = 0;
        Iterator var3 = array.iterator();

        while (var3.hasNext()) {
            Number value = (Number) var3.next();
            int count = 1;
            Iterator var6 = array.iterator();

            while (var6.hasNext()) {
                Number i = (Number) var6.next();
                if (i.equals(value)) {
                    ++count;
                }

                if (count > maxCount) {
                    mode = (Integer) value;
                    maxCount = count;
                }
            }
        }

        return mode;
    }

    public static double runsZScore(List<Double> values) {
        if (values == null || values.size() < 10) {
            return 0.0;
        }
        ArrayList<Double> sorted = new ArrayList<Double>(values);
        Collections.sort(sorted);
        double median = (Double) sorted.get(sorted.size() / 2);
        ArrayList<Integer> signs = new ArrayList<Integer>();
        for (double v : values) {
            if (v > median) {
                signs.add(1);
                continue;
            }
            if (!(v < median)) continue;
            signs.add(-1);
        }
        if (signs.size() < 10) {
            return 0.0;
        }
        int runs = 1;
        for (int i = 1; i < signs.size(); ++i) {
            if (((Integer) signs.get(i)).equals(signs.get(i - 1))) continue;
            ++runs;
        }
        int n1 = 0;
        int n2 = 0;
        Iterator iterator = signs.iterator();
        while (iterator.hasNext()) {
            int s = (Integer) iterator.next();
            if (s == 1) {
                ++n1;
                continue;
            }
            ++n2;
        }
        double expectedRuns = 2.0 * (double) n1 * (double) n2 / (double) (n1 + n2) + 1.0;
        double varianceRuns = 2.0 * (double) n1 * (double) n2 * (2.0 * (double) n1 * (double) n2 - (double) n1 - (double) n2) / (Math.pow(n1 + n2, 2.0) * (double) (n1 + n2 - 1));
        if (varianceRuns <= 0.0) {
            return 0.0;
        }
        return ((double) runs - expectedRuns) / Math.sqrt(varianceRuns);
    }

    public static double scaleVal(double value, double scale) {
        double scale2 = Math.pow(10, scale);
        return Math.ceil(value * scale2) / scale2;
    }

    public static double clamp(double num, double min, double max) {
        if (num < min) {
            return min;
        }
        return Math.min(num, max);
    }

    public static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        }
        return Math.min(num, max);
    }

    public static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        }
        return Math.min(num, max);
    }

    public static int floor(double d) {
        return (int) Math.floor(d);
    }

    public static int ceil(double d) {
        return (int) Math.ceil(d);
    }


    public static int sign(double x) {
        if (x == 0.0) {
            return 0;
        } else {
            return x > 0.0 ? 1 : -1;
        }
    }

    public static float square(float value) {
        return value * value;
    }

    public static double square(double value) {
        return value * value;
    }

    public static boolean equal(double first, double second) {
        return Math.abs(second - first) < 1.0E-5F;
    }

    public static float sqrt(float value) {
        return (float) Math.sqrt(value);
    }

    public static double lerp(double lerpAmount, double start, double end) {
        return start + lerpAmount * (end - start);
    }

    public static double frac(double p_14186_) {
        return p_14186_ - lfloor(p_14186_);
    }

    public static long lfloor(double p_14135_) {
        long i = (long) p_14135_;
        return p_14135_ < (double) i ? i - 1L : i;
    }

    public static double lengthSquared(double pXDistance, double pYDistance, double pZDistance) {
        return pXDistance * pXDistance + pYDistance * pYDistance + pZDistance * pZDistance;
    }

    public static double lengthSquared(double pXDistance, double pYDistance) {
        return pXDistance * pXDistance + pYDistance * pYDistance;
    }

}
