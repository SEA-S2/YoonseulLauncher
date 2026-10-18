package kr.yoonseul.core;

import net.fabricmc.loader.api.FabricLoader;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class YoonseulConfig {
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("yoonseul-core.properties");
    public boolean brightness, autoSprint, inventoryHud, customTime;
    public boolean hideHudInCamera = true;
    public int timePreset, zoomFov = 30;

    public static YoonseulConfig load() {
        YoonseulConfig c = new YoonseulConfig(); Properties p = new Properties();
        try (InputStream in = Files.newInputStream(PATH)) { p.load(in); } catch (Exception ignored) { return c; }
        c.brightness = Boolean.parseBoolean(p.getProperty("brightness", "false"));
        c.autoSprint = Boolean.parseBoolean(p.getProperty("autoSprint", "false"));
        c.inventoryHud = Boolean.parseBoolean(p.getProperty("inventoryHud", "false"));
        c.customTime = Boolean.parseBoolean(p.getProperty("customTime", "false"));
        c.hideHudInCamera = Boolean.parseBoolean(p.getProperty("hideHudInCamera", "true"));
        try { c.timePreset = Integer.parseInt(p.getProperty("timePreset", "0")); } catch (Exception ignored) {}
        try { c.zoomFov = Integer.parseInt(p.getProperty("zoomFov", "30")); } catch (Exception ignored) {}
        return c;
    }

    public void save() {
        Properties p = new Properties();
        p.setProperty("brightness", Boolean.toString(brightness)); p.setProperty("autoSprint", Boolean.toString(autoSprint));
        p.setProperty("inventoryHud", Boolean.toString(inventoryHud)); p.setProperty("customTime", Boolean.toString(customTime));
        p.setProperty("hideHudInCamera", Boolean.toString(hideHudInCamera)); p.setProperty("timePreset", Integer.toString(timePreset));
        p.setProperty("zoomFov", Integer.toString(zoomFov));
        try { Files.createDirectories(PATH.getParent()); try (OutputStream out = Files.newOutputStream(PATH)) { p.store(out, "Yoonseul Core"); } }
        catch (Exception ignored) {}
    }
}
