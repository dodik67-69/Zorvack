package dev.lightmod;

import dev.lightmod.config.LightModConfig;
import net.minecraft.client.Minecraft;

/**
 * Runs every client tick.
 * Monitors smoothed FPS and nudges render distance up/down
 * to keep the game near the configured target FPS.
 *
 * Safe with Sodium: we only touch options.renderDistance,
 * which Sodium respects.
 *
 * Safe with Iris: we only touch render distance, not shaders.
 */
public class DynamicOptimizer {

    private static int    tickCounter = 0;
    private static final int CHECK_INTERVAL = 40; // every ~2 seconds

    private static double smoothedFps = 60.0;

    public static void tick(Minecraft client) {
        if (client.level == null || client.player == null) return;

        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;

        LightModConfig cfg = LightModConfig.get();
        if (!cfg.dynamicFpsOptimizer) return;

        int currentFps = client.getFps();
        // Exponential moving average — avoids reacting to single-frame spikes
        smoothedFps = smoothedFps * 0.85 + currentFps * 0.15;

        // In 26.x the option is accessed via options.renderDistance()
        int renderDist = client.options.renderDistance().get();

        if (smoothedFps < cfg.targetFps - 10 && renderDist > cfg.minRenderDistance) {
            int newDist = Math.max(cfg.minRenderDistance, renderDist - 1);
            client.options.renderDistance().set(newDist);
            LightMod.LOGGER.debug("[LightMod] FPS low ({} avg), reducing render dist → {}", (int)smoothedFps, newDist);

        } else if (smoothedFps > cfg.targetFps + 15 && renderDist < cfg.maxRenderDistance) {
            int newDist = Math.min(cfg.maxRenderDistance, renderDist + 1);
            client.options.renderDistance().set(newDist);
            LightMod.LOGGER.debug("[LightMod] FPS headroom ({} avg), increasing render dist → {}", (int)smoothedFps, newDist);
        }
    }
}
