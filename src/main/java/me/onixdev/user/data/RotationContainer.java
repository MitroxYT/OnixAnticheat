package me.onixdev.user.data;

import lombok.Getter;
import me.onixdev.event.impl.PlayerRotationEvent;
import me.onixdev.user.OnixUser;
import me.onixdev.util.math.GraphUtil;
import me.onixdev.util.math.MathUtil;
import me.onixdev.util.net.MinecraftValues;

import java.util.*;

@Getter
public class RotationContainer {
    private final OnixUser user;
    public double yaw,pitch,lastYaw,lastPitch;
    private double deltaYaw,deltaPitch,lastDeltaYaw,lastDeltaPitch;
    private double finalSensitivity;
    private double mcpSensitivity;
    private final ArrayDeque<Integer> sensitivitySamples;
    private int sensitivity;
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
        lastYaw = this.yaw;
        lastPitch = this.pitch;
        if (user.getMovementContainer().getSetbackLocation() != null) {
            user.getMovementContainer().getSetbackLocation().setYaw((float) yaw);
            user.getMovementContainer().getSetbackLocation().setPitch((float) pitch);
        }
        this.yaw = yaw;
        this.pitch = pitch;
        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;
        this.deltaYaw = Math.abs(yaw - this.lastYaw);
        this.deltaPitch = Math.abs(pitch - this.lastPitch);
        PlayerRotationEvent preRotation = new PlayerRotationEvent(false, yaw, pitch, deltaYaw, deltaPitch);
        user.handleEvent(preRotation);
        if (this.deltaPitch > 0.1 && this.deltaPitch < 25.0f) {
            this.processSensitivity();
        }
        processCinematic();
        //Тут будет просчет сенсы и тд
        PlayerRotationEvent postRotation = new PlayerRotationEvent(true, yaw, pitch, deltaYaw, deltaPitch);
        user.handleEvent(postRotation);
    }
    private void processSensitivity() {
        final float gcd = (float) MathUtil.getGcd(this.deltaPitch, this.lastDeltaPitch);
        final double sensitivityModifier = Math.cbrt(0.8333 * gcd);
        final double sensitivityStepTwo = 1.666 * sensitivityModifier - 0.3333;
        final double finalSensitivity = sensitivityStepTwo * 200.0;
        this.finalSensitivity = finalSensitivity;
        this.sensitivitySamples.add((int)finalSensitivity);
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
}
