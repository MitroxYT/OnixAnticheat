package dev.onixac.api.events.impl;

import dev.onixac.api.events.api.BaseEvent;
import dev.onixac.api.user.IOnixUser;

public class PlayerPayLoadEvent extends BaseEvent {
   private final String[] strings;
   private final IOnixUser user;
   public PlayerPayLoadEvent(String[] strings,IOnixUser user) {
       this.strings = strings;
       this.user = user;
   }

    public IOnixUser getUser() {
        return user;
    }

    public String[] getStrings() {
        return strings;
    }
}
