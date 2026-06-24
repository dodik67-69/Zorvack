package dev.lightmod.mixin;

import dev.lightmod.LightMod;
import dev.lightmod.config.LightModConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * ENTITY CULLING
 *
 * Skips rendering entities that are solidly behind the camera.
 *
 * ⚠ This mixin is AUTOMATICALLY DISABLED at runtime when Sodium is present
 *   (Sodium has superior occlusion culling built in).
 *
 * Uses require=0 so it won't crash if Sodium rewrites EntityRenderer.
 */
@Mixin(value = EntityRenderer.class, priority = 900)
public abstract class EntityCullingMixin<T extends Entity> {

    @Inject(
        method = "render",
        at = @At("HEAD"),
        cancellable = true,
        require = 0  // soft — won't crash if Sodium patches this class
    )
    private void lightmod$cullEntity(T entity, float yaw, float partialTick,
                                     PoseStack poseStack, MultiBufferSource buffer,
                                     int packedLight, CallbackInfo ci) {

        // Skip entirely if Sodium is handling this
        if (LightMod.SODIUM_PRESENT) return;
        if (!LightModConfig.get().entityCulling) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gameRenderer == null) return;

        // Never cull the vehicle the player is riding
        if (entity == mc.player.getVehicle()) return;

        Camera cam = mc.gameRenderer.getMainCamera();
        Vec3 camPos = cam.getPosition();
        Vec3 toEntity = entity.position().subtract(camPos);

        // Never cull close entities (within 5 blocks)
        if (toEntity.lengthSqr() < 25.0) return;

        // Build look vector from yaw angle
        float yawRad = (float) Math.toRadians(cam.getYaw());
        Vec3 look = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad));

        // If entity is more than ~115° behind us → skip
        double dot = toEntity.normalize().dot(look);
        if (dot < -0.42) {
            ci.cancel();
        }
    }
}
