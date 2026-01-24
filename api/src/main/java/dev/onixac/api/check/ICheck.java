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
    void setEnabled(boolean value);
    void setCancel(boolean value);
    void setSetback(boolean value);
    void setVl(double value);
    boolean isExperimental();
    boolean fail(Object debug);
}
