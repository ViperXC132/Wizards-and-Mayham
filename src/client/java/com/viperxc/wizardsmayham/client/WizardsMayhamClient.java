package com.viperxc.wizardsmayham.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

public final class WizardsMayhamClient implements ClientModInitializer {
    private boolean wasGDown;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try {
                if (client.getWindow() == null) return;
                long handle = client.getWindow().handle();
                boolean gDown = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_G) == GLFW.GLFW_PRESS;
                if (gDown && !wasGDown && client.player != null && client.screen == null) {
                    client.setScreen(new MagicScreen());
                }
                wasGDown = gDown;
            } catch (Throwable ignored) {
                // Window handle API can vary; never crash the client from the key poll.
            }
        });
    }
}
