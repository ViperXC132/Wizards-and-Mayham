package com.viperxc.wizardsmayham.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

public final class WizardsMayhamClient implements ClientModInitializer {
    private static final KeyMapping OPEN_MAGIC = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.wizardsmayham.open_magic",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_G,
            KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath("wizardsmayham", "magic"))
    ));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_MAGIC.consumeClick()) {
                if (client.player != null) client.setScreen(new MagicScreen());
            }
        });
    }
}
