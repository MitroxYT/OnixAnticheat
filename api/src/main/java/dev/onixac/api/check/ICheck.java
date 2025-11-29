package dev.onixac.api.check;

public interface ICheck {
    String getName();
    String getDescription();
    String getType();
    double getVl();
    CheckStage getStage();
    double getDecay();
    boolean isEnabled();
}
