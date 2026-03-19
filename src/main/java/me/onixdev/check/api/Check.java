package me.onixdev.check.api;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import dev.onixac.api.check.CheckInfo;
import dev.onixac.api.check.CheckStage;
import dev.onixac.api.check.ICheck;
import dev.onixac.api.check.custom.ConfigVlCommandData;
import dev.onixac.api.events.api.BaseEvent;
import me.onixdev.OnixAnticheat;
import me.onixdev.user.OnixUser;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Check implements ICheck {
    private String configName;
    private boolean enabled;
    protected boolean cancel;
    protected double vl;
    protected String checkName;
    protected String description;
    protected String type;
    protected CheckStage stage;
    protected double maxbuffer;
    protected double decay, decayBuffer;
    protected final OnixUser player;
    private List<ConfigVlCommandData> commands = new ArrayList<>();
    public double setbackVL;
    private boolean setback;
    private boolean createdByAPI;

    public Check(OnixUser player, CheckBuilder builder) {
        this.player = player;
        if (builder != null) {
            checkName = builder.getCheckName();
            description = builder.getDescription();
            stage = builder.getCheckStage();
            type = builder.getType();
            maxbuffer = builder.getMaxBuffer();
            createdByAPI = builder.isCreatedByApi();
            if (createdByAPI) {
                enabled = true;
                commands = builder.getCommandData();
            }
        }
    }

    public Check(OnixUser player) {
        this.player = player;
        final CheckInfo checkData = this.getClass().getAnnotation(CheckInfo.class);
        if (checkData != null) {
            this.checkName = checkData.name();
            this.type = checkData.type();
            this.configName = checkData.customCfgName();
            if (this.configName.equals("DEFAULT")) this.configName = this.checkName + this.type;
            this.decay = checkData.decay();
            this.decayBuffer = checkData.decayBuffer();
            this.setback = checkData.setback();
            this.setbackVL = checkData.setbackVl();
            this.stage = checkData.stage();
            this.description = checkData.description();
            this.maxbuffer = checkData.maxBuffer();
        }
        reload();
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
    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    @Override
    public void setCancel(boolean value) {
        this.cancel = value;
    }

    @Override
    public void setSetback(boolean value) {
        this.setback = value;
    }

    @Override
    public void setVl(double value) {
        this.vl = value;
    }

    @Override
    public boolean isExperimental() {
        return stage != null && stage == CheckStage.EXPERIMENTAL;
    }

    public boolean failAndSetback(Object debug) {
        if (!shouldFlag()) return false;
        OnixAnticheat.INSTANCE.getAlertExecutor().run(() -> {
            executeCommands(debug.toString());
            if (vl > setbackVL && setback) player.getMovementContainer().setback();
        });
        return true;
    }

    @Override
    public boolean fail(Object debug) {
        if (!shouldFlag()) return false;
        OnixAnticheat.INSTANCE.getAlertExecutor().run(() -> {
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
        return enabled && cancel;
    }

    public void reload() {
        // чеки созданные через апи не возможно использовать через конфиг
        if (createdByAPI) return;
        OnixAnticheat.INSTANCE.getReloadExecuter().run(() -> {
            YamlConfiguration checkscfg = OnixAnticheat.INSTANCE.getConfigManager().getConfig();
            //  if (noCheck) return;
            cancel = checkscfg.getBoolean(configName + ".cancel", cancel);
            setback = checkscfg.getBoolean(configName + ".setback", false);
            setbackVL = checkscfg.getInt(configName + ".setbackvl", -1);
            decay = checkscfg.getDouble(configName + ".decay", 0.25);
            decayBuffer = checkscfg.getDouble(configName + ".decayBuffer", decayBuffer);
            maxbuffer = checkscfg.getDouble(configName + ".maxBuffer", maxbuffer);
            if (setbackVL == -1) setbackVL = Double.MAX_VALUE;
//            if (tempbuff == -1) maxbuffer = Double.MAX_VALUE;
        });
    }

    public String getCheckPatch() {
        return configName + ".";
    }

    public String format(Double value) {
        return String.format("%.5f", value);
    }

    public YamlConfiguration getCheckConfig() {
        return OnixAnticheat.INSTANCE.getConfigManager().getConfig();
    }
//    public YamlConfiguration getCheckConfig() {
//        return OnixAnticheat.INSTANCE.getConfigManager().getChecksconfig();
//    }

    private void executeCommands(String verbose) {
        player.punishManager.handleViolation(this);
        // lastViolationTime = System.currentTimeMillis();
        vl++;
        player.punishManager.handleAlert(player, verbose, this);
//        player.getAlertManager().handleVerbose(player, this, verbose);
//        for (ConfigVlCommandData cmdData : commands) {
//            if (vl >= cmdData.getVl() && (vl - cmdData.getVl()) % cmdData.getAlertInterval() == 0) {
//                String command = cmdData.getCommand().replace("%player%", player.getName()).replace("%vl%", String.valueOf(vl)).replace("%prefix%", OnixAnticheat.INSTANCE.getConfigManager().getPrefix());
//                if (command.startsWith("[alert]")) {
//                //    player.getAlertManager().handleAlert(player, this, verbose);
//                } else if (command.startsWith("[swapslot]")) player.getInventory().swapSlot();
//                else if (command.startsWith("[proxy]")) {player.getAlertManager().onProxy(player,this,verbose);}
//                else if (command.startsWith("[invalidItem]")) player.disconnect(KickTypes.InvalidItemUse,"sss");//player.disconnect(player.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21) ? "<lang:disconnect.packetError>" : "<lang:disconnect.lost>");
//                else if (command.toLowerCase(Locale.ROOT).contains("kick") || command.toLowerCase(Locale.ROOT).contains("ban")) {
//                    try {
//                        String punished = PunishIdSystem.INSTANCE.getID(player.getName());
//                        PunishIdSystem.INSTANCE.logPunish(player, this, punished, verbose);
//                        String finalised = command.replace("%id%", punished);
//                        Bukkit.getScheduler().runTask(OnixAnticheat.INSTANCE.getPlugin(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalised));
//                    } catch (IllegalArgumentException e) {
//                        Bukkit.getScheduler().runTask(OnixAnticheat.INSTANCE.getPlugin(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
//                        throw new RuntimeException(e);
//                    }
//                } else {
//                    Bukkit.getScheduler().runTask(OnixAnticheat.INSTANCE.getPlugin(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
//                }
//            }
//        }
//    }
    }

    public void onPacketOut(PacketSendEvent event) {
    }
}
