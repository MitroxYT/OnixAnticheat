package me.onixdev.util.config;


import lombok.Getter;
import lombok.Setter;
import me.onixdev.OnixAnticheat;
import me.onixdev.util.color.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ConfigManager {
    @Getter
    private YamlConfiguration messagesconfig;
    @Getter
    private YamlConfiguration checksconfig;
    @Getter
    private YamlConfiguration config;

    public ConfigManager(boolean onload) {

        if (onload) {
            File cfgfile = new File(OnixAnticheat.INSTANCE.getPlugin().getDataFolder(), "config.yml");
            File cfgfile2 = new File(OnixAnticheat.INSTANCE.getPlugin().getDataFolder(), "checks.yml");
            File cfgfile3 = new File(OnixAnticheat.INSTANCE.getPlugin().getDataFolder(), "messages.yml");

            if (!OnixAnticheat.INSTANCE.getPlugin().getDataFolder().exists()) {
                OnixAnticheat.INSTANCE.getPlugin().getDataFolder().mkdir();
            }
            if (!cfgfile2.exists()) {
                OnixAnticheat.INSTANCE.getPlugin().saveResource("checks.yml", false);
            }
            if (!cfgfile.exists()) {
                OnixAnticheat.INSTANCE.getPlugin().saveResource("config.yml", false);
            }
            if (!cfgfile3.exists()) {
                OnixAnticheat.INSTANCE.getPlugin().saveResource("messages.yml", false);
            }
            updateAllConfigFiles();
            this.config = YamlConfiguration.loadConfiguration(cfgfile);
            this.checksconfig = YamlConfiguration.loadConfiguration(cfgfile2);
            this.messagesconfig = YamlConfiguration.loadConfiguration(cfgfile3);

            init();


        }
    }

    private void updateAllConfigFiles() {

        var configFiles = Map.of(
                "config.yml",   1.1,
                "checks.yml",   1.1,
                "messages.yml", 1.3
        );

        JavaPlugin plugin = OnixAnticheat.INSTANCE.getPlugin();

        boolean anyUpdated = false;

        for (Map.Entry<String, Double> entry : configFiles.entrySet()) {
            String fileName = entry.getKey();
            double latestVersion = entry.getValue();

            if (updateSingleConfig(fileName, latestVersion)) {
                anyUpdated = true;
            }
        }

        if (anyUpdated) {
            plugin.getLogger().info("Все конфиги успешно обновлены!");
        } else {
            plugin.getLogger().info("Все конфиги уже актуальны.");
        }

        reload();
    }
    private boolean updateSingleConfig(String fileName, double latestVersion) {
        File file = new File(OnixAnticheat.INSTANCE.getPlugin().getDataFolder(), fileName);
        JavaPlugin plugin = OnixAnticheat.INSTANCE.getPlugin();
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
            plugin.getLogger().info("Создан новый файл: " + fileName);
            return true;
        }

        YamlConfiguration diskConfig = YamlConfiguration.loadConfiguration(file);
        double currentVersion = diskConfig.getDouble("config-version", 0.0);

        if (currentVersion >= latestVersion) {
            return false;
        }

        plugin.getLogger().info("Обновление " + fileName + " → v" + latestVersion + "...");
        Map<String, Object> userValues = diskConfig.getValues(false);

        plugin.saveResource(fileName, true);
        YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(file);
        for (Map.Entry<String, Object> userEntry : userValues.entrySet()) {
            String key = userEntry.getKey();
            if (!"config-version".equals(key)) {
                newConfig.set(key, userEntry.getValue());
            }
        }
        newConfig.set("config-version", latestVersion);
        try {
            newConfig.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка при сохранении " + fileName + "!");
            e.printStackTrace();
        }

        return true;
    }

    public void reload() {

        this.checksconfig = YamlConfiguration.loadConfiguration(new File(OnixAnticheat.INSTANCE.getPlugin().getDataFolder(), "checks.yml"));
        this.config = YamlConfiguration.loadConfiguration(new File(OnixAnticheat.INSTANCE.getPlugin().getDataFolder(), "config.yml"));
        this.messagesconfig = YamlConfiguration.loadConfiguration(new File(OnixAnticheat.INSTANCE.getPlugin().getDataFolder(), "messages.yml"));
        init();

    }

    private String prefix = "§7[§bOnixAnticheatAC§7] §8» §7";
    private String alertsformat = "%prefix% &aИгрок&r &5%player%&r &aпровалил &r&4%check_name%&r%experimental% &r&f&l(x&b%vl%&f&l) &5%verbose%&f&l.";
    private List<String> hover = List.of("&8«&f[&2&nOnixAnticheatIAN&f]&8»", "&7 &c%player%", "&7 &c%verbose%");
    public String onAlertsMsg = "%prefix% <gray> alerts <green> on";
    public String offAlertsMsg = "%prefix% <gray> alerts <green> off";
    private String hoverMsg = "";
    private String profileMessage;
    public boolean enableAlertsOnJoin;


    private void init() {
        prefix = MessageUtil.translate(messagesconfig.getString("prefix", "§7[§bOnixAnticheatAC§7] §8» §7"));
        alertsformat = MessageUtil.translate(messagesconfig.getString("alerts_format", "%prefix% &aИгрок&r &5%player%&r &aпровалил &r&4%check_name%&r%experimental% &r&f&l(x&b%vl%&f&l) &5%verbose%&f&l."));
        hover= messagesconfig.getStringList("hover");
        hoverMsg = MessageUtil.translate(MessageUtil.listToString(hover));
        profileMessage = MessageUtil.translate(MessageUtil.listToString(messagesconfig.getStringList("profile")));
        onAlertsMsg = MessageUtil.translate(messagesconfig.getString("alerts-on","%prefix% <gray> alerts <green> on"));
        offAlertsMsg = MessageUtil.translate(messagesconfig.getString("alerts-off","%prefix% <gray> alerts <green> off"));
        enableAlertsOnJoin = config.getBoolean("enable-alert-on-join",false);
    }

    public String getProFileMsg() {
        return profileMessage;
    }
    public String getUrl() {
        return "localhost:8080";
    }
}

