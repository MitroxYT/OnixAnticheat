package me.onixdev.user;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.potion.PotionType;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import dev.onixac.api.check.ICheck;
import dev.onixac.api.check.custom.CheckMaker;
import dev.onixac.api.events.api.BaseEvent;
import dev.onixac.api.events.impl.PlayerOnixEventCall;
import dev.onixac.api.user.IClientInput;
import dev.onixac.api.user.IOnixUser;
import dev.onixac.api.user.data.IPlayerClickData;
import dev.onixac.api.user.data.IPlayerRotationData;
import lombok.Getter;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.check.impl.player.misc.data.PlayerPacketData;
import me.onixdev.event.impl.PlayerActionPacket;
import me.onixdev.manager.CheckManager;
import me.onixdev.user.data.*;
import me.onixdev.util.alert.AlertManager;
import me.onixdev.util.color.MessageUtil;
import me.onixdev.util.items.PlayerInventory;
import me.onixdev.util.net.*;
import me.onixdev.util.net.ping.PingUtil;
import me.onixdev.util.rotation.Rotation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OnixUser implements IOnixUser {
    public int currentTick;
    public PlayerConnectionStep connectionStage;
    public PlayerConnectionStep ServerconnectionStage;
    public int lastTeleportTime;
    @Getter
    private int serverTickSinceJoin;
    public int food;
    private final User user;
    @Getter
    private final UUID uuid;
    @Getter
    private final String name;
    @Getter
    private int id;
    private boolean alertsEnabled, verboseEnabled;
    private final AlertManager alertManager;
    private boolean checkAlertsTogglingWhileBukkitPlayerNotNull;
    private boolean debug;
    private long lastStopSprint;

    public AlertManager getAlertManager() {
        return alertManager;
    }

    public boolean isAlertsEnabled() {
        return this.alertsEnabled;
    }


    public boolean isVerboseEnabled() {
        return this.verboseEnabled;
    }


    public void setAlertsEnabled(boolean alertsEnabled) {
        this.alertsEnabled = alertsEnabled;
    }


    public void setVerboseEnabled(boolean verboseEnabled) {
        this.verboseEnabled = verboseEnabled;
    }

    @Getter
    private Player player;
    @Getter
    public List<Check> checks = new ArrayList<>();
    private final RotationContainer rotationContainer;
    public final ClickData clickData;
    private final ConnectionContainer connectionContainer;
    private final BrigingContainer brigingContainer;
    private final MovementContainer movementContainer;
    private final PlayerInventory inventory;
    public final AntiFalsePositivesHandler antiFalsePositivesHandler;
    public final CombatData combatData;
    public final PlayerPacketData packetData;
    private InteractionHand usingHand = InteractionHand.MAIN_HAND;
    public boolean isUsingItem = false;
    public int ItemUseTime;
    public boolean isDead;
    public int lastHitTime = 100;
    @Getter
    public String mitigateType;
    private double timetoMitigate, lastMitigateTime;
    public IClientInput theoreticalInput = new ClientInput();
    public IClientInput Input = new ClientInput();
    private String brand = "unparsed";
    public PingUtil lagCompensation;
    public static final @Nullable Consumer<Player> resetActiveBukkitItem;
    public static final @Nullable Predicate<Player> isUsingBukkitItem;
    @Getter
    private final double walkSpeed = 0.10000000149011612D;
    @Getter
    private int jumpBoost, speedBoost, slowness;
    public Rotation rotation = new Rotation(0, 0);

    public OnixUser(User user) {
        this.user = user;
        this.uuid = this.user.getUUID();
        this.name = this.user.getName();
        alertManager = new AlertManager(this);
        this.player = Bukkit.getPlayer(this.uuid);
        checks = CheckManager.loadChecks(this);
        packetData = getCheck(PlayerPacketData.class);
        rotationContainer = new RotationContainer(this);
        connectionContainer = new ConnectionContainer(this);
        brigingContainer = new BrigingContainer(this);
        movementContainer = new MovementContainer(this);
        inventory = new PlayerInventory(this);
        antiFalsePositivesHandler = new AntiFalsePositivesHandler(this);
        combatData = new CombatData(this);
        clickData = new ClickData(this);
        lagCompensation = new PingUtil(this);
        checkAlertsTogglingWhileBukkitPlayerNotNull = true;
        if ((Bukkit.getPlayer(this.uuid) != null)) {
            player = Bukkit.getPlayer(this.uuid);
        }
        for (Check check : checks) {
            check.reload();
        }
        OnixAnticheat.INSTANCE.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(OnixAnticheat.INSTANCE.getPlugin(),()-> {
            if (player != null) {
                if (OnixAnticheat.INSTANCE.getConfigManager().enableAlertsOnJoin && (player != null && player.hasPermission("onix.alerts.join"))) {
                    alertManager.toggleAlerts();
                    checkPermissions();
                }
            }
        },20 * 3);
    }
    public void checkPermissions() {
        if (player == null) return;
        for (Check check : checks) {
            check.setEnabled(!player.hasPermission("onix.bypass." + check.getName() + "." + check.getType() + ".enabled"));
            check.setSetback(!player.hasPermission("onix.bypass." + check.getName() + "." + check.getType() + ".setback"));
            check.setSetback(!player.hasPermission("onix.bypass." + check.getName() + "." + check.getType() + ".cancel"));
        }
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

    @Override
    public double getSensitivity() {
        // я хз почему оно становится большее 200
        if (rotationContainer.getFinalSensitivity() > 200) return 200;
        return rotationContainer.getFinalSensitivity();
    }

    @Override
    public Optional<Object> getValue(String name) {
        switch (name.toLowerCase()) {
            case "food" -> {
                return Optional.of(food);
            }
            case "usingitem" -> {
                return Optional.of(isUsingItem);
            }
            case "itemusetime" -> {
                return Optional.of(ItemUseTime);
            }
            case "hitticks" -> {
                return Optional.of(lastHitTime);
            }
            case "lastmsattack" -> {
                return Optional.of(combatData.getPassedAttackSince());
            }
            case "sprintstop" -> {
                return Optional.of(System.currentTimeMillis() - lastStopSprint);
            }

        }
        return Optional.empty();
    }

    @Override
    public String getBrand() {
        return brand;
    }

    @Override
    public void setBrand(String brand) {
        if (brand == null || brand.isBlank()) return;
        this.brand = brand;
    }

    /**
     * @param runnable
     */
    @Override
    public void runTaskPre(Runnable runnable) {
        lagCompensation.addTask(connectionContainer.lastTransactionSent.get() + 1, runnable);
    }

    /**
     * @param runnable
     */
    @Override
    public void runTaskPost(Runnable runnable) {
        lagCompensation.addTask(connectionContainer.lastTransactionSent.get() + 2, runnable);
    }

    /**
     * @param runnable
     * @param offset
     */
    @Override
    public void runTask(Runnable runnable, int offset) {
        lagCompensation.addTask(connectionContainer.lastTransactionSent.get() + offset, runnable);
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
            if (player != null) {
                id = player.getEntityId();
            }
            sendTransaction();
        }
        if (player != null && checkAlertsTogglingWhileBukkitPlayerNotNull) {
            if (!player.hasPermission("onix.alerts.join")) {
                checkAlertsTogglingWhileBukkitPlayerNotNull = false;
                return;
            }
            if (OnixAnticheat.INSTANCE.getConfigManager().enableAlertsOnJoin && player.hasPermission("onix.alerts.join")) {
                checkAlertsTogglingWhileBukkitPlayerNotNull = false;
            }
        }
    }

    public void handleEvent(BaseEvent clickEvent) {
        OnixAnticheat.INSTANCE.getPlugin().getServer().getScheduler().runTask(OnixAnticheat.INSTANCE.getPlugin(), () -> {
            Bukkit.getPluginManager().callEvent(new PlayerOnixEventCall(clickEvent, this, true));
        });
        if (clickEvent instanceof PlayerActionPacket) {
            if (((PlayerActionPacket) clickEvent).action == WrapperPlayClientEntityAction.Action.STOP_SPRINTING) {
                lastStopSprint = System.currentTimeMillis();
            }
        }
        antiFalsePositivesHandler.onEvent(clickEvent);
        combatData.onEvent(clickEvent);
        for (Check check : checks) {
            check.onEvent(clickEvent);
        }
        OnixAnticheat.INSTANCE.getPlugin().getServer().getScheduler().runTask(OnixAnticheat.INSTANCE.getPlugin(), () -> {
            Bukkit.getPluginManager().callEvent(new PlayerOnixEventCall(clickEvent, this, false));
        });
    }

    public boolean hasConfirmPlayState() {
        return serverTickSinceJoin > 5;
    }

    public boolean shouldMitigate() {
        return System.currentTimeMillis() - lastMitigateTime < timetoMitigate && mitigateType != null && mitigateType.equals("canceldamage") || (mitigateType != null && mitigateType.equals("reducedamage"));
    }

    public void sendTransaction() {
        connectionContainer.sendTransaction();
    }

    public void onSend(PacketSendEvent event) {
        if (player == null) return;
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_STATUS) {
            WrapperPlayServerEntityStatus wrapperPlayServerEntityStatus = new WrapperPlayServerEntityStatus(event);
            if (wrapperPlayServerEntityStatus.getEntityId() == id) {
                int status = wrapperPlayServerEntityStatus.getStatus();
                if (status == EntityStatuses.BREAK_SHIELD) {
                    setUsingItem(false);
                }
            }
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            WrapperPlayServerEntityMetadata wrapperPlayServerEntityMetadata = new WrapperPlayServerEntityMetadata(event);
            if (wrapperPlayServerEntityMetadata.getEntityId() == id) {
                for (EntityData<?> data : wrapperPlayServerEntityMetadata.getEntityMetadata()) {

                    EntityDataType<?> type = data.getType();
                    if (type == EntityDataTypes.PARTICLE || type == EntityDataTypes.PARTICLES) return;
                    EntityData<?> using = BukkitNms.getIndex(wrapperPlayServerEntityMetadata.getEntityMetadata(), 8);
                    if (using != null) {
                        if (using.getValue() != null) {
                            Object value = using.getValue();
                            if (value != null) {
                                if (value instanceof Byte) {
                                    byte b = (Byte) value;
                                    sendTransaction();
                                    if (b == 1) {
                                        setUsingHand(InteractionHand.MAIN_HAND);
                                        setUsingItem(true);
                                    } else if (b == 0) {
                                        setUsingItem(false);
                                    } else if (b == 3) {
                                        setUsingHand(InteractionHand.OFF_HAND);
                                        setUsingItem(true);
                                    } else if (b == 2) {
                                        setUsingHand(InteractionHand.OFF_HAND);
                                        setUsingItem(false);
                                    } else {
                                    }
                                } else {
                                }
                            }
                        }
                    }
                }

            }
        }
        if (event.getPacketType() == PacketType.Play.Server.UPDATE_ATTRIBUTES) {
            WrapperPlayServerUpdateAttributes updateAttributes = new WrapperPlayServerUpdateAttributes(event);

            for (WrapperPlayServerUpdateAttributes.Property snapshot : updateAttributes.getProperties()) {
                if (snapshot.getAttribute() == Attributes.MOVEMENT_SPEED) {
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
            PotionType potionType = removeEntityEffect.getPotionType();
            if (potionType == PotionTypes.SPEED) {
                this.speedBoost = 0;
            } else if (potionType == PotionTypes.SLOWNESS) {
                this.slowness = 0;
            } else if (potionType == PotionTypes.JUMP_BOOST) {
                this.jumpBoost = 0;
            }
        }
    }


    @Override
    public void mitigate(String type, double time) {
        this.mitigateType = type;
        this.timetoMitigate = time;
        lastMitigateTime = System.currentTimeMillis();
    }

    public boolean inVehicle() {
        return player != null && player.getVehicle() != null;
    }

    public boolean isDead() {
        return player != null && isDead;
    }

    public boolean isSpectator() {
        return player != null && player.getGameMode() == GameMode.SPECTATOR;
    }

    @Override
    public ICheck getCheck(String name, String type) {
        for (Check check : checks) {
            if (name.equals(check.getName()) && type.equals(check.getType())) {
                return check;
            }
        }
        return null;
    }

    /**
     * @return
     */
    @Override
    public IPlayerRotationData getRotationData() {
        return rotationContainer;
    }

    /**
     * @return
     */
    @Override
    public IPlayerClickData getClickData() {
        return clickData;
    }

    @Override
    public void registerCheck(CheckMaker checkMaker) {
        Check customCheck = new Check(this, CheckBuilder.fromCheckMaker(checkMaker));
        checks.add(customCheck);
    }

    public void debug(Object object) {
        if (player == null) return;
        if (!debug) return;
        player.sendMessage(object.toString());
    }

    public boolean isUsingBukkitItem() {
        return isUsingBukkitItem != null && this.getBukkitPlayer() != null && isUsingBukkitItem.test(this.getBukkitPlayer());
    }

    public User getUser() {
        return user;
    }

    @SuppressWarnings("unchecked")
    public <T extends Check> T getCheck(Class<T> check) {
        for (Check check1 : checks) {
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

    public double getMoveSpeed(boolean sprint, boolean isPredicting) {
        double baseValue = walkSpeed;

        if (sprint) {
            baseValue += baseValue * 0.30000001192092896D;
        }
        if (isPredicting) {
            baseValue += baseValue * 0 * 0.20000000298023224D;
        } else {
            baseValue += baseValue * speedBoost * 0.20000000298023224D;
        }
        baseValue += baseValue * slowness * -0.15000000596046448D;

        return baseValue;
    }

    public void disconnect(String string) {
        Component component = MiniMessage.miniMessage().deserialize(string).compact();
        user.sendPacket(new WrapperPlayServerDisconnect(component));
    }

    public void disconnect(KickTypes type, String string) {
        switch (type) {
            case InvalidItemUse ->
                    string = user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21) ? "<lang:disconnect.packetError>" : "<lang:disconnect.lost>";
        }
        OnixAnticheat.INSTANCE.getPlugin().getLogger().info("Disconnecting: " + name + " type: " + type + " debug: " + string);
        Component component = MiniMessage.miniMessage().deserialize(string).compact();
        user.sendPacket(new WrapperPlayServerDisconnect(component));
    }


    public RotationContainer getRotationContainer() {
        return this.rotationContainer;
    }


    public ConnectionContainer getConnectionContainer() {
        return this.connectionContainer;
    }


    public BrigingContainer getBrigingContainer() {
        return this.brigingContainer;
    }


    public MovementContainer getMovementContainer() {
        return this.movementContainer;
    }

    public void setUsingHand(InteractionHand usingHand) {
        this.usingHand = usingHand;
    }

    public InteractionHand getUsingHand() {
        return this.usingHand;
    }

    public boolean isUsingItem() {
        return this.isUsingItem;
    }

    public void setUsingItem(boolean isUsingItem) {
        this.isUsingItem = isUsingItem;
    }


    public PlayerInventory getInventory() {
        return this.inventory;
    }

    public void toggleDebug() {
        this.debug = !this.debug;
        sendMessage(MessageUtil.translate("<red> debug = " + debug));
    }

    public boolean isDebug() {
        return this.debug;
    }

    public ClientVersion getClientVersion() {
        if (user == null) return ClientVersion.UNKNOWN;
        return user.getClientVersion();
    }
}
