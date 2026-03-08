package dev.onixac.api.check;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CheckInfo {
    String name() default "UNKNOWN";

    String type() default "s";

    String description() default "No description provided";

    double decay() default 0.05;
    double decayBuffer() default 0.25;
    boolean setback() default false;
    double setbackVl() default 25;

    double maxBuffer() default 10;

    CheckStage stage() default CheckStage.RELEASE;

    String customCfgName() default "DEFAULT";
}
