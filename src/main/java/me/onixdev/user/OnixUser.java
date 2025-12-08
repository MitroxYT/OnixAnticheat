package me.onixdev.user;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.potion.PotionType;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRemoveEntityEffect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
import dev.onixac.api.user.IClientInput;
import dev.onixac.api.user.IOnixUser;
import lombok.Getter;
import lombok.Setter;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
import me.onixdev.event.api.BaseEvent;
import me.onixdev.manager.CheckManager;
import me.onixdev.user.data.BrigingContainer;
import me.onixdev.user.data.ConnectionContainer;
import me.onixdev.user.data.MovementContainer;
import me.onixdev.user.data.RotationContainer;
import me.onixdev.util.alert.AlertManager;
import me.onixdev.util.net.ClientInput;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
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
    private final User user;
    @Getter
    private final UUID uuid;
    @Getter
    private final String name;
    @Getter
    private final int id;
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
    @Getter
    private final MovementContainer movementContainer;
    @Setter@Getter
    private InteractionHand usingHand = InteractionHand.MAIN_HAND;
    @Getter@Setter
    private boolean isUsingItem = false;
    @Getter@Setter
    private int ItemUseTime;
    public int lastHitTime = 100;
    @Getter
    private String mitigateType;
    private double timetoMitigate,lastMitigateTime;
    public IClientInput theoreticalInput = new ClientInput();
    public IClientInput Input = new ClientInput();
    public static final @Nullable Consumer<Player> resetActiveBukkitItem;
    public static final @Nullable Predicate<Player> isUsingBukkitItem;
    @Getter
    private double walkSpeed = 0.10000000149011612D;
    @Getter
    private int jumpBoost, speedBoost, slowness;

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
        movementContainer = new MovementContainer(this);
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
        return serverTickSinceJoin > 5;
    }
    public boolean shouldMitigate() {
        return System.currentTimeMillis() - lastMitigateTime < timetoMitigate && mitigateType != null && mitigateType.equals("canceldamage") || (mitigateType != null &&mitigateType.equals("reducedamage"));
    }
    public void sendTransaction() {
        connectionContainer.sendTransaction();
    }
    public void onSend(PacketSendEvent event) {
        if (player == null) return;
        if (event.getPacketType() == PacketType.Play.Server.UPDATE_ATTRIBUTES) {
            WrapperPlayServerUpdateAttributes updateAttributes = new WrapperPlayServerUpdateAttributes(event);

            for (WrapperPlayServerUpdateAttributes.Property snapshot : updateAttributes.getProperties()) {
                if (snapshot.getAttribute() == Attributes.MOVEMENT_SPEED)  {
                    connectionContainer.confirmPost(()-> walkSpeed = snapshot.getValue());
                }
            }
        }

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
            WrapperPlayServerEntityEffect entityEffect = new WrapperPlayServerEntityEffect(event);


            if (entityEffect.getEntityId() != id) {
                return;
            }


            int amplifier = entityEffect.getEffectAmplifier();

            PotionType potionType = entityEffect.getPotionType();
            if (potionType == PotionTypes.SPEED) {
                this.speedBoost = amplifier + 1;
            } else if (potionType == PotionTypes.SLOWNESS) {
                this.slowness = amplifier + 1;
            } else if (potionType == PotionTypes.JUMP_BOOST) {
                this.jumpBoost = amplifier + 1;
            }

        }

        if (event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
            WrapperPlayServerRemoveEntityEffect removeEntityEffect = new WrapperPlayServerRemoveEntityEffect(event);

            if (removeEntityEffect.getEntityId() != id) {
                return;
            }
            sendTransaction();
            connectionContainer.confirmPost(()-> {
                PotionType potionType = removeEntityEffect.getPotionType();
                if (potionType == PotionTypes.SPEED) {
                    this.speedBoost = 0;
                } else if (potionType == PotionTypes.SLOWNESS) {
                    this.slowness = 0;
                } else if (potionType == PotionTypes.JUMP_BOOST) {
                    this.jumpBoost = 0;
                }
            });
        }
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
                isUsingBukkitItem0 = HumanEntity::isHandRaised;
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        } finally {
            resetActiveBukkitItem = resetActiveBukkitItem0;
            isUsingBukkitItem = isUsingBukkitItem0;
        }
    }

    public double getMoveSpeed(boolean sprint) {
        double baseValue = walkSpeed;

        if (sprint) {
            baseValue += baseValue * 0.30000001192092896D;
        }

        baseValue += baseValue * speedBoost * 0.20000000298023224D;
        baseValue += baseValue * slowness * -0.15000000596046448D;

        return baseValue;
    }
}
