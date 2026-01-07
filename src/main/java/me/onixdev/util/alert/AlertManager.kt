package me.onixdev.util.alert

import dev.onixac.api.manager.IAlertManager
import me.onixdev.OnixAnticheat
import me.onixdev.check.api.Check
import me.onixdev.user.OnixUser
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent

class AlertManager(private val user: OnixUser) : IAlertManager {
    override fun toggleAlerts() {
        if (user.isAlertsEnabled()) {
            user.setAlertsEnabled(false)
            user.sendMessage(
                OnixAnticheat.INSTANCE.getConfigManager().offAlertsMsg.replace(
                    "%prefix%".toRegex(),
                    OnixAnticheat.INSTANCE.getConfigManager().getPrefix()
                )
            )
        } else {
            user.setAlertsEnabled(true)
            user.sendMessage(
                OnixAnticheat.INSTANCE.getConfigManager().onAlertsMsg.replace(
                    "%prefix%".toRegex(),
                    OnixAnticheat.INSTANCE.getConfigManager().getPrefix()
                )
            )
        }
    }


    override fun toggleVerbose() {
        if (user.isVerboseEnabled()) user.setVerboseEnabled(false)
        else user.setVerboseEnabled(true)
    }

    fun handleVerbose(user: OnixUser, check: Check, verbose: String) {
        val alertString = OnixAnticheat.INSTANCE.getConfigManager().getAlertsformat()
        val prefix = OnixAnticheat.INSTANCE.getConfigManager().getPrefix()
        val hoverMessage = OnixAnticheat.INSTANCE.getConfigManager().getHoverMsg()
        val finalAlertMsg = alertString.replace("%prefix%".toRegex(), prefix)
            .replace("%player%", user.getName())
            .replace("%check_name%", check.getName())
            .replace("%vl%", check.getVl().toInt().toString())
            .replace("%type%", check.getType().uppercase())
            .replace("%verbose%", verbose)
            .replace("%experimental%", if (check.isExperimental()) " *" else "")
        val finalVerboseMsg = hoverMessage.replace("%player%".toRegex(), user.getName())
            .replace("%check_name%", check.getName().uppercase())
            .replace("%type%", check.getType().uppercase())
            .replace("%vl%", check.getVl().toString())
            .replace("%verbose%", verbose)
        val alert: Component =
            Component.text(finalAlertMsg).hoverEvent(HoverEvent.showText(Component.text(finalVerboseMsg)))
        for (users in OnixAnticheat.INSTANCE.getPlayerDatamanager().getAllData()) {
            if (users.isVerboseEnabled()) users.sendMessage(alert)
        }
    }

    fun handleAlert(user: OnixUser, check: Check, verbose: String) {
        val alertString = OnixAnticheat.INSTANCE.getConfigManager().getAlertsformat()
        val prefix = OnixAnticheat.INSTANCE.getConfigManager().getPrefix()
        val hoverMessage = OnixAnticheat.INSTANCE.getConfigManager().getHoverMsg()
        val finalAlertMsg = alertString.replace("%prefix%".toRegex(), prefix)
            .replace("%player%", user.getName())
            .replace("%check_name%", check.getName())
            .replace("%type%", check.getType().uppercase())
            .replace("%vl%", check.getVl().toInt().toString())
            .replace("%verbose%", verbose)
            .replace("%experimental%", if (check.isExperimental()) " *" else "")
        val finalVerboseMsg = hoverMessage.replace("%player%".toRegex(), user.getName())
            .replace("%check_name%", check.getName().uppercase())
            .replace("%type%", check.getType().uppercase())
            .replace("%vl%", check.getVl().toString())
            .replace("%verbose%", verbose)
        val alert: Component =
            Component.text(finalAlertMsg).hoverEvent(HoverEvent.showText(Component.text(finalVerboseMsg)))
        for (users in OnixAnticheat.INSTANCE.getPlayerDatamanager().getAllData()) {
            if (users.isAlertsEnabled()) users.sendMessage(alert)
        }
    }
}