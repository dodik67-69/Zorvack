package dev.lightmod;

import dev.lightmod.config.LightModConfig;
import dev.lightmod.screen.LightModScreenFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LightMod implements ClientModInitializer {

    public static final String MOD_ID = "lightmod";
    public static final Logger LOGGER  = LoggerFactory.getLogger(MOD_ID);

    public static boolean SODIUM_PRESENT = false;
    public static boolean IRIS_PRESENT   = false;

    public static KeyMapping openSettingsKey;

    @Override
    public void onInitializeClient() {
        SODIUM_PRESENT = isLoaded("sodium");
        IRIS_PRESENT   = isLoaded("iris");

        LOGGER.info("[LightMod] Sodium: {} | Iris: {}", SODIUM_PRESENT, IRIS_PRESENT);

        LightModConfig cfg = LightModConfig.get();
        cfg.applyModAwareDefaults();

        // Keybind: press O to open LightMod settings (same key as Iris shader picker — different category)
        openSettingsKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.lightmod.open_settings",
            GLFW.GLFW_KEY_L,
            "category.lightmod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Open settings screen on keypress
            while (openSettingsKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(LightModScreenFactory.create(null));
                }
            }
            // Dynamic FPS optimizer
            DynamicOptimizer.tick(client);
        });

        LOGGER.info("[LightMod] v2.0 ready. Press L to open settings.");
    }

    private static boolean isLoaded(String modId) {
        try {
            return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded(modId);
        } catch (Exception e) { return false; }
    }
}
