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

        OnixAnticheat.INSTANCE.printCool("experimental-symbol: " + config.getString("experimental-symbol", "*"));
        experimentalSymbol = config.getString("experimental-symbol", "*");

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
                        OnixAnticheat.INSTANCE.printCool("&b Checking: " + checkFullName + " against: " + checkName);

                        if (checkFullName.contains(checkName)) {
                            if (exclude) {
                                checksList.remove(check);
                                check.setEnabled(false);
                                OnixAnticheat.INSTANCE.printCool("disable: " + checkFullName + " che: " + checkName);
                            } else if (!checksList.contains(check)) {
                                checksList.add(check);
                                check.setEnabled(true);
                                OnixAnticheat.INSTANCE.printCool("enable: " + checkFullName + " che: " + checkName);
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
                        String commandString = command.substring(firstSpace + 1);

                        String[] numbers = numbersPart.split(":");

                        if (numbers.length != 2) {
                            OnixAnticheat.INSTANCE.getPlugin().getLogger().warning("Invalid command format (expected threshold:interval): " + command);
                            continue;
                        }

                        int threshold = Integer.parseInt(numbers[0]);
                        int interval = Integer.parseInt(numbers[1]);

                        OnixAnticheat.INSTANCE.printCool("Parsed command: threshold=" + threshold + " interval=" + interval + " cmd=" + commandString);
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
//    public void reload() {
//        YamlConfiguration config = OnixAnticheat.INSTANCE.getConfigManager().getChecksconfig();
//        List<String> punish = OnixAnticheat.INSTANCE.getConfigManager().punish;
//        ConfigurationSection section = config.getConfigurationSection("punish");
//        OnixAnticheat.INSTANCE.printCool("aaa: " + punish + " list: " + config.isList("punish"));
//        experimentalSymbol = config.getString("experimental-symbol", "*");
//        try {
//            groups.clear();
//
//            // To support reloading
//            for (Check check : player.checks) {
//                check.setEnabled(false);
//            }
//
//            for (Object s : punish) {
//                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) s;
//
//                List<String> checks = (List<String>) map.getOrDefault("checks", new ArrayList<>());
//                List<String> commands = (List<String>) map.getOrDefault("commands", new ArrayList<>());
//                int removeViolationsAfter = (int) map.getOrDefault("remove-violations-after", 300);
//                OnixAnticheat.INSTANCE.printCool("ches: " + checks + " c: " + commands);
//                List<ParsedCommand> parsed = new ArrayList<>();
//                List<Check> checksList = new ArrayList<>();
//                List<Check> excluded = new ArrayList<>();
//                for (String command : checks) {
//                    command = command.toLowerCase(Locale.ROOT);
//                    boolean exclude = false;
//                    if (command.startsWith("!")) {
//                        exclude = true;
//                        command = command.substring(1);
//                    }
//                    for (Check check : player.checks) { // o(n) * o(n)?
//                        String finals = check.getName() + check.getType();
//                        OnixAnticheat.INSTANCE.printCool("&b " + finals);
//                        if (check.getName() != null &&
//                                (finals.toLowerCase(Locale.ROOT).contains(command))) { // Some checks have equivalent names like AntiKB and AntiKnockback
//                            if (exclude) {
//                                excluded.add(check);
//                            } else {
//                                checksList.add(check);
//                                check.setEnabled(true);
//                            }
//                        }
//                    }
//                    for (Check check : excluded) checksList.remove(check);
//                }
//
//                for (String command : commands) {
//                    String firstNum = command.substring(0, command.indexOf(":"));
//                    String secondNum = command.substring(command.indexOf(":"), command.indexOf(" "));
//
//                    int threshold = Integer.parseInt(firstNum);
//                    int interval = Integer.parseInt(secondNum.substring(1));
//                    String commandString = command.substring(command.indexOf(" ") + 1);
//
//                    parsed.add(new ParsedCommand(threshold, interval, commandString));
//                }
//
//                groups.add(new PunishGroup(checksList, parsed, removeViolationsAfter));
//            }
//        } catch (Exception e) {
//            OnixAnticheat.INSTANCE.getPlugin().getLogger().severe("Error while loading punishments.yml! This is likely your fault!");
//            e.printStackTrace();
//        }
//    }


    public boolean handleAlert(OnixUser player, String verbose, Check check) {
        boolean sentDebug = false;
        player.getAlertManager().handleVerbose(player,check,verbose);
        // Check commands
        for (PunishGroup group : groups) {
            if (group.checks.contains(check)) {
                final int vl = getViolations(group, check);
                final int violationCount = group.violations.size();
                for (ParsedCommand command : group.commands) {
                    String cmd = command.command.replace("%player%", player.getName()).replace("%vl%", String.valueOf(check.getVl())).replace("%prefix%", OnixAnticheat.INSTANCE.getConfigManager().getPrefix());

                    // Verbose that prints all flags
                    //if (command.command.equals("[alert]")) {
                        sentDebug = true;
                 //   }

                    if (violationCount >= command.threshold) {
                        // 0 means execute once
                        // Any other number means execute every X interval
                        boolean inInterval = command.interval == 0 ? (command.executeCount == 0) : (violationCount % command.interval == 0);
                        if (inInterval) {
                            if (command.command.equals("[webhook]")) {
                           //     GrimAPI.INSTANCE.getDiscordManager().sendAlert(player, verbose, check.getDisplayName(), vl);
                            } else if (command.command.equals("[proxy]")) {
                             //   ProxyAlertMessenger.sendPluginMessage(replaceAlertPlaceholders(command.command, vl, check, proxyAlertString, verbose));
                            } else {
                                if (command.command.equals("[alert]")) {
                                    sentDebug = true;
                                  player.getAlertManager().handleAlert(player,check,verbose);
                               //   return sentDebug;
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
                // Remove violations older than the defined time in the config
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

