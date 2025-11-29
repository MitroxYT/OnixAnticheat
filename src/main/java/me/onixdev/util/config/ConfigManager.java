package me.onixdev.util.config;


import lombok.Getter;
import lombok.Setter;
import me.onixdev.OnixAnticheat;
import me.onixdev.util.color.MessageUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

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
            this.checksconfig = YamlConfiguration.loadConfiguration(cfgfile2);
            this.config = YamlConfiguration.loadConfiguration(cfgfile);
            this.messagesconfig = YamlConfiguration.loadConfiguration(cfgfile3);
            //  checkversion();

            init();


        }
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
    private String hoverMsg = "";


    private void init() {
        prefix = MessageUtil.translate(messagesconfig.getString("prefix", "§7[§bOnixAnticheatAC§7] §8» §7"));
        alertsformat = MessageUtil.translate(messagesconfig.getString("alerts_format", "%prefix% &aИгрок&r &5%player%&r &aпровалил &r&4%check_name%&r%experimental% &r&f&l(x&b%vl%&f&l) &5%verbose%&f&l."));
        hover= messagesconfig.getStringList("hover");
        hoverMsg = MessageUtil.translate(MessageUtil.listToString(hover));
    }
}

