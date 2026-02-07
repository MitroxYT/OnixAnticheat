package dev.onixac.api.events.impl;

import dev.onixac.api.events.api.BaseEvent;

public class PlayerPayLoadEvent extends BaseEvent {
   private final String[] strings;
   public PlayerPayLoadEvent(String[] strings) {
       this.strings = strings;
   }

    public String[] getStrings() {
        return strings;
    }
}
