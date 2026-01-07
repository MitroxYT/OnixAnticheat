package dev.onixac.api.check.custom;

import dev.onixac.api.check.CheckStage;

import java.util.ArrayList;
import java.util.List;

public class CheckMaker {
    private String checkName,type,description;
    private double maxBuffer = 10;
    private CheckStage checkStage;
    private List<ConfigVlCommandData> commandData = new ArrayList<>();
    public static CheckMaker create() {
        return new CheckMaker();
    }
    public CheckMaker setBuffer(double buffer) {
        this.maxBuffer = buffer;
        return this;
    }
    public List<ConfigVlCommandData> getCommandData() {
        return commandData;
    }

    public CheckMaker setCommandData(List<ConfigVlCommandData> commandData) {
        this.commandData = commandData;
        return this;
    }
    public CheckMaker setCheckName(String checkName) {
        this.checkName = checkName;
        return this;
    }
    public CheckMaker setType(String type) {
        this.type = type;
        return this;
    }
    public CheckMaker setDescription(String description) {
        this.description = description;
        return this;
    }
    public CheckMaker setCheckStage(CheckStage checkStage) {
        this.checkStage = checkStage;
        return this;
    }
    public CheckMaker build() {
        return this;
    }
    public String getCheckName() {
        return this.checkName;
    }

    public String getType() {
        return this.type;
    }

    public String getDescription() {
        return this.description;
    }

    public double getMaxBuffer() {
        return this.maxBuffer;
    }

    public CheckStage getCheckStage() {
        return this.checkStage;
    }
}
