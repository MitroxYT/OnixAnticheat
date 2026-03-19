package me.onixdev.compability.impl

import me.onixdev.OnixAnticheat
import me.onixdev.compability.ICompabilityCheck
import me.onixdev.compability.manager.CompatibilityManager

class LeafCompabilityWorldTicking : ICompabilityCheck {
    override fun check(manager: CompatibilityManager?) {
        try {
            val clazz = Class.forName("org.dreeam.leaf.config.modules.async.SparklyPaperParallelWorldTicking")
            val field = clazz.getField("enabled")

            val enabled = field.getBoolean(clazz)
            if (enabled) {
                manager?.isLeafTicking = true
                OnixAnticheat.INSTANCE.printCool("&cВЫ ИСПОЛЬЗУЕТЕ НЕСТАБИЛЬНУЮ ФУНКЦИЮ PARALLEL WORLD TICKING ДЛЯ СТАБИЛЬНОЙ РАБОТЫ ПРОСЬБА ОТКЛЮЧИТЬ ДАННУЮ ФУНКЦИЮ")
                OnixAnticheat.INSTANCE.printCool("&cНЕКОТОРЫЕ ПРОВЕРКИ НЕ БУДУТ РАБОТАТЬ")
                OnixAnticheat.INSTANCE.printCool("&cВЫ ИСПОЛЬЗУЕТЕ НЕСТАБИЛЬНУЮ ФУНКЦИЮ PARALLEL WORLD TICKING ДЛЯ СТАБИЛЬНОЙ РАБОТЫ ПРОСЬБА ОТКЛЮЧИТЬ ДАННУЮ ФУНКЦИЮ")
                OnixAnticheat.INSTANCE.printCool("&cНЕКОТОРЫЕ ПРОВЕРКИ НЕ БУДУТ РАБОТАТЬ")
                OnixAnticheat.INSTANCE.printCool("&cВЫ ИСПОЛЬЗУЕТЕ НЕСТАБИЛЬНУЮ ФУНКЦИЮ PARALLEL WORLD TICKING ДЛЯ СТАБИЛЬНОЙ РАБОТЫ ПРОСЬБА ОТКЛЮЧИТЬ ДАННУЮ ФУНКЦИЮ")
                OnixAnticheat.INSTANCE.printCool("&cНЕКОТОРЫЕ ПРОВЕРКИ НЕ БУДУТ РАБОТАТЬ")
            }
        } catch (_: ClassNotFoundException) {
        } catch (_: NoSuchFieldException) {
        } catch (_: IllegalAccessException) {
        }
    }
}