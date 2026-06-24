package dev.lightmod.config;

import dev.lightmod.LightMod;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Flat .properties config — no external library.
 * File: .minecraft/config/lightmod.properties
 */
public class LightModConfig {

    private static LightModConfig INSTANCE;

    // ── Dynamic FPS optimizer ─────────────────────────────────────────────
    /** Enable the auto render-distance adjuster */
    public boolean dynamicFpsOptimizer  = true;
    /** Target FPS to maintain */
    public int     targetFps            = 60;
    /** Minimum render distance the optimizer is allowed to set */
    public int     minRenderDistance    = 4;
    /** Maximum render distance the optimizer is allowed to restore to */
    public int     maxRenderDistance    = 12;

    // ── Entity culling ────────────────────────────────────────────────────
    /**
     * Skip rendering entities behind the camera.
     * DISABLED automatically if Sodium is present (Sodium has its own culling).
     */
    public boolean entityCulling        = true;

    // ── Chunk throttle ────────────────────────────────────────────────────
    /**
     * Limit chunk mesh uploads per frame.
     * DISABLED automatically if Sodium is present (Sodium manages this itself).
     */
    public boolean chunkLoadThrottle    = true;
    public int     maxChunksPerTick     = 4;

    // ── Particles ─────────────────────────────────────────────────────────
    /** Reduce particle spawn rate when FPS is low */
    public boolean dynamicParticles     = true;

    // ── Clouds ────────────────────────────────────────────────────────────
    /**
     * Auto-disable clouds when FPS is low.
     * Safe with Iris — we only touch the cloud option, not the render pipeline.
     */
    public boolean autoClouds           = true;

    // ── Iris-specific ─────────────────────────────────────────────────────
    /**
     * When Iris shaders are active, automatically lower particle density
     * more aggressively (shaders make particles much heavier).
     */
    public boolean aggressiveParticlesWithShaders = true;

    // ─────────────────────────────────────────────────────────────────────

    public static LightModConfig get() {
        if (INSTANCE == null) {
            INSTANCE = new LightModConfig();
            INSTANCE.load();
        }
        return INSTANCE;
    }

    /** Call after mod detection is complete so we can apply smart defaults */
    public void applyModAwareDefaults() {
        if (LightMod.SODIUM_PRESENT) {
            // Sodium already handles chunk throttling and entity culling better than us
            chunkLoadThrottle = false;
            entityCulling     = false;
            LightMod.LOGGER.info("[LightMod] Sodium detected — disabling chunk throttle & entity culling (Sodium handles these)");
        }
        if (LightMod.IRIS_PRESENT) {
            // With Iris, particle pressure is higher — lower the threshold
            aggressiveParticlesWithShaders = true;
            LightMod.LOGGER.info("[LightMod] Iris detected — aggressive particle reduction with shaders enabled");
        }
    }

    private void load() {
        File file = new File("config/lightmod.properties");
        if (!file.exists()) {
            save();
            return;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            Properties p = new Properties();
            p.load(fis);
            dynamicFpsOptimizer              = bool(p, "dynamicFpsOptimizer",              dynamicFpsOptimizer);
            targetFps                        = intVal(p, "targetFps",                      targetFps);
            minRenderDistance                = intVal(p, "minRenderDistance",              minRenderDistance);
            maxRenderDistance                = intVal(p, "maxRenderDistance",              maxRenderDistance);
            entityCulling                    = bool(p, "entityCulling",                   entityCulling);
            chunkLoadThrottle                = bool(p, "chunkLoadThrottle",               chunkLoadThrottle);
            maxChunksPerTick                 = intVal(p, "maxChunksPerTick",              maxChunksPerTick);
            dynamicParticles                 = bool(p, "dynamicParticles",                dynamicParticles);
            autoClouds                       = bool(p, "autoClouds",                      autoClouds);
            aggressiveParticlesWithShaders   = bool(p, "aggressiveParticlesWithShaders",  aggressiveParticlesWithShaders);
        } catch (Exception e) {
            LoggerFactory.getLogger("lightmod").warn("[LightMod] Config load failed, using defaults: {}", e.getMessage());
        }
    }

    public void save() {
        try {
            new File("config").mkdirs();
            Properties p = new Properties();
            p.setProperty("dynamicFpsOptimizer",            String.valueOf(dynamicFpsOptimizer));
            p.setProperty("targetFps",                      String.valueOf(targetFps));
            p.setProperty("minRenderDistance",              String.valueOf(minRenderDistance));
            p.setProperty("maxRenderDistance",              String.valueOf(maxRenderDistance));
            p.setProperty("entityCulling",                  String.valueOf(entityCulling));
            p.setProperty("chunkLoadThrottle",              String.valueOf(chunkLoadThrottle));
            p.setProperty("maxChunksPerTick",               String.valueOf(maxChunksPerTick));
            p.setProperty("dynamicParticles",               String.valueOf(dynamicParticles));
            p.setProperty("autoClouds",                     String.valueOf(autoClouds));
            p.setProperty("aggressiveParticlesWithShaders", String.valueOf(aggressiveParticlesWithShaders));
            try (FileOutputStream fos = new FileOutputStream("config/lightmod.properties")) {
                p.store(fos, "LightMod Configuration\n# Restart required after changes");
            }
        } catch (Exception e) {
            LoggerFactory.getLogger("lightmod").warn("[LightMod] Config save failed: {}", e.getMessage());
        }
    }

    private boolean bool(Properties p, String key, boolean def) {
        return Boolean.parseBoolean(p.getProperty(key, String.valueOf(def)));
    }
    private int intVal(Properties p, String key, int def) {
        try { return Integer.parseInt(p.getProperty(key, String.valueOf(def))); }
        catch (NumberFormatException e) { return def; }
    }
}
