package kr.yoonseul.core;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

/** Minecraft 26.2 전용 윤슬 클라이언트 진입점. */
public final class YoonseulCoreClient implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("yoonseul_core", "main"));
    private KeyMapping openHub;

    @Override
    public void onInitializeClient() {
        openHub = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.yoonseul_core.open_hub",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(Minecraft client) {
        while (openHub.consumeClick()) {
            if (client.gui.screen() == null) client.gui.setScreen(new YoonseulHubScreen());
        }
    }
}
