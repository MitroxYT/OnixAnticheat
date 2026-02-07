package me.onixdev.util.world.utils.versions;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.manager.server.VersionComparison;
import me.onixdev.OnixAnticheat;
import me.onixdev.user.OnixUser;
import me.onixdev.util.world.utils.versions.impl.Onix1_19_4BlockData;
import me.onixdev.util.world.utils.versions.impl.Onix_1_16_5BlockData;

public class BlockVersionManager {
    private VersionCFR cfr;

    public BlockVersionManager() {
        this.loadCRFSToCache();
        OnixAnticheat.INSTANCE.printCool("&bОбнаружена версия сервера: " +getHumanVersion(String.valueOf(OnixAnticheat.INSTANCE.getServerVersion())) + " Загружена блок дата для: " + getHumanVersion(cfr.getName()));
    }
    public String getHumanVersion(String raw) {
        return raw.replaceAll("_",".").replaceAll("v","");
    }

    public static VersionCFR getCurrentCFR() {
        return OnixAnticheat.INSTANCE.getBlockVersionManager().cfr;
    }

    private void loadCRFSToCache() {

        ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();
        if (version.isOlderThanOrEquals(ServerVersion.V_1_11_2)) {
        }
        if (version.isNewerThan(ServerVersion.V_1_9_2) && version.isOlderThan(ServerVersion.V_1_16_5)) {
        }
        if (version.isNewerThanOrEquals(ServerVersion.V_1_16_5) && version.isOlderThan(ServerVersion.V_1_19_2)) {
            this.cfr = new Onix_1_16_5BlockData();
        }
        if (version.isNewerThan(ServerVersion.V_1_19_2)) {
            this.cfr = new Onix1_19_4BlockData();
        }
    }




    public VersionCFR getCurrentVersion() {
        return this.cfr;
    }
}
