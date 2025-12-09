package dev.onixac.api.user;

import dev.onixac.api.check.ICheck;
import dev.onixac.api.check.custom.CheckMaker;

public interface IOnixUser {
    String getName();
    void mitigate(String type,double time);
    ICheck getCheck(String name,String type);
    void registerCheck(CheckMaker checkMaker);
    void sendMessage(String message);
}
