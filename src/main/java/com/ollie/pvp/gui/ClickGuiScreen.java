package com.ollie.pvp.gui;

import com.ollie.pvp.Module;
import com.ollie.pvp.PvpClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {

    // layout
    private static final int PANEL_W      = 540;
    private static final int PANEL_H      = 360;
    private static final int SIDEBAR_W    = 140;
    private static final int HEADER_H     = 40;
    private static final int ROW_H        = 26;
    private static final int CAT_ROW_H    = 30;

    // palette
    private static final int OVERLAY      = 0xB5000000;
    private static final int PANEL_BG     = 0xFF15151C;
    private static final int SIDEBAR_BG   = 0xFF101016;
    private static final int HEADER_TOP   = 0xFF9D6DF5;
    private static final int HEADER_BOT   = 0xFF6D28D9;
    private static final int ACCENT       = 0xFF8B5CF6;
    private static final int ACCENT_DIM   = 0xFF4C1D95;
    private static final int TEXT_MAIN    = 0xFFF1F1F5;
    private static final int TEXT_MUTED   = 0xFF8B8B9A;
    private static final int ROW_HOVER    = 0xFF1E1E27;
    private static final int BORDER       = 0xFF26262E;
    private static final int TOGGLE_OFF   = 0xFF30303A;
    private static final int TOGGLE_ON    = ACCENT;
    private static final int KNOB         = 0xFFF5F5FA;

    private Category selected = Category.COMBAT;
    private final List<Row> rows = new ArrayList<>();
    private int px, py;

    public ClickGuiScreen() {
        super(Text.literal("Click GUI"));
    }

    @Override
    protected void init() {
        px = (width  - PANEL_W) / 2;
        py = (height - PANEL_H) / 2;
        rebuild();
    }

    private void rebuild() {
        rows.clear();
        int y = py + HEADER_H + 10;
        for (Module m : PvpClient.MODULES.all()) {
            if (m.getCategory() == selected) {
                rows.add(new Row(m, px + SIDEBAR_W + 10, y,
                                 PANEL_W - SIDEBAR_W - 20, ROW_H - 4));
                y += ROW_H;
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // dim background
        ctx.fill(0, 0, width, height, OVERLAY);

        // layered shadow
        ctx.fill(px - 6, py - 6, px + PANEL_W + 6, py + PANEL_H + 6, 0x30FFFFFF);
        ctx.fill(px - 3, py - 3, px + PANEL_W + 3, py + PANEL_H + 3, 0x60000000);

        // panel body
        ctx.fill(px, py, px + PANEL_W, py + PANEL_H, PANEL_BG);
        ctx.drawBorder(px, py, PANEL_W, PANEL_H, BORDER);

        // header gradient
        vGradient(ctx, px + 1, py + 1, px + PANEL_W - 1, py + HEADER_H, HEADER_TOP, HEADER_BOT);

        // header text
        ctx.drawText(textRenderer, "OLLIE", px + 14, py + 14, TEXT_MAIN, true);
        ctx.drawText(textRenderer, "pvp",   px + 14 + textRenderer.getWidth("OLLIE ") + 2,
                     py + 15, 0xFFE9D5FF, true);
        String status = PvpClient.MODULES.enabledCount() + " / "
                      + PvpClient.MODULES.all().size() + " active";
        ctx.drawText(textRenderer, status,
                     px + PANEL_W - textRenderer.getWidth(status) - 14,
                     py + 15, 0xFFE9D5FF, false);

        // sidebar
        ctx.fill(px, py + HEADER_H, px + SIDEBAR_W, py + PANEL_H, SIDEBAR_BG);
        ctx.fill(px + SIDEBAR_W, py + HEADER_H, px + SIDEBAR_W + 1,
                 py + PANEL_H, BORDER);

        int cy = py + HEADER_H + 10;
        for (Category cat : Category.values()) {
            boolean hovered = inRect(mouseX, mouseY, px + 8, cy,
                                     SIDEBAR_W - 16, CAT_ROW_H - 4);
            boolean active  = cat == selected;

            if (active) {
                ctx.fill(px + 8, cy, px + SIDEBAR_W - 8, cy + CAT_ROW_H - 4, ACCENT_DIM);
                ctx.fill(px + 8, cy, px + 11, cy + CAT_ROW_H - 4, ACCENT);
            } else if (hovered) {
                ctx.fill(px + 8, cy, px + SIDEBAR_W - 8, cy + CAT_ROW_H - 4, ROW_HOVER);
            }

            // icon box
            ctx.fill(px + 16, cy + 6, px + 16 + 16, cy + 6 + 16,
                     active ? ACCENT : 0xFF26262E);
            ctx.drawText(textRenderer, cat.icon, px + 20, cy + 10,
                         0xFFFFFFFF, false);

            ctx.drawText(textRenderer, cat.displayName,
                         px + 40, cy + (CAT_ROW_H - 4 - textRenderer.fontHeight) / 2 + 1,
                         active || hovered ? TEXT_MAIN : TEXT_MUTED, false);

            cy += CAT_ROW_H;
        }

        // modules
        ctx.enableScissor(px + SIDEBAR_W + 1, py + HEADER_H,
                          px + PANEL_W, py + PANEL_H);
        for (Row r : rows) r.render(ctx, mouseX, mouseY, textRenderer);
        ctx.disableScissor();

        if (rows.isEmpty()) {
            String empty = "nothing in here yet";
            ctx.drawText(textRenderer, empty,
                         px + SIDEBAR_W + (PANEL_W - SIDEBAR_W - textRenderer.getWidth(empty)) / 2,
                         py + PANEL_H / 2, TEXT_MUTED, false);
        }

        // footer hint
        String hint = "RIGHT SHIFT  •  close";
        ctx.drawText(textRenderer, hint, px + 14,
                     py + PANEL_H - 16, TEXT_MUTED, false);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            // category
            int cy = py + HEADER_H + 10;
            for (Category cat : Category.values()) {
                if (inRect((int) mx, (int) my, px + 8, cy, SIDEBAR_W - 16, CAT_ROW_H - 4)) {
                    if (selected != cat) {
                        selected = cat;
                        rebuild();
                    }
                    return true;
                }
                cy += CAT_ROW_H;
            }
            // module row toggle
            for (Row r : rows) {
                if (r.contains((int) mx, (int) my)) {
                    r.module.toggle();
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 || keyCode == 344) { // ESC or RIGHT SHIFT
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // ---------- helpers ----------

    private static boolean inRect(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private static void vGradient(DrawContext ctx, int x1, int y1, int x2, int y2,
                                  int c1, int c2) {
        int h = y2 - y1;
        int steps = Math.max(1, h / 2);
        for (int i = 0; i < steps; i++) {
            float t = (float) i / (steps - 1);
            int c = lerp(c1, c2, t);
            int ya = y1 + i * h / steps;
            int yb = (i == steps - 1) ? y2 : y1 + (i + 1) * h / steps;
            ctx.fill(x1, ya, x2, yb, c);
        }
    }

    private static int lerp(int a, int b, float t) {
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF, aa = (a >>> 24) & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF, ba = (b >>> 24) & 0xFF;
        int r  = (int) (ar + (br - ar) * t);
        int g  = (int) (ag + (bg - ag) * t);
        int bl = (int) (ab + (bb - ab) * t);
        int al = (int) (aa + (ba - aa) * t);
        return (al << 24) | (r << 16) | (g << 8) | bl;
    }

    // ---------- row widget ----------

    private static final class Row {
        final Module module;
        final int x, y, w, h;
        float hover = 0f;
        float anim  = 0f;

        Row(Module module, int x, int y, int w, int h) {
            this.module = module;
            this.x = x; this.y = y; this.w = w; this.h = h;
        }

        boolean contains(int mx, int my) {
            return inRect(mx, my, x, y, w, h);
        }

        void render(DrawContext ctx, int mouseX, int mouseY, TextRenderer tr) {
            boolean over = contains(mouseX, mouseY);
            hover += ((over ? 1f : 0f) - hover) * 0.25f;
            anim  += ((module.isEnabled() ? 1f : 0f) - anim) * 0.25f;

            // row bg
            int bg = lerp(PANEL_BG, ROW_HOVER, hover);
            ctx.fill(x, y, x + w, y + h, bg);
            ctx.drawBorder(x, y, w, h, BORDER);

            // accent bar (grows with anim)
            int barW = (int) (3 * anim);
            if (barW > 0) {
                ctx.fill(x, y, x + barW, y + h, ACCENT);
            }

            // label
            int labelColor = lerp(TEXT_MUTED, TEXT_MAIN, anim);
            ctx.drawText(tr, module.getName(),
                         x + 10, y + (h - tr.fontHeight) / 2 + 1,
                         labelColor, false);

            // animated toggle pill
            int tw = 30, th = 14;
            int tx = x + w - tw - 10;
            int ty = y + (h - th) / 2;
            int track = lerp(TOGGLE_OFF, TOGGLE_ON, anim);
            ctx.fill(tx, ty, tx + tw, ty + th, track);

            int kw = 10;
            int travel = tw - kw - 4;
            int kx = tx + 2 + (int) (travel * anim);
            ctx.fill(kx, ty + 2, kx + kw, ty + th - 2, KNOB);
        }
    }
}
