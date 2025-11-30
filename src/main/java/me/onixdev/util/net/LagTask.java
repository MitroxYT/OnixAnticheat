package me.onixdev.util.net;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LagTask {
    private int transaction;
    private Runnable task;
}
