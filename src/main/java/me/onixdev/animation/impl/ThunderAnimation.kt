package me.onixdev.animation.impl

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer
import me.onixdev.OnixAnticheat
import me.onixdev.animation.BaseAnimation
import me.onixdev.util.color.MessageUtil
import org.bukkit.*
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.function.Consumer
import kotlin.math.cos
import kotlin.math.sin

class ThunderAnimation : BaseAnimation("Thunder") {
    val PARTICLE: Particle = getDustParticle()
    override fun execute(player: Player?, data: String?) {
        player!!.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 100, 1))
        val location1 = player.location

        val baseRadius = 0.5
        val maxRadius = 1.5
        val height = 2.5
        val spiralTurns = 3
        val particlesPerTurn = 8

        player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 80, 10))
        player.addPotionEffect(
            PotionEffect(
                Objects.requireNonNull<PotionEffectType?>(PotionEffectType.getById(2)),
                80,
                10
            )
        )
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 80, 1))


        val finalLocation = location1
        player.world.players.forEach(Consumer { p: Player? ->
            if (p!!.location.distance(finalLocation) <= 30) {
                p.playSound(finalLocation, Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 0.5f, 1.0f)
            }
        })


//        player.sendTitle(
//            " \u00a7x\u00a7F\u00a7F\u00a72\u00a79\u00a72\u00a79\u00a7l\u256d  \u00a7k!! \u00a7x\u00a7F\u00a7F\u00a72\u00a79\u00a72\u00a79\u00a7lAntiCheat \u00a7k!!",
//            " \u00a7x\u00a7F\u00a7F\u00a72\u00a79\u00a72\u00a79\u2503 \u00a7x\u00a7F\u00a7F\u00a7F\u00a75\u00a7E\u00a72"
//        )

        val particleTask: BukkitRunnable = object : BukkitRunnable() {
            var t: Double = 0.0

            override fun run() {
                this.t += 0.15
                if (this.t > Math.PI * 2 * spiralTurns) {
                    this.cancel()
                    return
                }
                val location = player.location

//                player.sendMessage(" ")
//                player.sendMessage(" \u00a7x\u00a7F\u00a7F\u00a72\u00a79\u00a72\u00a79\u00a7l\u256d              \u00a7k!! \u00a7x\u00a7F\u00a7F\u00a72\u00a79\u00a72\u00a79\u00a7lAntiCheat \u00a7k!!")
//                player.sendMessage(" \u00a7x\u00a7F\u00a7F\u00a72\u00a79\u00a72\u00a79\u2503 \u00a7x\u00a7F\u00a7F\u00a7F\u00a75\u00a7E\u00a72")
//                player.sendMessage(" \u00a7x\u00a7F\u00a7F\u00a72\u00a79\u00a72\u00a79\u00a7l\u2570")
//                player.sendMessage(" ")

                for (i in 0..<particlesPerTurn) {
                    val angle = Math.PI * 2 * i.toDouble() / particlesPerTurn.toDouble()

                    val radius = baseRadius + (maxRadius - baseRadius) * (this.t / (Math.PI * 2 * spiralTurns))
                    val x = radius * cos(angle + this.t)
                    val z = radius * sin(angle + this.t)

                    val y = height * (this.t / (Math.PI * 2 * spiralTurns))

                    val particleLocation = location.clone().add(x, y, z)

                    val colorProgress = (this.t / (Math.PI * 2 * spiralTurns)).toFloat()
                    val particleColor = getColorFromProgress(colorProgress)

                    for (nearbyPlayer in Objects.requireNonNull<World?>(location.getWorld()).players) {
                        if (nearbyPlayer.location.distance(location) <= 30) {
                            nearbyPlayer.spawnParticle<DustOptions?>(
                                PARTICLE, particleLocation, 1,
                                DustOptions(particleColor, 2.0f)
                            )
                        }
                    }
                }
                val centerParticle = location.clone().add(0.0, height * (this.t / (Math.PI * 2 * spiralTurns)) / 2, 0.0)
                for (nearbyPlayer in location.getWorld()?.players!!) {
                    if (nearbyPlayer.location.distance(location) <= 30) {
                        nearbyPlayer.spawnParticle<DustOptions?>(
                            PARTICLE, centerParticle, 3,
                            DustOptions(Color.RED, 3.0f)
                        )
                    }
                }
            }

            private fun getColorFromProgress(progress: Float): Color {
                if (progress < 0.33f) {
                    return Color.fromRGB(255, (255 * progress * 3).toInt(), 0)
                } else if (progress < 0.66f) {
                    return Color.fromRGB(255, 255, (255 * (progress - 0.33f) * 3).toInt())
                } else {
                    val value = (255 * (progress - 0.66f) * 3).toInt()
                    return Color.fromRGB(255, 255, 255)
                }
            }
        }

        particleTask.runTaskTimer(OnixAnticheat.INSTANCE.plugin, 0L, 1L)

        object : BukkitRunnable() {
            override fun run() {
                particleTask.cancel()

                createExplosionEffect(player)
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    LegacyComponentSerializer.legacySection().serialize(MessageUtil.miniMessage(data.toString().replace("%player%",player.name).replace("%prefix%",
                        OnixAnticheat.INSTANCE.configManager.prefix)))
                )
            }
        }.runTaskLater(OnixAnticheat.INSTANCE.plugin, 80L)
    }

    private fun createExplosionEffect(player: Player) {
        val location = player.location

        for (nearbyPlayer in Objects.requireNonNull<World?>(location.getWorld()).players) {
            if (nearbyPlayer.location.distance(location) <= 30) {
                for (i in 0..99) {
                    val angle = Math.PI * 2 * i / 100
                    val distance = 2.0
                    val x = distance * cos(angle)
                    val z = distance * sin(angle)
                    val y = 2.0 * Math.random()

                    val particleLocation = location.clone().add(x, y, z)

                    val explosionColors = arrayOf<Color>(
                        Color.RED, Color.ORANGE, Color.YELLOW, Color.fromRGB(255, 100, 0)
                    )
                    val particleColor = explosionColors[i % explosionColors.size]

                    nearbyPlayer.spawnParticle<DustOptions?>(
                        PARTICLE, particleLocation, 1,
                        DustOptions(particleColor, 1.5f + Math.random().toFloat())
                    )
                }

                nearbyPlayer.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f)
                nearbyPlayer.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.7f, 1.0f)
            }
        }

        for (i in 0..49) {
            val angle = Math.PI * 2 * i / 50
            val distance = 1.0
            val x = distance * cos(angle)
            val z = distance * sin(angle)
            val y = 1.0 * Math.random()

            val particleLocation = location.clone().add(x, y, z)
            player.spawnParticle<DustOptions?>(
               PARTICLE, particleLocation, 1,
                DustOptions(Color.RED, 2.0f)
            )
        }
    }

    private fun getDustParticle(): Particle {
        try {
            return Particle.valueOf("DUST")
        } catch (e: IllegalArgumentException) {
            return Particle.valueOf("REDSTONE")
        }
    }
}