package me.onixdev.compability.impl;

import me.onixdev.OnixAnticheat;
import me.onixdev.compability.ICompabilityCheck;
import me.onixdev.compability.manager.CompatibilityManager;

import java.lang.reflect.Field;

public class LeafCompabilityWorldTicking implements ICompabilityCheck {
    @Override
    public void check(CompatibilityManager compatibilityManager) {
        try {
            Class clazz = Class.forName("org.dreeam.leaf.config.modules.async.SparklyPaperParallelWorldTicking");
            Field field = clazz.getField("enabled");
            boolean enabled= field.getBoolean(clazz);
            if (enabled) {
                compatibilityManager.setLeafTicking(true);
                OnixAnticheat.INSTANCE.printCool("&bУ Вас включен паралельный тикинг включаю поддержку");
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {

        }

    }
}
