package me.onixdev.util.alert

import dev.onixac.api.manager.IAlertManager
import me.onixdev.OnixAnticheat
import me.onixdev.check.api.Check
import me.onixdev.user.OnixUser
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent

class AlertManager(private val user: OnixUser) : IAlertManager {
    override fun toggleAlerts() {
        if (user.isAlertsEnabled) {
            user.isAlertsEnabled = false
            user.sendMessage(
                OnixAnticheat.INSTANCE.configManager.offAlertsMsg.replace(
                    "%prefix%".toRegex(),
                    OnixAnticheat.INSTANCE.configManager.prefix
                )
            )
        } else {
            user.isAlertsEnabled = true
            user.sendMessage(
                OnixAnticheat.INSTANCE.configManager.onAlertsMsg.replace(
                    "%prefix%".toRegex(),
                    OnixAnticheat.INSTANCE.configManager.prefix
                )
            )
        }
    }


    override fun toggleVerbose() {
        if (user.isVerboseEnabled()) user.setVerboseEnabled(false)
        else user.setVerboseEnabled(true)
    }

    @Suppress("DEPRECATION")
    fun handleVerbose(user: OnixUser, check: Check, verbose: String) {
        val alertString = OnixAnticheat.INSTANCE.configManager.alertsformat
        val prefix = OnixAnticheat.INSTANCE.configManager.prefix
        val hoverMessage = OnixAnticheat.INSTANCE.configManager.hoverMsg
        val finalAlertMsg = alertString.replace("%prefix%".toRegex(), prefix)
            .replace("%player%", user.name)
            .replace("%check_name%", check.name)
            .replace("%vl%", check.getVl().toInt().toString())
            .replace("%type%", check.getType().uppercase())
            .replace("%verbose%", verbose)
            .replace("%experimental%", if (check.isExperimental) " *" else "")
        val finalVerboseMsg = hoverMessage.replace("%player%".toRegex(), user.name)
            .replace("%check_name%", check.name.uppercase())
            .replace("%type%", check.getType().uppercase())
            .replace("%vl%", check.getVl().toString())
            .replace("%verbose%", verbose)
        if (!OnixAnticheat.INSTANCE.configManager.isFixHoverSystemCompability) {
            val alert: Component =
                Component.text(finalAlertMsg).hoverEvent(HoverEvent.showText(Component.text(finalVerboseMsg)))
            for (users in OnixAnticheat.INSTANCE.playerDatamanager.allData) {
                if (users.isVerboseEnabled) users.sendMessage(alert)
            }
        }
        else {
            for (users in OnixAnticheat.INSTANCE.playerDatamanager.allData) {
                if (users.isVerboseEnabled) users.sendMessage(finalAlertMsg)
            }
        }
    }

    fun handleAlert(user: OnixUser, check: Check, verbose: String) {
        val alertString = OnixAnticheat.INSTANCE.configManager.alertsformat
        val prefix = OnixAnticheat.INSTANCE.configManager.prefix
        val hoverMessage = OnixAnticheat.INSTANCE.configManager.hoverMsg
        val finalAlertMsg = alertString.replace("%prefix%".toRegex(), prefix)
            .replace("%player%", user.name)
            .replace("%check_name%", check.name)
            .replace("%vl%", check.getVl().toInt().toString())
            .replace("%type%", check.getType().uppercase())
            .replace("%verbose%", verbose)
            .replace("%experimental%", if (check.isExperimental) "*" else "")
        val finalVerboseMsg = hoverMessage.replace("%player%".toRegex(), user.name)
            .replace("%check_name%", check.name.uppercase())
            .replace("%type%", check.getType().uppercase())
            .replace("%vl%", check.getVl().toString())
            .replace("%verbose%", verbose)
        if (!OnixAnticheat.INSTANCE.configManager.isFixHoverSystemCompability) {
            val alert: Component =
                Component.text(finalAlertMsg).hoverEvent(HoverEvent.showText(Component.text(finalVerboseMsg)))
            for (users in OnixAnticheat.INSTANCE.playerDatamanager.allData) {
                if (users.isAlertsEnabled) users.sendMessage(alert)
            }
        }
        else {
            for (users in OnixAnticheat.INSTANCE.playerDatamanager.allData) {
                if (users.isAlertsEnabled) users.sendMessage(finalAlertMsg)
            }
        }
    }
}