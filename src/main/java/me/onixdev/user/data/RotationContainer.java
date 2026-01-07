package me.onixdev.user.data;

import dev.onixac.api.user.data.IPlayerRotationData;
import me.onixdev.event.impl.PlayerRotationEvent;
import me.onixdev.user.OnixUser;
import me.onixdev.util.math.GraphUtil;
import me.onixdev.util.math.MathUtil;
import me.onixdev.util.net.MinecraftValues;

import java.util.*;


public class RotationContainer  implements IPlayerRotationData {
    private final OnixUser user;
    private float yaw;
    private float pitch;
    private float lastYaw;
    private float lastPitch;
    private float deltaYaw;
    private float deltaPitch;
    private float lastDeltaYaw;
    private float lastDeltaPitch;
    private float yawAccel;
    private float pitchAccel;
    private float lastYawAccel;
    private float lastPitchAccel;
    private float rawMouseDeltaX;
    private float rawMouseDeltaY;
    private float fuckedPredictedPitch;
    private float fuckedPredictedYaw;
    private float lastFuckedPredictedPitch;
    private float lastFuckedPredictedYaw;
    private boolean cinematic;
    private double finalSensitivity;
    private double mcpSensitivity;
    private final ArrayDeque<Integer> sensitivitySamples;
    private int sensitivity;
    private int mouseDeltaX;
    private int mouseDeltaY;

    private float lastJoltYaw;

    private float lastJoltPitch;

    private float joltYaw;

    private float joltPitch;
    private float smoothnessYaw = 0.0f;
    private float smoothnessPitch = 0.0f;
    private float consistency = 0.0f;
    private long lastSmooth = 0L;
    private long lastHighRate = 0L;
    private final List<Double> yawSamples = new ArrayList<>();
    private final List<Double> pitchSamples = new ArrayList<>();
    private boolean cinematicRotation = false;
    private int isTotallyNotCinematic = 0;

    public RotationContainer(final OnixUser user) {
        this.user = user;
        this.sensitivitySamples = new ArrayDeque<Integer>();
    }

    public void handle(double yaw, double pitch) {
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.yaw = (float) yaw;
        this.pitch = (float) pitch;
        this.lastJoltYaw = this.joltYaw;
        this.lastJoltPitch = this.joltPitch;
        this.joltYaw = Math.abs(deltaYaw - lastDeltaYaw);
        this.joltPitch = Math.abs(deltaPitch - lastDeltaPitch);
        this.lastDeltaYaw = this.deltaYaw;
        this.lastDeltaPitch = this.deltaPitch;
        this.deltaYaw = (float) Math.abs(yaw - this.lastYaw);
        this.deltaPitch = (float) Math.abs(pitch - this.lastPitch);
        PlayerRotationEvent preRotation = new PlayerRotationEvent(false, yaw, pitch, deltaYaw, deltaPitch);
        user.handleEvent(preRotation);
        this.lastPitchAccel = this.pitchAccel;
        this.lastYawAccel = this.yawAccel;
        this.yawAccel = Math.abs(this.deltaYaw - this.lastDeltaYaw);
        this.pitchAccel = Math.abs(this.deltaPitch - this.lastDeltaPitch);
        final float f = (float) this.mcpSensitivity * 0.6f + 0.2f;
        final float gcd = f * f * f * 1.2f;
        this.rawMouseDeltaX = this.deltaYaw / gcd;
        this.rawMouseDeltaY = this.deltaPitch / gcd;
        this.mouseDeltaX = (int) (this.deltaYaw / gcd);
        this.mouseDeltaY = (int) (this.deltaPitch / gcd);
        this.processCinematic();
        final float expectedYaw = this.deltaYaw * 1.073742f + (float) (this.deltaYaw + 0.15);
        final float expectedPitch = this.deltaPitch * 1.073742f - (float) (this.deltaPitch - 0.15);
        final float pitchDiff = Math.abs(this.deltaPitch - expectedPitch);
        final float yawDiff = Math.abs(this.deltaYaw - expectedYaw);
        this.lastFuckedPredictedPitch = this.fuckedPredictedPitch;
        this.lastFuckedPredictedYaw = this.fuckedPredictedYaw;
        this.fuckedPredictedPitch = Math.abs(this.deltaPitch - pitchDiff);
        this.fuckedPredictedYaw = Math.abs(this.deltaYaw - yawDiff);
        this.smoothnessYaw = 10.0f - Math.abs(yawAccel - lastYawAccel);
        this.smoothnessPitch = 5.0f - Math.abs(pitchAccel - lastPitchAccel);
        this.consistency = 1.0f - Math.abs(1.0f - (deltaYaw / (deltaPitch + 0.0001f)));

        if (this.deltaPitch > 0.1 && this.deltaPitch < 25.0f) {
            this.processSensitivity();
        }
        PlayerRotationEvent postRotation = new PlayerRotationEvent(true, yaw, pitch, deltaYaw, deltaPitch);
        user.handleEvent(postRotation);
//        lastYaw = this.yaw;
//        lastPitch = this.pitch;
//        if (user.getMovementContainer().getSetbackLocation() != null) {
//            user.getMovementContainer().getSetbackLocation().setYaw((float) yaw);
//            user.getMovementContainer().getSetbackLocation().setPitch((float) pitch);
//        }
//        this.yaw = yaw;
//        this.pitch = pitch;
//        lastDeltaYaw = deltaYaw;
//        lastDeltaPitch = deltaPitch;
//        this.deltaYaw = Math.abs(yaw - this.lastYaw);
//        this.deltaPitch = Math.abs(pitch - this.lastPitch);
//        PlayerRotationEvent preRotation = new PlayerRotationEvent(false, yaw, pitch, deltaYaw, deltaPitch);
//        user.handleEvent(preRotation);
//        if (this.deltaPitch > 0.1 && this.deltaPitch < 25.0f) {
//            this.processSensitivity();
//        }
//        processCinematic();
//        //Тут будет просчет сенсы и тд
//        PlayerRotationEvent postRotation = new PlayerRotationEvent(true, yaw, pitch, deltaYaw, deltaPitch);
//        user.handleEvent(postRotation);
    }

    private void processSensitivity() {
        final float gcd = (float) MathUtil.getGcd(this.deltaPitch, this.lastDeltaPitch);
        final double sensitivityModifier = Math.cbrt(0.8333 * gcd);
        final double sensitivityStepTwo = 1.666 * sensitivityModifier - 0.3333;
        final double finalSensitivity = sensitivityStepTwo * 200.0;
        this.finalSensitivity = finalSensitivity;
        this.sensitivitySamples.add((int) finalSensitivity);
        if (this.sensitivitySamples.size() == 40) {
            this.sensitivity = MathUtil.getMode(this.sensitivitySamples);
            if (this.hasValidSensitivity()) {
                this.mcpSensitivity = MinecraftValues.SENSITIVITY_MCP_VALUES.get(this.sensitivity);
            }
            this.sensitivitySamples.clear();
        }
    }

    private void processCinematic() {
        long now = System.currentTimeMillis();

        double differenceYaw = Math.abs(deltaYaw - lastDeltaYaw);
        double differencePitch = Math.abs(deltaPitch - lastDeltaPitch);

        double joltYaw = Math.abs(differenceYaw - deltaYaw);
        double joltPitch = Math.abs(differencePitch - deltaPitch);

        boolean cinematic = (now - lastHighRate > 250L) || (now - lastSmooth < 9000L);

        if (joltYaw > 1.0 && joltPitch > 1.0) {
            lastHighRate = now;
        }

        yawSamples.add((double) deltaYaw);
        pitchSamples.add((double) deltaPitch);

        if (yawSamples.size() >= 20 && pitchSamples.size() >= 20) {
            Set<Double> shannonYaw = new HashSet<>();
            Set<Double> shannonPitch = new HashSet<>();
            List<Double> stackYaw = new ArrayList<>();
            List<Double> stackPitch = new ArrayList<>();

            for (Double yawSample : yawSamples) {
                stackYaw.add(yawSample);
                if (stackYaw.size() >= 10) {
                    shannonYaw.add(MathUtil.getSE(stackYaw));
                    stackYaw.clear();
                }
            }

            for (Double pitchSample : pitchSamples) {
                stackPitch.add(pitchSample);
                if (stackPitch.size() >= 10) {
                    shannonPitch.add(MathUtil.getSE(stackPitch));
                    stackPitch.clear();
                }
            }

            if (shannonYaw.size() != 1 || shannonPitch.size() != 1 ||
                    !shannonYaw.toArray()[0].equals(shannonPitch.toArray()[0])) {
                isTotallyNotCinematic = 20;
            }

            GraphUtil.GraphResult resultsYaw = GraphUtil.INSTANCE.getGraph(yawSamples);
            GraphUtil.GraphResult resultsPitch = GraphUtil.INSTANCE.getGraph(pitchSamples);

            int negativesYaw = resultsYaw.getNegatives();
            int negativesPitch = resultsPitch.getNegatives();
            int positivesYaw = resultsYaw.getPositives();
            int positivesPitch = resultsPitch.getPositives();

            if (positivesYaw > negativesYaw || positivesPitch > negativesPitch) {
                lastSmooth = now;
            }

            yawSamples.clear();
            pitchSamples.clear();
        }

        if (isTotallyNotCinematic > 0) {
            isTotallyNotCinematic--;
            cinematicRotation = false;
        } else {
            cinematicRotation = cinematic;
        }

    }

    public boolean hasValidSensitivity() {
        return this.sensitivity > 0 && this.sensitivity < 200;
    }

    public boolean isCinematicRotation() {
        return cinematicRotation;
    }

    public boolean hasValidSensitivityNormalaized() {
        return this.sensitivity > 0 && this.sensitivity < 269;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getLastYaw() {
        return this.lastYaw;
    }

    public float getLastPitch() {
        return this.lastPitch;
    }

    public float getDeltaYaw() {
        return this.deltaYaw;
    }

    public float getDeltaPitch() {
        return this.deltaPitch;
    }

    public float getLastDeltaYaw() {
        return this.lastDeltaYaw;
    }

    public float getLastDeltaPitch() {
        return this.lastDeltaPitch;
    }

    public float getYawAccel() {
        return this.yawAccel;
    }

    public float getPitchAccel() {
        return this.pitchAccel;
    }

    public float getLastYawAccel() {
        return this.lastYawAccel;
    }

    public float getLastPitchAccel() {
        return this.lastPitchAccel;
    }

    public float getRawMouseDeltaX() {
        return this.rawMouseDeltaX;
    }

    public float getRawMouseDeltaY() {
        return this.rawMouseDeltaY;
    }

    public float getFuckedPredictedPitch() {
        return this.fuckedPredictedPitch;
    }

    public float getFuckedPredictedYaw() {
        return this.fuckedPredictedYaw;
    }

    public float getLastFuckedPredictedPitch() {
        return this.lastFuckedPredictedPitch;
    }

    public float getLastFuckedPredictedYaw() {
        return this.lastFuckedPredictedYaw;
    }

    public boolean isCinematic() {
        return this.cinematic;
    }

    public double getFinalSensitivity() {
        return this.finalSensitivity;
    }

    public double getMcpSensitivity() {
        return this.mcpSensitivity;
    }

    public ArrayDeque<Integer> getSensitivitySamples() {
        return this.sensitivitySamples;
    }

    public int getSensitivity() {
        return this.sensitivity;
    }

    public int getMouseDeltaX() {
        return this.mouseDeltaX;
    }

    public int getMouseDeltaY() {
        return this.mouseDeltaY;
    }

    public float getSmoothnessYaw() {
        return smoothnessYaw;
    }

    public float getSmoothnessPitch() {
        return smoothnessPitch;
    }

    public float getConsistency() {
        return consistency;
    }

    public void setCinematic(boolean cinematic) {
        this.cinematic = cinematic;
    }
}
