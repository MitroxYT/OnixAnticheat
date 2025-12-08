package dev.onixac.api.user;

public interface IClientInput {
    boolean isForward();

    boolean isBackward();

    boolean isLeft();

    boolean isRight();

    boolean isJump();

    boolean isShift();

    boolean isSprint();

    boolean hasInput();


    double getForwardMotion();

    double getStrafe();

    boolean isSlowing();

    void reset();


    double getMotion();
}
