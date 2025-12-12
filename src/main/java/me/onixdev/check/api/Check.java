package me.onixdev.check.api;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import dev.onixac.api.check.CheckStage;
import dev.onixac.api.check.ICheck;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.onixdev.OnixAnticheat;
import dev.onixac.api.events.api.BaseEvent;
import me.onixdev.user.OnixUser;
import me.onixdev.util.alert.id.PunishIdSystem;
import me.onixdev.util.net.KickTypes;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Check implements ICheck {
    private boolean enabled;
    protected boolean cancel;
    protected double vl;
    protected String checkName;
    protected String description;
    protected String type;
    protected CheckStage stage;
    protected double maxbuffer;
    protected double decay;
    protected final OnixUser player;
    private final List<ConfigVlCommandData> commands = new ArrayList<>();
    public double setbackVL;
    private boolean setback;

    public Check(OnixUser player, CheckBuilder builder) {
        this.player = player;
        if (builder != null) {
            checkName = builder.getCheckName();
            description = builder.getDescription();
            stage = builder.getCheckStage();
            type = builder.getType();
            maxbuffer = builder.getMaxBuffer();
            reload();
        }
    }

    @Override
    public String getName() {
        return checkName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public double getVl() {
        return vl;
    }

    @Override
    public CheckStage getStage() {
        return null;
    }

    @Override
    public double getDecay() {
        return decay;
    }

    @Override
    public double getMaxBuffer() {
        return maxbuffer;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isExperimental() {
        return stage != null && stage == CheckStage.EXPERIMENTAL;
    }

    public boolean failAndSetback(Object debug) {
        if (!shouldFlag()) return false;
        OnixAnticheat.INSTANCE.getAlertExecutor().run(() -> {
            ++vl;
            executeCommands(debug.toString());
            if (vl > setbackVL && setback) player.getMovementContainer().setback();
        });
        return true;
    }
    @Override
    public boolean fail(Object debug) {
        if (!shouldFlag()) return false;
        OnixAnticheat.INSTANCE.getAlertExecutor().run(() -> {
            ++vl;
            executeCommands(debug.toString());
        });
        return true;
    }

    public void onEvent(BaseEvent event) {
    }
    public void onPacketIn(PacketReceiveEvent event) {
    }

    private boolean shouldFlag() {
        return enabled;
    }

    public boolean shouldCancel() {
        return cancel;
    }

    public void reload() {
        OnixAnticheat.INSTANCE.getReloadExecuter().run(() -> {
            YamlConfiguration checkscfg = OnixAnticheat.INSTANCE.getConfigManager().getChecksconfig();
            //  if (noCheck) return;
            enabled = checkscfg.getBoolean("checks." + checkName.toLowerCase(Locale.ROOT) + "." + type.toLowerCase(Locale.ROOT) + ".enabled");
            cancel = checkscfg.getBoolean("checks." + checkName.toLowerCase(Locale.ROOT) + "." + type.toLowerCase(Locale.ROOT) + ".cancel");
            setback = checkscfg.getBoolean("checks." + checkName.toLowerCase(Locale.ROOT) + "." + type.toLowerCase(Locale.ROOT) + ".setback");
            setbackVL = checkscfg.getInt("checks." + checkName.toLowerCase(Locale.ROOT) + "." + type.toLowerCase(Locale.ROOT) + ".setbackvl");
            decay = checkscfg.getDouble("checks." + checkName.toLowerCase(Locale.ROOT) + "." + type.toLowerCase(Locale.ROOT) + ".decay", 0.25);
            double tempbuff = checkscfg.getDouble("checks." + checkName.toLowerCase(Locale.ROOT) + "." + type.toLowerCase(Locale.ROOT) + ".maxbuffer", -1);
            if (setbackVL == -1) setbackVL = Double.MAX_VALUE;
            if (tempbuff == -1) maxbuffer = Double.MAX_VALUE;
            else maxbuffer = tempbuff;
            commands.clear();
            List<String> commandList = checkscfg.getStringList("checks." + checkName.toLowerCase(Locale.ROOT) + "." + type.toLowerCase(Locale.ROOT) + ".commands");
            for (String cmd : commandList) {
                try {
                    String[] parts = cmd.split(" ", 2);
                    String[] vlData = parts[0].split(":");
                    int vlThreshold = Integer.parseInt(vlData[0]);
                    int alertInterval = Integer.parseInt(vlData[1]);
                    String command = parts.length > 1 ? parts[1] : "";
                    commands.add(new ConfigVlCommandData(vlThreshold, alertInterval, command));
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Invalid command format in checks.yml: " + cmd);
                }
            }
        });
    }

    private void executeCommands(String verbose) {
        player.getAlertManager().handleVerbose(player, this, verbose);
        for (ConfigVlCommandData cmdData : commands) {
            if (vl >= cmdData.getVl() && (vl - cmdData.getVl()) % cmdData.getAlertInterval() == 0) {
                String command = cmdData.getCommand().replace("%player%", player.getName()).replace("%vl%", String.valueOf(vl)).replace("%prefix%", OnixAnticheat.INSTANCE.getConfigManager().getPrefix());
                if (command.startsWith("[alert]")) {
                    player.getAlertManager().handleAlert(player, this, verbose);
                } else if (command.startsWith("[swapslot]")) player.getInventory().swapSlot();
                else if (command.startsWith("[invalidItem]")) player.disconnect(KickTypes.InvalidItemUse,"sss");//player.disconnect(player.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21) ? "<lang:disconnect.packetError>" : "<lang:disconnect.lost>");
                else if (command.toLowerCase(Locale.ROOT).contains("kick") || command.toLowerCase(Locale.ROOT).contains("ban")) {
                    try {
                        String punishid = PunishIdSystem.GenerateId(player.getName());
                        PunishIdSystem.LogPunish(player, this, punishid, verbose);
                        String finalcmd = command.replace("%id%", punishid);
                        Bukkit.getScheduler().runTask(OnixAnticheat.INSTANCE.getPlugin(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalcmd));
                    } catch (IllegalArgumentException e) {
                        Bukkit.getScheduler().runTask(OnixAnticheat.INSTANCE.getPlugin(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
                        throw new RuntimeException(e);
                    }
                } else {
                    Bukkit.getScheduler().runTask(OnixAnticheat.INSTANCE.getPlugin(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
                }
            }
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    class ConfigVlCommandData {
        int vl;
        int alertInterval;
        String command;
    }
}
