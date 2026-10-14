package dev.kurdo.client.gui;

import dev.kurdo.client.Config;
import dev.kurdo.client.module.*;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/** Drag any enabled HUD element anywhere on screen. Press ESC to save and exit. */
public class HudEditorScreen extends Screen {
    private HudModule dragging;
    private int offX, offY;

    public HudEditorScreen() { super(Text.literal("HUD Editor")); }

    @Override public void renderBackground(DrawContext c, int mx, int my, float d) { c.fill(0, 0, width, height, 0x60000000); }

    @Override
    public void render(DrawContext c, int mx, int my, float d) {
        super.render(c, mx, my, d);
        c.drawCenteredTextWithShadow(textRenderer, "Drag elements to move them  -  ESC to save", width / 2, 8, 0xFF2ED573);
        for (HudModule h : ModuleManager.huds()) {
            if (!h.enabled) continue;
            h.render(c, textRenderer);
            int w = h.width(textRenderer), hh = h.height(textRenderer);
            int col = (h == dragging) ? 0xFF2ED573 : 0xAAFFFFFF;
            c.fill(h.x - 1, h.y - 1, h.x + w + 1, h.y, col);
            c.fill(h.x - 1, h.y + hh, h.x + w + 1, h.y + hh + 1, col);
            c.fill(h.x - 1, h.y, h.x, h.y + hh, col);
            c.fill(h.x + w, h.y, h.x + w + 1, h.y + hh, col);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) {
            var list = ModuleManager.huds();
            for (int i = list.size() - 1; i >= 0; i--) {
                HudModule h = list.get(i);
                if (!h.enabled) continue;
                if (click.x() >= h.x && click.x() <= h.x + h.width(textRenderer)
                        && click.y() >= h.y && click.y() <= h.y + h.height(textRenderer)) {
                    dragging = h; offX = (int) click.x() - h.x; offY = (int) click.y() - h.y;
                    return true;
                }
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double dx, double dy) {
        if (dragging != null) {
            int w = dragging.width(textRenderer), h = dragging.height(textRenderer);
            dragging.x = Math.max(0, Math.min(width - w, (int) click.x() - offX));
            dragging.y = Math.max(0, Math.min(height - h, (int) click.y() - offY));
            return true;
        }
        return super.mouseDragged(click, dx, dy);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (dragging != null) { dragging = null; Config.save(); return true; }
        return super.mouseReleased(click);
    }

    @Override public void close() { Config.save(); client.setScreen(new MenuScreen()); }
    @Override public boolean shouldPause() { return false; }
}
