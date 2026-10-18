package kr.yoonseul.core;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class YoonseulCoreClient implements ClientModInitializer {
    static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("yoonseul_core", "client"));
    static YoonseulConfig config;
    private KeyMapping hub, quick, calc, shortcuts, brightness, time, inventory;
    private KeyMapping autoSprint, zoom, recentTimer, timer, camera;
    private Integer savedFov;
    private Double savedGamma;

    private static KeyMapping key(String id, int glfw) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.yoonseul_core." + id, InputConstants.Type.KEYSYM, glfw, CATEGORY));
    }

    @Override public void onInitializeClient() {
        config = YoonseulConfig.load();
        calc = key("calculator", GLFW.GLFW_KEY_UNKNOWN);
        shortcuts = key("shortcuts", GLFW.GLFW_KEY_UNKNOWN);
        brightness = key("brightness", GLFW.GLFW_KEY_UNKNOWN);
        time = key("time_preset", GLFW.GLFW_KEY_UNKNOWN);
        inventory = key("inventory_hud", GLFW.GLFW_KEY_UNKNOWN);
        autoSprint = key("auto_sprint", GLFW.GLFW_KEY_UNKNOWN);
        zoom = key("zoom", GLFW.GLFW_KEY_Z);
        recentTimer = key("recent_timer", GLFW.GLFW_KEY_UNKNOWN);
        quick = key("quick_menu", GLFW.GLFW_KEY_C);
        hub = key("open_hub", GLFW.GLFW_KEY_RIGHT_SHIFT);
        timer = key("timer", GLFW.GLFW_KEY_UNKNOWN);
        camera = key("preview_camera", GLFW.GLFW_KEY_UNKNOWN);
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(Minecraft mc) {
        while (hub.consumeClick() || quick.consumeClick()) if (mc.gui.screen() == null) mc.gui.setScreen(new YoonseulHubScreen());
        while (brightness.consumeClick()) { config.brightness = !config.brightness; changed(mc, "밝기 조절", config.brightness); }
        while (autoSprint.consumeClick()) { config.autoSprint = !config.autoSprint; changed(mc, "자동 달리기", config.autoSprint); }
        while (inventory.consumeClick()) { config.inventoryHud = !config.inventoryHud; changed(mc, "인벤토리 HUD", config.inventoryHud); }
        while (time.consumeClick()) { config.customTime = true; config.timePreset = (config.timePreset + 1) % 4; config.save(); message(mc, "시간 프리셋: " + new String[]{"낮", "노을", "밤", "서버 시간"}[config.timePreset]); }
        while (calc.consumeClick()) if (mc.gui.screen() == null) mc.gui.setScreen(new CalculatorScreen());
        while (shortcuts.consumeClick()) if (mc.gui.screen() == null) mc.gui.setScreen(new YoonseulHubScreen("단축어"));
        while (timer.consumeClick() || recentTimer.consumeClick()) if (mc.gui.screen() == null) mc.gui.setScreen(new TimerScreen(new YoonseulHubScreen()));
        while (camera.consumeClick()) if (mc.gui.screen() == null) mc.gui.setScreen(new YoonseulHubScreen("프리뷰 카메라"));
        applyLiveFeatures(mc);
    }

    private void applyLiveFeatures(Minecraft mc) {
        try {
            if (zoom.isDown()) {
                if (savedFov == null) savedFov = mc.options.fov().get();
                mc.options.fov().set(config.zoomFov);
            } else if (savedFov != null) {
                mc.options.fov().set(savedFov); savedFov = null;
            }
            if (config.brightness) {
                if (savedGamma == null) savedGamma = mc.options.gamma().get();
                mc.options.gamma().set(16.0D);
            } else if (savedGamma != null) {
                mc.options.gamma().set(savedGamma); savedGamma = null;
            }
            if (config.autoSprint && mc.player != null && mc.gui.screen() == null) {
                mc.options.keyUp.setDown(true); mc.options.keySprint.setDown(true);
            }
        } catch (Throwable ignored) {}
    }

    static void changed(Minecraft mc, String name, boolean on) { config.save(); message(mc, name + (on ? " 켜짐" : " 꺼짐")); }
    static void message(Minecraft mc, String s) { if (mc.player != null) mc.player.sendSystemMessage(Component.literal("§b[윤슬] §f" + s)); }
}
