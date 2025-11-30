package me.onixdev.manager;


import me.onixdev.check.api.Check;
import me.onixdev.check.impl.combat.aura.AuraA;
import me.onixdev.check.impl.player.inventory.InventoryA;
import me.onixdev.user.OnixUser;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class CheckManager
{
    OnixUser data;
    public CheckManager(OnixUser data) {
        this.data = data;
    }
    public static final List<Constructor<?>> CONSTRUCTORS;
    public static final Class<? extends Check>[] CHECKS;
    private static Map<Class<? extends Check>, Check> checkMap = new HashMap<>();

    public static List<Check> loadChecks(final OnixUser data) {
        final List<Check> checkList = new ArrayList<Check>();

        for (final Constructor<?> constructor : CheckManager.CONSTRUCTORS) {
            try {
                Check checkInstance = (Check) constructor.newInstance(data);
                checkList.add(checkInstance);
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
        return checkList;
    }
    public static void setup() {
        for (final Class<? extends Check> clazz : CheckManager.CHECKS) {
            try {
                Constructor<?> constructor = clazz.getConstructor(OnixUser.class);
                CheckManager.CONSTRUCTORS.add(constructor);
            } catch (final NoSuchMethodException exception) {
                exception.printStackTrace();
            }
        }
    }


    static {
        CONSTRUCTORS = new ArrayList<Constructor<?>>();
        CHECKS = new Class[] {
                AuraA.class, InventoryA.class
        };

    }
}
