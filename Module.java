package dev.kurdo.client.module;

public class Module {
    public final String name;
    public final String description;
    public boolean enabled;
    public Module(String name, String description, boolean enabled) {
        this.name = name; this.description = description; this.enabled = enabled;
    }
    public void onTick(net.minecraft.client.MinecraftClient mc) {}
    public void toggle() { enabled = !enabled; }
}
