package com.viperxc.wizardsmayham.client;

import net.fabricmc.api.ClientModInitializer;

public final class WizardsMayhamClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Client-only initialization will remain isolated from server gameplay.
        // Rendering will use Minecraft/Fabric APIs so VulkanMod can remain optional.
    }
}
