package me.onixdev.user;

import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.onixac.api.check.ICheck;
import lombok.Getter;
import lombok.Setter;
import me.onixdev.check.api.Check;
import me.onixdev.event.api.BaseEvent;
import me.onixdev.event.impl.PlayerClickEvent;
import me.onixdev.manager.CheckManager;
import me.onixdev.user.data.ConnectionContainer;
import me.onixdev.user.data.RotationContainer;
import me.onixdev.util.alert.AlertManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OnixUser {
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
    @Setter@Getter
    private InteractionHand usingHand = InteractionHand.MAIN_HAND;
    @Getter@Setter
    private boolean isUsingItem = false;
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
    }
    public void sendMessage(Component message) {
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

    public void sendTransaction() {
        connectionContainer.sendTransaction();
    }
}
