package me.onixdev.manager;


import me.onixdev.check.api.Check;
import me.onixdev.check.impl.combat.aim.*;
import me.onixdev.check.impl.combat.aura.AuraA;
import me.onixdev.check.impl.combat.aura.AuraB;
import me.onixdev.check.impl.movement.noslow.NoslowPrediction;
import me.onixdev.check.impl.movement.noslow.NoslowTick;
import me.onixdev.check.impl.player.airstuck.AirStuckA;
import me.onixdev.check.impl.player.badpackets.*;
import me.onixdev.check.impl.player.block.GhostHandB;
import me.onixdev.check.impl.player.block.GhostHandC;
import me.onixdev.check.impl.player.inventory.InventoryA;
import me.onixdev.check.impl.player.inventory.InventoryB;
import me.onixdev.check.impl.player.inventory.InventoryC;
import me.onixdev.check.impl.player.misc.ClientBrandParser;
import me.onixdev.check.impl.player.scaffold.ScaffoldA;
import me.onixdev.check.impl.player.block.GhostHandA;
import me.onixdev.user.OnixUser;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

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
        final List<Check> checkList = new ArrayList<>();

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
        CHECKS = new Class[] {AimA.class, ClientBrandParser.class, AimB.class, AimC.class, AimD.class, AimE.class, AuraA.class, AuraB.class, AimT.class, AimF.class,BadPacketA.class, AirStuckA.class, BadPacketB.class, BadPacketC.class, BadPacketD.class, BadPacketE.class, NoslowPrediction.class, NoslowTick.class,ScaffoldA.class,
                InventoryA.class, GhostHandB.class, GhostHandA.class, GhostHandC.class, InventoryB.class, InventoryC.class
        };

    }
}
