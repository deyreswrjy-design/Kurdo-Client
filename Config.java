package dev.kurdo.client;

import com.google.gson.*;
import dev.kurdo.client.module.*;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;

public class Config {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("kurdo-client.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (Module m : ModuleManager.all) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.enabled);
                if (m instanceof HudModule h) { o.addProperty("x", h.x); o.addProperty("y", h.y); }
                root.add(m.name, o);
            }
            Files.writeString(FILE, GSON.toJson(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void load() {
        try {
            if (!Files.exists(FILE)) return;
            JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
            for (Module m : ModuleManager.all) {
                if (!root.has(m.name)) continue;
                JsonObject o = root.getAsJsonObject(m.name);
                if (o.has("enabled")) m.enabled = o.get("enabled").getAsBoolean();
                if (m instanceof HudModule h) {
                    if (o.has("x")) h.x = o.get("x").getAsInt();
                    if (o.has("y")) h.y = o.get("y").getAsInt();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
