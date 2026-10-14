package dev.kurdo.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final List<Module> all = new ArrayList<>();
    public static boolean zoomActive = false;

    // CPS tracking (polled every frame so fast clicks aren't missed)
    private static final List<Long> lmb = new ArrayList<>(), rmb = new ArrayList<>();
    private static boolean lWas, rWas;
    private static void pollCps(MinecraftClient mc) {
        long h = mc.getWindow().getHandle();
        boolean l = GLFW.glfwGetMouseButton(h, 0) == 1, r = GLFW.glfwGetMouseButton(h, 1) == 1;
        long now = System.currentTimeMillis();
        if (l && !lWas) lmb.add(now);
        if (r && !rWas) rmb.add(now);
        lWas = l; rWas = r;
        lmb.removeIf(t -> now - t > 1000); rmb.removeIf(t -> now - t > 1000);
    }

    public static final Module fullbright = new Module("Fullbright", "Maximum brightness everywhere", true);
    public static final Module zoom = new Module("Zoom", "Hold the zoom key to zoom in", true);
    public static final Module toggleSprint = new Module("Toggle Sprint", "Sprint automatically while moving forward", false) {
        @Override public void onTick(MinecraftClient mc) {
            if (enabled && mc.player != null && mc.currentScreen == null && mc.options.forwardKey.isPressed()
                    && !mc.player.isSneaking() && !mc.player.horizontalCollision) mc.player.setSprinting(true);
        }
    };

    public static final HudModule fps = new HudModule("FPS", "Frames per second", true, 6, 6) {
        public String text() { return MinecraftClient.getInstance().getCurrentFps() + " FPS"; }
    };
    public static final HudModule cps = new HudModule("CPS", "Clicks per second", true, 6, 26) {
        public String text() { return lmb.size() + " | " + rmb.size() + " CPS"; }
    };
    public static final HudModule ping = new HudModule("Ping", "Server latency", true, 6, 46) {
        public String text() {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.getNetworkHandler() == null) return "-- ms";
            PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            return (e == null ? "--" : String.valueOf(e.getLatency())) + " ms";
        }
    };
    public static final HudModule coords = new HudModule("Coordinates", "XYZ position", true, 6, 66) {
        public String text() {
            var p = MinecraftClient.getInstance().player;
            return p == null ? "XYZ" : String.format("XYZ %d %d %d", (int) Math.floor(p.getX()), (int) Math.floor(p.getY()), (int) Math.floor(p.getZ()));
        }
    };
    public static final HudModule direction = new HudModule("Direction", "Facing direction", false, 6, 86) {
        public String text() {
            var p = MinecraftClient.getInstance().player;
            return p == null ? "-" : p.getHorizontalFacing().asString().toUpperCase();
        }
    };
    public static final HudModule clock = new HudModule("Clock", "Real time", false, 6, 106) {
        public String text() { return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")); }
    };
    public static final HudModule keystrokes = new HudModule("Keystrokes", "WASD + mouse display", true, 6, 130) {
        public String text() { return ""; }
        @Override public int width(TextRenderer tr) { return 66; }
        @Override public int height(TextRenderer tr) { return 66; }
        private void key(DrawContext c, TextRenderer tr, int kx, int ky, int w, int h, String l, boolean down) {
            c.fill(kx, ky, kx + w, ky + h, down ? 0xC02ED573 : 0x90101018);
            c.drawCenteredTextWithShadow(tr, l, kx + w / 2, ky + (h - 8) / 2, 0xFFFFFFFF);
        }
        @Override public void render(DrawContext c, TextRenderer tr) {
            var o = MinecraftClient.getInstance().options;
            key(c, tr, x + 22, y, 20, 20, "W", o.forwardKey.isPressed());
            key(c, tr, x, y + 22, 20, 20, "A", o.leftKey.isPressed());
            key(c, tr, x + 22, y + 22, 20, 20, "S", o.backKey.isPressed());
            key(c, tr, x + 44, y + 22, 20, 20, "D", o.rightKey.isPressed());
            key(c, tr, x, y + 44, 31, 20, "LMB", o.attackKey.isPressed());
            key(c, tr, x + 33, y + 44, 31, 20, "RMB", o.useKey.isPressed());
        }
    };
    public static final HudModule armor = new HudModule("Armor", "Armor durability", true, 6, 200) {
        public String text() { return ""; }
        @Override public int width(TextRenderer tr) { return 20; }
        @Override public int height(TextRenderer tr) { return 72; }
        @Override public void render(DrawContext c, TextRenderer tr) {
            var p = MinecraftClient.getInstance().player;
            EquipmentSlot[] s = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
            for (int i = 0; i < 4; i++) {
                if (p == null) break;
                ItemStack st = p.getEquippedStack(s[i]);
                if (st.isEmpty()) continue;
                c.drawItem(st, x, y + i * 18);
                c.drawStackOverlay(tr, st, x, y + i * 18);
            }
            if (p == null || true) c.fill(x - 1, y - 1, x, y, 0x00000000);
        }
    };

    public static void init() {
        all.add(fullbright); all.add(zoom); all.add(toggleSprint);
        all.add(fps); all.add(cps); all.add(ping); all.add(coords);
        all.add(direction); all.add(clock); all.add(keystrokes); all.add(armor);
    }

    public static List<HudModule> huds() {
        List<HudModule> l = new ArrayList<>();
        for (Module m : all) if (m instanceof HudModule h) l.add(h);
        return l;
    }

    public static void renderHud(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden) return;
        pollCps(mc);
        for (HudModule h : huds()) if (h.enabled) h.render(ctx, mc.textRenderer);
    }
}
