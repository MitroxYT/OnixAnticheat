package dev.onixac.api.check;

public interface ICheck {
    String getName();
    String getDescription();
    String getType();
    double getVl();
    CheckStage getStage();
    double getDecay();
    double getMaxBuffer();
    boolean isEnabled();
    boolean isExperimental();
    boolean fail(Object debug);
}
