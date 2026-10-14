package dev.kurdo.client.module;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/** A module drawn on screen at a user-movable position. */
public abstract class HudModule extends Module {
    public int x, y;
    public HudModule(String name, String desc, boolean on, int x, int y) {
        super(name, desc, on); this.x = x; this.y = y;
    }
    public abstract String text();

    public int width(TextRenderer tr) { return tr.getWidth(text()) + 10; }
    public int height(TextRenderer tr) { return 16; }

    public void render(DrawContext ctx, TextRenderer tr) {
        int w = width(tr), h = height(tr);
        ctx.fill(x, y, x + w, y + h, 0x90101018);
        ctx.fill(x, y, x + 2, y + h, 0xFF2ED573);
        ctx.drawTextWithShadow(tr, text(), x + 6, y + 4, 0xFFFFFFFF);
    }
}
