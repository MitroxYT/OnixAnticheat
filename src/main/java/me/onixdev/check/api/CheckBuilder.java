package me.onixdev.check.api;

import dev.onixac.api.check.CheckStage;
import lombok.Getter;

public class CheckBuilder {
    @Getter
    private String checkName,type,description;
    @Getter
    private CheckStage checkStage;
    public static CheckBuilder create() {
        return new CheckBuilder();
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
