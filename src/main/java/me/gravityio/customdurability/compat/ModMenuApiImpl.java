package me.gravityio.customdurability.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.gravityio.customdurability.ModConfig;
import me.gravityio.yaclutils.ConfigScreenBuilder;

public class ModMenuApiImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ConfigScreenBuilder.getScreen(ModConfig.GSON, parent);
    }
}
