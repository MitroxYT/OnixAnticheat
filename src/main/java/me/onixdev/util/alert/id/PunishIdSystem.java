package me.onixdev.util.alert.id;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
import me.onixdev.user.OnixUser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class PunishIdSystem {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public void LogPunish(OnixUser user, Check check,String id,String verbose) {
        File filesDir = new File(OnixAnticheat.INSTANCE.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "punish");
        if (!filesDir.exists()) {
            filesDir.mkdirs();
        }
        File file = new File(filesDir.getAbsolutePath() + File.separator + id + ".json");
        if (!file.exists()) {
            try {
                file.createNewFile();
                JsonObject object = new JsonObject();
                object.addProperty("checkName", check.getName());
                object.addProperty("checkType", check.getType());
                object.addProperty("vl",check.getVl());
                object.addProperty("verbose",verbose);
                String json = gson.toJson(object);
                try {
                    try (Writer writer = new FileWriter(file)) {
                        writer.write(json);
                        writer.flush();
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public String GenerateId(String name) {
        return name + String.valueOf(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
    }
}
