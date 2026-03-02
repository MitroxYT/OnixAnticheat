package me.onixdev.util.alert;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
import me.onixdev.user.OnixUser;
import me.onixdev.util.color.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class PunishManager {
    OnixUser player;
    List<PunishGroup> groups = new ArrayList<>();
    String experimentalSymbol = "*";
    private String alertString;
    private boolean testMode;
    private boolean printToConsole;
    private String proxyAlertString = "";
    public PunishManager(OnixUser user) {
        this.player = user;
        reload();
    }

    public void reload() {
        YamlConfiguration config = OnixAnticheat.INSTANCE.getConfigManager().getChecksconfig();
        ConfigurationSection punishSection = config.getConfigurationSection("punish");

        if (punishSection == null) {
            OnixAnticheat.INSTANCE.getPlugin().getLogger().severe("No 'punish' section found in config!");
            return;
        }


        try {
            groups.clear();

            for (Check check : player.checks) {
                check.setEnabled(false);
            }

            for (String groupName : punishSection.getKeys(false)) {
                ConfigurationSection groupSection = punishSection.getConfigurationSection(groupName);

                if (groupSection == null) continue;

                List<String> checks = groupSection.getStringList("checks");

                List<String> commands = groupSection.getStringList("commands");

                int removeViolationsAfter = groupSection.getInt("remove-violations-after", 300);

                OnixAnticheat.INSTANCE.printCool("Group: " + groupName + " checks: " + checks + " commands: " + commands);

                List<ParsedCommand> parsed = new ArrayList<>();
                List<Check> checksList = new ArrayList<>();

                for (String checkName : checks) {
                    checkName = checkName.toLowerCase(Locale.ROOT);
                    boolean exclude = checkName.startsWith("!");

                    if (exclude) {
                        checkName = checkName.substring(1);
                    }

                    for (Check check : player.checks) {
                        String checkFullName = (check.getName() + check.getType()).toLowerCase(Locale.ROOT);

                        if (checkFullName.contains(checkName)) {
                            if (exclude) {
                                checksList.remove(check);
                                check.setEnabled(false);
                            } else if (!checksList.contains(check)) {
                                checksList.add(check);
                                check.setEnabled(true);
                            }
                        }
                    }
                }

                for (String command : commands) {
                    try {
                        int firstSpace = command.indexOf(' ');
                        if (firstSpace == -1) {
                            OnixAnticheat.INSTANCE.getPlugin().getLogger().warning("Invalid command format (no space): " + command);
                            continue;
                        }

                        String numbersPart = command.substring(0, firstSpace);
                        String commandString = OnixAnticheat.INSTANCE.getColorizer().colorize(command.substring(firstSpace + 1));

                        String[] numbers = numbersPart.split(":");

                        if (numbers.length != 2) {
                            OnixAnticheat.INSTANCE.getPlugin().getLogger().warning("Invalid command format (expected threshold:interval): " + command);
                            continue;
                        }

                        int threshold = Integer.parseInt(numbers[0]);
                        int interval = Integer.parseInt(numbers[1]);

                        parsed.add(new ParsedCommand(threshold, interval, commandString));

                    } catch (NumberFormatException e) {
                        OnixAnticheat.INSTANCE.getPlugin().getLogger().warning("Error parsing numbers in command: " + command);
                    } catch (Exception e) {
                        OnixAnticheat.INSTANCE.getPlugin().getLogger().warning("Error parsing command: " + command);
                    }
                }
                groups.add(new PunishGroup(checksList, parsed, removeViolationsAfter));
            }
            } catch (Exception e) {
            OnixAnticheat.INSTANCE.getPlugin().getLogger().severe("Error while loading punishments.yml! This is likely your fault!");
            e.printStackTrace();
        }
    }


    public boolean handleAlert(OnixUser player, String verbose, Check check) {
        boolean sentDebug = false;
        player.getAlertManager().handleVerbose(player,check,verbose);
        for (PunishGroup group : groups) {
            if (group.checks.contains(check)) {
                final int vl = getViolations(group, check);
                final int violationCount = group.violations.size();
                for (ParsedCommand command : group.commands) {
                    String cmd = command.command.replace("%player%", player.getName()).replace("%vl%", String.valueOf(vl)).replace("%prefix%", OnixAnticheat.INSTANCE.getConfigManager().getPrefix());

                        sentDebug = true;

                    if (violationCount >= command.threshold) {
                        boolean inInterval = command.interval == 0 ? (command.executeCount == 0) : (violationCount % command.interval == 0);
                        if (inInterval) {
                            if (command.command.equals("[webhook]")) {
                            } else if (command.command.equals("[proxy]")) {
                            } else {
                                if (command.command.equals("[alert]")) {
                                    sentDebug = true;
                                  player.getAlertManager().handleAlert(player,check,verbose);
                                }
                                else {

                                String finalCmd = cmd;
                                FoliaScheduler.getGlobalRegionScheduler().run(OnixAnticheat.INSTANCE.getPlugin(), (dummy) ->
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd));
                                }
                            }
                        }

                        command.executeCount++;
                    }
                }
            }
        }

        return sentDebug;
    }

    public void handleViolation(Check check) {
        for (PunishGroup group : groups) {
            if (group.checks.contains(check)) {
                long currentTime = System.currentTimeMillis();

                group.violations.put(currentTime, check);
                group.violations.entrySet().removeIf(time -> currentTime - time.getKey() > group.removeViolationsAfter);
            }
        }
    }

    private int getViolations(PunishGroup group, Check check) {
        int vl = 0;
        for (Check value : group.violations.values()) {
            if (value == check) vl++;
        }
        return vl;
    }
}


class PunishGroup {
        public final List<Check> checks;
        public final List<ParsedCommand> commands;
        public final Map<Long, Check> violations = new HashMap<>();
        public final int removeViolationsAfter;

        public PunishGroup(List<Check> checks, List<ParsedCommand> commands, int removeViolationsAfter) {
            this.checks = checks;
            this.commands = commands;
            this.removeViolationsAfter = removeViolationsAfter * 1000;
        }
    }

    class ParsedCommand {
        public final int threshold;
        public final int interval;
        public final String command;
        public int executeCount;

        public ParsedCommand(int threshold, int interval, String command) {
            this.threshold = threshold;
            this.interval = interval;
            this.command = command;
        }
    }

