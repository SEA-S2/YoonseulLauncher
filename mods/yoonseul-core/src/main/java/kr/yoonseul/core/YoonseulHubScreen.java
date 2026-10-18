package kr.yoonseul.core;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** 오른쪽 Shift로 여는 윤슬 전용 허브. */
public final class YoonseulHubScreen extends Screen {
    private static final int PANEL = 0xE61B2942;
    private static final int CARD = 0xCC263A5D;
    private static final int ACCENT = 0xFF9BE7D7;
    private static final int TEXT = 0xFFF5FAFF;
    private static final int MUTED = 0xFFB8C7D9;

    public YoonseulHubScreen() {
        super(Component.literal("윤슬 허브"));
    }

    @Override
    protected void init() {
        int w = Math.min(430, this.width - 32);
        int x = (this.width - w) / 2;
        int y = Math.max(18, (this.height - 260) / 2);

        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                Component.literal("닫기"), b -> onClose())
                .bounds(x + w - 104, y + 172, 80, 24).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0x99081425);
        int w = Math.min(430, this.width - 32);
        int x = (this.width - w) / 2;
        int y = Math.max(18, (this.height - 260) / 2);
        int h = 220;

        ctx.fill(x, y, x + w, y + h, PANEL);
        ctx.fill(x, y, x + w, y + 3, ACCENT);
        ctx.text(this.font, "윤슬 허브", x + 24, y + 22, TEXT, false);
        ctx.text(this.font, "Minecraft 26.2 · Fabric", x + 24, y + 42, MUTED, false);

        ctx.fill(x + 24, y + 70, x + w - 24, y + 146, CARD);
        ctx.text(this.font, "윤슬런처 클라이언트", x + 40, y + 88, TEXT, false);
        ctx.text(this.font, "오른쪽 Shift로 언제든 다시 열 수 있어요.", x + 40, y + 110, MUTED, false);
        ctx.text(this.font, "윤슬 전용 클라이언트 기능을 관리하는 화면입니다.", x + 40, y + 128, MUTED, false);

        super.extractRenderState(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
