package dev.lightmod.screen;

import dev.lightmod.LightMod;
import dev.lightmod.config.LightModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * LightMod Settings Screen
 *
 * Built with Cloth Config API — same library used by many Sodium addons.
 * Opens via: Options → Video Settings → LightMod Settings
 *            OR press [L] in-game
 *
 * Layout mirrors Sodium's style:
 *   General  |  Rendering  |  Advanced
 */
public class LightModScreenFactory {

    public static Screen create(Screen parent) {
        LightModConfig cfg = LightModConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.literal("§6LightMod §7Settings"))
            .setSavingRunnable(() -> {
                cfg.save();
                LightMod.LOGGER.info("[LightMod] Config saved from settings screen.");
            })
            .setTransparentBackground(false);

        ConfigEntryBuilder entry = builder.entryBuilder();

        // ─────────────────────────────────────────────────────────
        //  CATEGORY 1: General
        // ─────────────────────────────────────────────────────────
        ConfigCategory general = builder.getOrCreateCategory(
            Component.literal("§aGeneral")
        );

        general.addEntry(entry
            .startBooleanToggle(
                Component.literal("Dynamic FPS Optimizer"),
                cfg.dynamicFpsOptimizer
            )
            .setDefaultValue(true)
            .setTooltip(Component.literal(
                "Automatically adjusts render distance\n" +
                "to maintain your target FPS.\n" +
                "Works with and without Sodium."
            ))
            .setSaveConsumer(val -> cfg.dynamicFpsOptimizer = val)
            .build()
        );

        general.addEntry(entry
            .startIntSlider(
                Component.literal("Target FPS"),
                cfg.targetFps, 20, 240
            )
            .setDefaultValue(60)
            .setTooltip(Component.literal(
                "The FPS the optimizer tries to maintain.\n" +
                "Render distance drops if FPS falls 10+ below this,\n" +
                "and rises if FPS exceeds this by 15+."
            ))
            .setSaveConsumer(val -> cfg.targetFps = val)
            .build()
        );

        general.addEntry(entry
            .startIntSlider(
                Component.literal("Min Render Distance"),
                cfg.minRenderDistance, 2, 16
            )
            .setDefaultValue(4)
            .setTooltip(Component.literal("Optimizer will never go below this value."))
            .setSaveConsumer(val -> cfg.minRenderDistance = val)
            .build()
        );

        general.addEntry(entry
            .startIntSlider(
                Component.literal("Max Render Distance"),
                cfg.maxRenderDistance, 4, 32
            )
            .setDefaultValue(12)
            .setTooltip(Component.literal("Optimizer will never exceed this value."))
            .setSaveConsumer(val -> cfg.maxRenderDistance = val)
            .build()
        );

        // ─────────────────────────────────────────────────────────
        //  CATEGORY 2: Rendering
        // ─────────────────────────────────────────────────────────
        ConfigCategory rendering = builder.getOrCreateCategory(
            Component.literal("§bRendering")
        );

        rendering.addEntry(entry
            .startBooleanToggle(
                Component.literal("Entity Culling"),
                cfg.entityCulling
            )
            .setDefaultValue(true)
            .setTooltip(Component.literal(
                "Skip rendering entities behind the camera.\n" +
                (LightMod.SODIUM_PRESENT
                    ? "§eCurrently overridden by Sodium (Sodium does this better)."
                    : "Active — Sodium not detected.")
            ))
            .setSaveConsumer(val -> cfg.entityCulling = val)
            .build()
        );

        rendering.addEntry(entry
            .startBooleanToggle(
                Component.literal("Auto Clouds"),
                cfg.autoClouds
            )
            .setDefaultValue(true)
            .setTooltip(Component.literal(
                "Automatically disables cloud rendering\n" +
                "when FPS drops, and re-enables when FPS recovers.\n" +
                "Safe with Iris shaders."
            ))
            .setSaveConsumer(val -> cfg.autoClouds = val)
            .build()
        );

        rendering.addEntry(entry
            .startBooleanToggle(
                Component.literal("Dynamic Particles"),
                cfg.dynamicParticles
            )
            .setDefaultValue(true)
            .setTooltip(Component.literal(
                "Reduces particle spawn rate when FPS is low.\n" +
                "Extra-aggressive when Iris shaders are active."
            ))
            .setSaveConsumer(val -> cfg.dynamicParticles = val)
            .build()
        );

        rendering.addEntry(entry
            .startBooleanToggle(
                Component.literal("Aggressive Particles with Shaders"),
                cfg.aggressiveParticlesWithShaders
            )
            .setDefaultValue(true)
            .setTooltip(Component.literal(
                "When Iris shaders are active, particles are\n" +
                "much heavier. This applies a stricter reduction curve.\n" +
                (LightMod.IRIS_PRESENT
                    ? "§aIris detected — this setting is active."
                    : "§7Iris not detected — no effect.")
            ))
            .setSaveConsumer(val -> cfg.aggressiveParticlesWithShaders = val)
            .build()
        );

        // ─────────────────────────────────────────────────────────
        //  CATEGORY 3: Advanced
        // ─────────────────────────────────────────────────────────
        ConfigCategory advanced = builder.getOrCreateCategory(
            Component.literal("§cAdvanced")
        );

        advanced.addEntry(entry
            .startBooleanToggle(
                Component.literal("Chunk Load Throttle"),
                cfg.chunkLoadThrottle
            )
            .setDefaultValue(true)
            .setTooltip(Component.literal(
                "Limits chunk mesh uploads per frame.\n" +
                "Reduces stuttering when loading new areas.\n" +
                (LightMod.SODIUM_PRESENT
                    ? "§eCurrently overridden by Sodium."
                    : "Active.")
            ))
            .setSaveConsumer(val -> cfg.chunkLoadThrottle = val)
            .build()
        );

        advanced.addEntry(entry
            .startIntSlider(
                Component.literal("Max Chunks Per Frame"),
                cfg.maxChunksPerTick, 1, 20
            )
            .setDefaultValue(4)
            .setTooltip(Component.literal(
                "Maximum chunk mesh uploads allowed per frame.\n" +
                "Lower = smoother but slower chunk loading.\n" +
                "Vanilla default is ~10. We recommend 4."
            ))
            .setSaveConsumer(val -> cfg.maxChunksPerTick = val)
            .build()
        );

        // Status info rows (read-only)
        advanced.addEntry(entry
            .startTextDescription(Component.literal(
                "§7━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "§7Status:\n" +
                "  Sodium: " + (LightMod.SODIUM_PRESENT ? "§a✔ Detected" : "§c✘ Not found") + "\n" +
                "  Iris:   " + (LightMod.IRIS_PRESENT   ? "§a✔ Detected" : "§c✘ Not found") + "\n" +
                "§7━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            ))
            .build()
        );

        return builder.build();
    }
}
