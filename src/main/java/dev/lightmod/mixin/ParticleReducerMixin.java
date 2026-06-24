package dev.lightmod.mixin;

import dev.lightmod.LightMod;
import dev.lightmod.config.LightModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * DYNAMIC PARTICLE REDUCER
 *
 * Probabilistically drops particle spawns when FPS is low.
 *
 * Iris-aware:
 *   When Iris shaders are active, shaders make each particle significantly
 *   more expensive. We check if Iris reports shaders as enabled and apply
 *   a stricter skip curve.
 *
 * Skip probability curve:
 *   - fps >= target            → 0%   skip (normal)
 *   - fps = target/2           → 50%  skip
 *   - fps <= 15                → 80%  skip (no shaders) / 90% (with shaders)
 */
@Mixin(ParticleEngine.class)
public abstract class ParticleReducerMixin {

    private static final java.util.Random RNG = new java.util.Random();

    @Inject(
        method = "createParticle",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private <T extends ParticleOptions> void lightmod$maybeSkipParticle(
            T params, double x, double y, double z,
            double dx, double dy, double dz,
            CallbackInfo ci) {

        LightModConfig cfg = LightModConfig.get();
        if (!cfg.dynamicParticles) return;

        Minecraft mc = Minecraft.getInstance();
        int fps    = mc.getFps();
        int target = cfg.targetFps;
        if (fps >= target) return;

        // Detect if Iris shaders are currently active (reflection — soft dependency)
        boolean shadersActive = false;
        if (LightMod.IRIS_PRESENT && cfg.aggressiveParticlesWithShaders) {
            try {
                Class<?> irisApi = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
                Object instance  = irisApi.getMethod("getInstance").invoke(null);
                shadersActive    = (boolean) irisApi.getMethod("isShaderPackInUse").invoke(instance);
            } catch (Exception ignored) { /* Iris API not available */ }
        }

        double maxSkip = shadersActive ? 0.90 : 0.80;
        int    floor   = shadersActive ? 25    : 15;

        double ratio     = Math.max(0.0, (target - fps) / (double)(target - floor));
        double skipChance = Math.min(maxSkip, ratio * maxSkip);

        if (RNG.nextDouble() < skipChance) {
            ci.cancel();
        }
    }
}
