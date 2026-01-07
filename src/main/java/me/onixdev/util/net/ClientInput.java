package me.onixdev.util.net;

import dev.onixac.api.user.IClientInput;

public class ClientInput implements IClientInput {
    private boolean forward;
    private boolean backward;
    private boolean left;
    private boolean right;
    private boolean jump;
    private boolean shift;
    private boolean sprint;
    private final boolean slowing;
    private double forwardMotion;
    private double strafe;
    private final double motion;

    public ClientInput(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean shift, boolean sprint,boolean slowing, double forwardMotion, double strafe,double motion) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.jump = jump;
        this.shift = shift;
        this.sprint = sprint;
        this.forwardMotion = forwardMotion;
        this.strafe = strafe;
        this.motion = motion;
        this.slowing = slowing;
    }

    public ClientInput() {
        this(false, false, false, false, false, false, false,false,0,0,0);
    }

    public boolean isForward() {
        return forward;
    }

    public boolean isBackward() {
        return backward;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isJump() {
        return jump;
    }

    public boolean isShift() {
        return shift;
    }

    public boolean isSprint() {
        return sprint;
    }
    public boolean hasInput() {
        return forward || backward || left || right;
    }


    public double getForwardMotion() {
        return forwardMotion;
    }

    public double getStrafe() {
        return strafe;
    }

    public boolean isSlowing() {
        return slowing;
    }

    public void reset() {
        forward = backward = left = right = jump = shift = sprint = false;
        forwardMotion = strafe = 0;
    }


    public double getMotion() {
        return motion;
    }

    @Override
    public String toString() {
        return "ClientKeyInput{" +
                "forward=" + forward +
                ", backward=" + backward +
                ", left=" + left +
                ", right=" + right +
                ", jump=" + jump +
                ", shift=" + shift +
                ", sprint=" + sprint +
                '}';
    }
}
