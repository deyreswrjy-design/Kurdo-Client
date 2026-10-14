package dev.kurdo.client;

import dev.kurdo.client.gui.MenuScreen;
import dev.kurdo.client.module.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KurdoClient implements ClientModInitializer {
    public static KeyBinding guiKey, zoomKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        Config.load();

        KeyBinding.Category cat = KeyBinding.Category.create(Identifier.of("kurdo", "main"));
        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.kurdo.gui", GLFW.GLFW_KEY_RIGHT_SHIFT, cat));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.kurdo.zoom", GLFW.GLFW_KEY_C, cat));

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of("kurdo", "hud"),
                (ctx, tick) -> ModuleManager.renderHud(ctx));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (guiKey.wasPressed()) mc.setScreen(new MenuScreen());
            ModuleManager.zoomActive = ModuleManager.zoom.enabled && zoomKey.isPressed() && mc.currentScreen == null;
            for (Module m : ModuleManager.all) m.onTick(mc);
        });
    }
}
