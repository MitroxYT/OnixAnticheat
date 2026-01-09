package dev.onixac.api.user;

import dev.onixac.api.check.ICheck;
import dev.onixac.api.check.custom.CheckMaker;
import dev.onixac.api.events.api.BaseEvent;
import dev.onixac.api.user.data.IPlayerClickData;
import dev.onixac.api.user.data.IPlayerInventory;
import dev.onixac.api.user.data.IPlayerRotationData;

import java.util.Optional;

public interface IOnixUser {
    String getName();
    void mitigate(String type,double time);
    ICheck getCheck(String name,String type);
    IPlayerRotationData getRotationData();
    IPlayerClickData getClickData();
    IPlayerInventory getInventory();
    void registerCheck(CheckMaker checkMaker);
    void sendMessage(String message);
    double getSensitivity();
    Optional<Object> getValue(String name);
    String getBrand();
    void setBrand(String brand);
    void runTaskPre(Runnable runnable);
    void runTaskPost(Runnable runnable);
    void runTask(Runnable runnable,int offset);
    void handleEvent(BaseEvent clickEvent);
}
