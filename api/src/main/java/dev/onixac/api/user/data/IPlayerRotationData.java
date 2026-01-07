package dev.onixac.api.user.data;

import java.util.ArrayDeque;

public interface IPlayerRotationData {
     boolean hasValidSensitivity();

     boolean isCinematicRotation();

     boolean hasValidSensitivityNormalaized();

     float getYaw();

     float getPitch();

     float getLastYaw();

     float getLastPitch();

     float getDeltaYaw();

     float getDeltaPitch();

     float getLastDeltaYaw();

     float getLastDeltaPitch();

     float getYawAccel();

     float getPitchAccel();

     float getLastYawAccel();

     float getLastPitchAccel();

     float getRawMouseDeltaX();

     float getRawMouseDeltaY();

     float getFuckedPredictedPitch();

     float getFuckedPredictedYaw();

     float getLastFuckedPredictedPitch();

     float getLastFuckedPredictedYaw();

     boolean isCinematic();

     double getFinalSensitivity();

     double getMcpSensitivity();

     ArrayDeque<Integer> getSensitivitySamples();

     int getSensitivity();

     int getMouseDeltaX();

     int getMouseDeltaY();

     float getSmoothnessYaw();

     float getSmoothnessPitch();

     float getConsistency();

     void setCinematic(boolean cinematic);
}
