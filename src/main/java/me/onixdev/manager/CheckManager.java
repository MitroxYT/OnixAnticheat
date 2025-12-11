package me.onixdev.manager;


import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.check.impl.combat.aim.*;
import me.onixdev.check.impl.movement.noslow.NoslowPrediction;
import me.onixdev.check.impl.movement.noslow.NoslowTick;
import me.onixdev.check.impl.player.autototem.AutoTotemA;
import me.onixdev.check.impl.player.badpackets.*;
import me.onixdev.check.impl.player.inventory.InventoryA;
import me.onixdev.check.impl.player.inventory.InventoryB;
import me.onixdev.check.impl.player.inventory.InventoryC;
import me.onixdev.check.impl.player.scaffold.ScaffoldA;
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
    public void CreateCheck(OnixUser data) {
        data.getChecks().add(new Check(data, CheckBuilder.create().setCheckName("aaaa").setType("A")));
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
        CHECKS = new Class[] {AimA.class, AimB.class, AimC.class, AimD.class, AimE.class, AimT.class,BadPacketA.class, BadPacketB.class, BadPacketC.class, BadPacketD.class, BadPacketE.class, NoslowPrediction.class, NoslowTick.class,ScaffoldA.class,
                InventoryA.class, AutoTotemA.class, InventoryB.class, InventoryC.class
        };

    }
}
