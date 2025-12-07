package me.onixdev.user;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.onixac.api.check.ICheck;
import dev.onixac.api.user.IOnixUser;
import lombok.Getter;
import lombok.Setter;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
import me.onixdev.check.impl.player.badpackets.BadPacketA;
import me.onixdev.event.api.BaseEvent;
import me.onixdev.event.impl.PlayerClickEvent;
import me.onixdev.manager.CheckManager;
import me.onixdev.user.data.BrigingContainer;
import me.onixdev.user.data.ConnectionContainer;
import me.onixdev.user.data.RotationContainer;
import me.onixdev.util.alert.AlertManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OnixUser implements IOnixUser {
    public int currentTick;
    private int serverTickSinceJoin;
    public double food;
    @Getter
    private User user;
    @Getter
    private UUID uuid;
    @Getter
    private String name;
    @Getter
    private int id;
    @Getter@Setter
    private boolean alertsEnabled,verboseEnabled;
    @Getter
    private final AlertManager alertManager;
    @Getter
    private Player player;
    @Getter
    private List<Check> checks = new ArrayList<>();
    @Getter
    private final RotationContainer rotationContainer;
    @Getter
    private final ConnectionContainer connectionContainer;
    @Getter
    private final BrigingContainer brigingContainer;
    @Setter@Getter
    private InteractionHand usingHand = InteractionHand.MAIN_HAND;
    @Getter@Setter
    private boolean isUsingItem = false;
    public int lastHitTime = 100;
    @Getter
    private String mitigateType;
    private double timetoMitigate,lastMitigateTime;
    public static final @Nullable Consumer<Player> resetActiveBukkitItem;
    public static final @Nullable Predicate<Player> isUsingBukkitItem;
    public OnixUser(User user) {
        this.user = user;
        this.uuid = this.user.getUUID();
        this.name = this.user.getName();
        this.id = this.user.getEntityId();
        alertManager = new AlertManager(this);
        this.player = Bukkit.getPlayer(this.uuid);
        checks = CheckManager.loadChecks(this);
        rotationContainer = new RotationContainer(this);
        connectionContainer = new ConnectionContainer(this);
        brigingContainer = new BrigingContainer(this);
    }
    public void sendMessage(Component message) {
        if (OnixAnticheat.noSupportComponentMessage) {
            user.sendMessage(message);
            return;
        }
        if (player == null) user.sendMessage(message);
        else player.sendMessage(message);
    }
    public void sendMessage(String message) {
        if (player == null) user.sendMessage(message);
        else player.sendMessage(message);
    }

    public Player getBukkitPlayer() {
        return player;
    }

    public void tick() {
        if (player == null && (Bukkit.getPlayer(this.uuid) != null)) {
            player = Bukkit.getPlayer(this.uuid);
        }
        serverTickSinceJoin++;
        if (serverTickSinceJoin % 3 == 0) {
            sendTransaction();
        }
    }

    public void handleEvent(BaseEvent clickEvent) {
        for (Check check : checks) {
            check.onEvent(clickEvent);
        }
    }

    public boolean hasConfirmPlayState() {
        return true;
    }
    public boolean shouldMitigate() {
        return System.currentTimeMillis() - lastMitigateTime < timetoMitigate && mitigateType != null && mitigateType.equals("canceldamage") || (mitigateType != null &&mitigateType.equals("reducedamage"));
    }
    public void sendTransaction() {
        connectionContainer.sendTransaction();
    }

    @Override
    public void mitigate(String type, double time) {
        this.mitigateType = type;
        this.timetoMitigate = time;
        lastMitigateTime = System.currentTimeMillis();
    }

    public void debug(Object object) {
        player.sendMessage(object.toString());
    }
    public boolean isUsingBukkitItem() {
        return isUsingBukkitItem != null && this.getBukkitPlayer() != null && isUsingBukkitItem.test(this.getBukkitPlayer());
    }
    public <T extends Check> T getCheck(Class<T> check) {
        for (Check check1 : getChecks()) {
            if (check1.getClass() == check) {
                return (T) check1;
            }
        }
        return null;
    }
    static {
        ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();
        Predicate<Player> isUsingBukkitItem0 = null;
        Consumer<Player> resetActiveBukkitItem0 = null;

        try {
            try { // paper 1.16+
                LivingEntity.class.getMethod("clearActiveItem");
                //    resetActiveBukkitItem0 = LivingEntity::clearActiveItem;
            } catch (NoSuchMethodException ignored) {
            }

            if (version == ServerVersion.V_1_8_8) {
                Class<?> EntityHuman = Class.forName("net.minecraft.server.v1_8_R3.EntityHuman");
                Method getHandle = Class.forName("org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer").getMethod("getHandle");
                Method clearActiveItem = EntityHuman.getMethod("bV");
                Method isUsingItem = EntityHuman.getMethod("bS");

                resetActiveBukkitItem0 = player -> {
                    try {
                        clearActiveItem.invoke(getHandle.invoke(player));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };

                isUsingBukkitItem0 = player -> {
                    try {
                        return (boolean) isUsingItem.invoke(getHandle.invoke(player));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };
            } else if (version == ServerVersion.V_1_12_2) {
                Class<?> EntityLiving = Class.forName("net.minecraft.server.v1_12_R1.EntityLiving");
                Method getHandle = Class.forName("org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer").getMethod("getHandle");
                Method clearActiveItem = EntityLiving.getMethod("cN");
                Method getItemInUse = EntityLiving.getMethod("cJ");
                Method isEmpty = Class.forName("net.minecraft.server.v1_12_R1.ItemStack").getMethod("isEmpty");

                resetActiveBukkitItem0 = player -> {
                    try {
                        clearActiveItem.invoke(getHandle.invoke(player));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };

                isUsingBukkitItem0 = player -> {
                    try {
                        var item = getItemInUse.invoke(getHandle.invoke(player));
                        return item != null && !((boolean) isEmpty.invoke(item));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };
            } else if (version == ServerVersion.V_1_16_5) {
                Class<?> EntityLiving = Class.forName("net.minecraft.server.v1_16_R3.EntityLiving");
                Method getHandle = Class.forName("org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer").getMethod("getHandle");
                Method clearActiveItem = EntityLiving.getMethod("clearActiveItem");
                Method getItemInUse = EntityLiving.getMethod("getActiveItem");
                Method isEmpty = Class.forName("net.minecraft.server.v1_16_R3.ItemStack").getMethod("isEmpty");

                resetActiveBukkitItem0 = player -> {
                    try {
                        clearActiveItem.invoke(getHandle.invoke(player));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };

                isUsingBukkitItem0 = player -> {
                    try {
                        Object item = getItemInUse.invoke(getHandle.invoke(player));
                        return item != null && !((boolean) isEmpty.invoke(item));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };
            } else if (version.isNewerThanOrEquals(ServerVersion.V_1_17_1)) {
                isUsingBukkitItem0 = player -> player.isHandRaised();
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        } finally {
            resetActiveBukkitItem = resetActiveBukkitItem0;
            isUsingBukkitItem = isUsingBukkitItem0;

            if (resetActiveBukkitItem == null) {
                //   Alice.get().getGhostLogger().error("could not find method to reset item usage (are you using spigot?)");
            }

            if (isUsingBukkitItem == null) {
                //  Alice.get().getGhostLogger().error("could not find method to get item usage status (are you using an unsupported version?)");
            }
        }
    }
}
