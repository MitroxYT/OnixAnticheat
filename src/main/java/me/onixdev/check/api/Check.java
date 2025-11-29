package me.onixdev.check.api;

import dev.onixac.api.check.CheckStage;
import dev.onixac.api.check.ICheck;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.onixdev.OnixAnticheat;
import me.onixdev.event.api.BaseEvent;
import me.onixdev.user.OnixUser;
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
    protected double decay;
    protected final OnixUser player;
    private final List<ConfigVlCommandData> commands = new ArrayList<>();
    public double setbackVL;
    private boolean setback;

    public Check(OnixUser player,CheckBuilder builder) {
        this.player = player;
        if (builder != null) {
            checkName = builder.getCheckName();
            description = builder.getDescription();
            stage = builder.getCheckStage();
            type = builder.getType();
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
    public boolean isEnabled() {
        return enabled;
    }
    public boolean fail(Object debug) {
        if (!shouldFlag()) return false;
        OnixAnticheat.INSTANCE.getAlertExecutor().run(()-> {
            ++vl;
            executeCommands(debug.toString());
        });
        return true;
    }
    public void onEvent(BaseEvent event) {}

    private boolean shouldFlag() {
        return enabled;
    }
    public boolean shouldCancel() {return cancel;}

    public void reload() {
        OnixAnticheat.INSTANCE.getReloadExecuter().run(()->{
            YamlConfiguration checkscfg = OnixAnticheat.INSTANCE.getConfigManager().getChecksconfig();
          //  if (noCheck) return;
            enabled = checkscfg.getBoolean("checks." + checkName.toLowerCase(Locale.ROOT) + "." + type.toLowerCase(Locale.ROOT) + ".enabled");
            cancel  = checkscfg.getBoolean("checks." + checkName.toLowerCase(Locale.ROOT) + "." + type.toLowerCase(Locale.ROOT) + ".cancel");
            setback = checkscfg.getBoolean("checks." + checkName.toLowerCase(Locale.ROOT) + "." + type.toLowerCase(Locale.ROOT) + ".setback");
            setbackVL = checkscfg.getInt("checks." + checkName.toLowerCase(Locale.ROOT) + "." + type.toLowerCase(Locale.ROOT) + ".setbackvl");
            decay = checkscfg.getDouble("checks." + checkName.toLowerCase(Locale.ROOT) + "." + type.toLowerCase(Locale.ROOT) + ".decay",0.25);
            if (setbackVL == -1) setbackVL = Double.MAX_VALUE;
            
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
            System.out.println("e: " + enabled + " c: " + vl + " d: " + decay);
        });
    }
    private void executeCommands(String verbose) {
        player.getAlertManager().handleVerbose(player,this,verbose);
        for (ConfigVlCommandData cmdData : commands) {
            if (vl >= cmdData.getVl() && (vl - cmdData.getVl()) % cmdData.getAlertInterval() == 0) {
                String command = cmdData.getCommand().replace("%player%", player.getName()).replace("%vl%", String.valueOf(vl)).replace("%prefix%",OnixAnticheat.INSTANCE.getConfigManager().getPrefix());
                if (command.startsWith("[alert]")) {
                    player.getAlertManager().handleAlert(player,this,verbose);
                } else {
                    Bukkit.getScheduler().runTask(OnixAnticheat.INSTANCE.getPlugin(), () ->  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
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
