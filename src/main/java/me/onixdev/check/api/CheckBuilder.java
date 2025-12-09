package me.onixdev.check.api;

import dev.onixac.api.check.CheckStage;
import dev.onixac.api.check.custom.CheckMaker;
import lombok.Getter;

public class CheckBuilder {
    @Getter
    private String checkName,type,description;
    @Getter
    private double maxBuffer = 10;
    @Getter double decay = 0.05;
    @Getter
    private CheckStage checkStage;
    public static CheckBuilder create() {
        return new CheckBuilder();
    }

    public static CheckBuilder fromCheckMaker(CheckMaker checkMaker) {
        return create().setCheckName(checkMaker.getCheckName()).setType(checkMaker.getType()).build();
    }

    public CheckBuilder setBuffer(double buffer) {
        this.maxBuffer = buffer;
        return this;
    }
    public CheckBuilder setDecay(double decay) {
        this.decay = decay;
        return this;
    }

    public CheckBuilder setCheckName(String checkName) {
        this.checkName = checkName;
        return this;
    }
    public CheckBuilder setType(String type) {
        this.type = type;
        return this;
    }
    public CheckBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    public CheckBuilder setCheckStage(CheckStage checkStage) {
        this.checkStage = checkStage;
        return this;
    }
    public CheckBuilder build() {
        return this;
    }
}
