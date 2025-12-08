package me.onixdev.util.net;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
@Getter
public class PlayerTeleportTask {
    private int transaction;
    private int teleportId;
    private Location location;
    public PlayerTeleportTask(int transaction, int teleportId, Location location) {
        this.transaction = transaction;
        this.teleportId = teleportId;
        this.location = location;
    }
    @Setter
    private boolean confirmTransaction, confirmTeleport,ConfirmLocation;
}
