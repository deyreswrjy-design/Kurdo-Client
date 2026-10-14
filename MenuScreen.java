package dev.kurdo.client.gui;

import dev.kurdo.client.Config;
import dev.kurdo.client.module.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MenuScreen extends Screen {
    private static final int W = 240, ROW = 22;
    public MenuScreen() { super(Text.literal("Kurdo Client")); }

    private int left() { return (width - W) / 2; }
    private int top() { return Math.max(10, (height - (ModuleManager.all.size() * ROW + 70)) / 2); }

    @Override public void renderBackground(DrawContext c, int mx, int my, float d) { c.fill(0, 0, width, height, 0x90000000); }

    @Override
    public void render(DrawContext c, int mx, int my, float d) {
        super.render(c, mx, my, d);
        int l = left(), t = top(), h = ModuleManager.all.size() * ROW + 62;
        c.fill(l, t, l + W, t + h, 0xE0141420);
        c.fill(l, t, l + W, t + 3, 0xFF2ED573);
        c.drawCenteredTextWithShadow(textRenderer, "KURDO CLIENT", width / 2, t + 12, 0xFF2ED573);
        int y = t + 30;
        for (Module m : ModuleManager.all) {
            boolean hover = mx >= l + 8 && mx <= l + W - 8 && my >= y && my < y + ROW - 2;
            c.fill(l + 8, y, l + W - 8, y + ROW - 2, hover ? 0xFF2A2A3C : 0xFF1E1E2C);
            c.drawTextWithShadow(textRenderer, m.name, l + 14, y + 7, 0xFFFFFFFF);
            String s = m.enabled ? "ON" : "OFF";
            c.drawTextWithShadow(textRenderer, s, l + W - 14 - textRenderer.getWidth(s), y + 7, m.enabled ? 0xFF2ED573 : 0xFFFF4757);
            y += ROW;
        }
        boolean hb = mx >= l + 8 && mx <= l + W - 8 && my >= y + 4 && my <= y + 24;
        c.fill(l + 8, y + 4, l + W - 8, y + 24, hb ? 0xFF2ED573 : 0xFF1F9D55);
        c.drawCenteredTextWithShadow(textRenderer, "Edit HUD Layout", width / 2, y + 10, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y();
        int l = left(), y = top() + 30;
        for (Module m : ModuleManager.all) {
            if (mx >= l + 8 && mx <= l + W - 8 && my >= y && my < y + ROW - 2) { m.toggle(); Config.save(); return true; }
            y += ROW;
        }
        if (mx >= l + 8 && mx <= l + W - 8 && my >= y + 4 && my <= y + 24) { client.setScreen(new HudEditorScreen()); return true; }
        return super.mouseClicked(click, doubled);
    }

    @Override public boolean shouldPause() { return false; }
}
