package dev.lightmod.mixin;

import dev.lightmod.LightMod;
import dev.lightmod.config.LightModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CloudStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * AUTO CLOUDS
 *
 * Disables cloud rendering automatically when FPS is consistently low,
 * restores when FPS recovers.
 *
 * ✅ Safe with Iris: we only modify options.cloudStatus — this is a vanilla
 *    option that Iris does NOT touch. Iris renders clouds via the shader
 *    pipeline, but the option flag still controls whether they appear.
 *
 * ✅ Safe with Sodium: Sodium doesn't change cloud rendering logic.
 *
 * Uses require=0 so it degrades gracefully if LevelRenderer is patched
 * by another mod.
 */
@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class AutoCloudsMixin {

    private static int     lowFpsTicks         = 0;
    private static boolean cloudsDisabledByUs  = false;

    @Inject(
        method = "renderClouds",
        at = @At("HEAD"),
        require = 0
    )
    private void lightmod$autoManageClouds(PoseStack poseStack,
                                            net.minecraft.world.phys.Vec3 cloudColor,
                                            float partialTick,
                                            double camX, double camY, double camZ,
                                            CallbackInfo ci) {

        if (!LightModConfig.get().autoClouds) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        int fps    = mc.getFps();
        int target = LightModConfig.get().targetFps;

        if (fps < target - 10) {
            lowFpsTicks++;
            if (lowFpsTicks >= 5 && !cloudsDisabledByUs) {
                CloudStatus current = mc.options.cloudStatus().get();
                if (current != CloudStatus.OFF) {
                    mc.options.cloudStatus().set(CloudStatus.OFF);
                    cloudsDisabledByUs = true;
                    LightMod.LOGGER.debug("[LightMod] Auto-disabled clouds (FPS: {})", fps);
                }
            }
        } else if (fps > target + 20) {
            lowFpsTicks = 0;
            if (cloudsDisabledByUs) {
                mc.options.cloudStatus().set(CloudStatus.FAST);
                cloudsDisabledByUs = false;
                LightMod.LOGGER.debug("[LightMod] Auto-restored clouds (FPS: {})", fps);
            }
        } else {
            lowFpsTicks = 0;
        }
    }
}
