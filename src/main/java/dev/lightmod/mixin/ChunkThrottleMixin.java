package dev.lightmod.mixin;

import dev.lightmod.LightMod;
import dev.lightmod.config.LightModConfig;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * CHUNK LOAD THROTTLE
 *
 * Limits how many chunk meshes are uploaded to the GPU per frame.
 * Vanilla can burst 10+ uploads in one frame, causing stutters.
 *
 * ⚠ AUTOMATICALLY DISABLED when Sodium is present:
 *   Sodium replaces ChunkRenderDispatcher entirely with its own
 *   upload scheduler that already handles this more efficiently.
 *
 * require=0 means: if this injection point doesn't exist (because
 * Sodium replaced the class), the mixin is silently skipped — no crash.
 */
@Mixin(value = ChunkRenderDispatcher.class, priority = 900)
public abstract class ChunkThrottleMixin {

    @ModifyVariable(
        method = "upload",
        at = @At("HEAD"),
        argsOnly = false,
        index = 1,
        require = 0
    )
    private int lightmod$limitChunkUploads(int original) {
        if (LightMod.SODIUM_PRESENT) return original; // let Sodium handle it
        if (!LightModConfig.get().chunkLoadThrottle) return original;
        return Math.min(original, LightModConfig.get().maxChunksPerTick);
    }
}
