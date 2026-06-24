package dev.lightmod.screen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * ModMenu integration
 *
 * When Mod Menu is installed, this adds a "Configure" button
 * to LightMod's entry in the mod list — exactly like Sodium does.
 *
 * ModMenu is optional (compileOnly). If not installed, this class
 * is simply never loaded.
 */
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> LightModScreenFactory.create(parent);
    }
}
