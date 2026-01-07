package me.onixdev.util.alert.id

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import me.onixdev.OnixAnticheat
import me.onixdev.check.api.Check
import me.onixdev.user.OnixUser
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.concurrent.ThreadLocalRandom

object PunishIdSystem {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    fun logPunish(user: OnixUser?, check: Check, id: String?, verbose: String?) {
        val filesDir =
            File(OnixAnticheat.INSTANCE.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "punish")
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }
        val file = File(filesDir.getAbsolutePath() + File.separator + id + ".json")
        if (!file.exists()) {
            try {
                file.createNewFile()
                val `object` = JsonObject()
                `object`.addProperty("checkName", check.getName())
                `object`.addProperty("checkType", check.getType())
                `object`.addProperty("vl", check.getVl())
                `object`.addProperty("verbose", verbose)
                val json = gson.toJson(`object`)
                try {
                    FileWriter(file).use { writer ->
                        writer.write(json)
                        writer.flush()
                        writer.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    fun getID(name: String?): String {
        return name + ThreadLocalRandom.current().nextInt(0, Int.Companion.MAX_VALUE).toString()
    }
}