package dev.onixac.api.user;

import dev.onixac.api.check.ICheck;

public interface IOnixUser {
    String getName();
    void mitigate(String type,double time);
    ICheck getCheck(String name,String type);
}
