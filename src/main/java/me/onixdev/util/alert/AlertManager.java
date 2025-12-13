package me.onixdev.util.alert;

import dev.onixac.api.manager.IAlertManager;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
import me.onixdev.user.OnixUser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.Locale;

public class AlertManager implements IAlertManager {
    private OnixUser user;

    public AlertManager(OnixUser user) {
        this.user = user;
    }


    @Override
    public void toggleAlerts() {
        if (user.isAlertsEnabled()) {
            user.setAlertsEnabled(false);
            user.sendMessage(OnixAnticheat.INSTANCE.getConfigManager().offAlertsMsg.replaceAll("%prefix%",OnixAnticheat.INSTANCE.getConfigManager().getPrefix()));
        }
        else {
            user.setAlertsEnabled(true);
            user.sendMessage(OnixAnticheat.INSTANCE.getConfigManager().onAlertsMsg.replaceAll("%prefix%",OnixAnticheat.INSTANCE.getConfigManager().getPrefix()));
        }
    }


    @Override
    public void toggleVerbose() {
       if (user.isVerboseEnabled()) user.setVerboseEnabled(false);
       else user.setVerboseEnabled(true);
    }

    public void handleVerbose(OnixUser user,Check check, String verbose) {
        String alertString = OnixAnticheat.INSTANCE.getConfigManager().getAlertsformat();
        String prefix = OnixAnticheat.INSTANCE.getConfigManager().getPrefix();
        String hoverMessage = OnixAnticheat.INSTANCE.getConfigManager().getHoverMsg();
        String finalAlertMsg = alertString.replaceAll("%prefix%", prefix)
                .replace("%player%", user.getName())
                .replace("%check_name%", check.getName())
                .replace("%vl%", String.valueOf((int) check.getVl()))
                .replace("%type%", check.getType().toUpperCase(Locale.ROOT))
                .replace("%verbose%", verbose)
                .replace("%experimental%", check.isExperimental() ? " *" : "");
        String finalVerboseMsg = hoverMessage.replaceAll("%player%", user.getName())
                .replace("%check_name%", check.getName().toUpperCase(Locale.ROOT))
                .replace("%type%", check.getType().toUpperCase(Locale.ROOT))
                .replace("%vl%", String.valueOf(check.getVl()))
                .replace("%verbose%", verbose);
        Component alert = Component.text(finalAlertMsg).hoverEvent(HoverEvent.showText(Component.text(finalVerboseMsg)));
        for (OnixUser users: OnixAnticheat.INSTANCE.getPlayerDatamanager().getAllData()) {
            if (users.isVerboseEnabled()) users.sendMessage(alert);
        }

    }

    public void handleAlert(OnixUser user,Check check, String verbose) {
        String alertString = OnixAnticheat.INSTANCE.getConfigManager().getAlertsformat();
        String prefix = OnixAnticheat.INSTANCE.getConfigManager().getPrefix();
        String hoverMessage = OnixAnticheat.INSTANCE.getConfigManager().getHoverMsg();
        String finalAlertMsg = alertString.replaceAll("%prefix%", prefix)
                .replace("%player%", user.getName())
                .replace("%check_name%", check.getName())
                .replace("%type%", check.getType().toUpperCase(Locale.ROOT))
                .replace("%vl%", String.valueOf((int) check.getVl()))
                .replace("%verbose%", verbose)
                .replace("%experimental%", check.isExperimental() ? " *" : "");
        String finalVerboseMsg = hoverMessage.replaceAll("%player%", user.getName())
                .replace("%check_name%", check.getName().toUpperCase(Locale.ROOT))
                .replace("%type%", check.getType().toUpperCase(Locale.ROOT))
                .replace("%vl%", String.valueOf(check.getVl()))
                .replace("%verbose%", verbose);
        Component alert = Component.text(finalAlertMsg).hoverEvent(HoverEvent.showText(Component.text(finalVerboseMsg)));
        for (OnixUser users: OnixAnticheat.INSTANCE.getPlayerDatamanager().getAllData()) {
            if (users.isAlertsEnabled()) users.sendMessage(alert);
        }
    }
}
