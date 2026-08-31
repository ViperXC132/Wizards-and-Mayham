package com.viperxc.wizardsmayham.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** First step: pick Magician or Human. */
public final class PathChoiceScreen extends Screen {
    public PathChoiceScreen() {
        super(Component.literal("Choose Your Path"));
    }

    @Override
    protected void init() {
        int left = width / 2 - 100;
        addRenderableWidget(Button.builder(Component.literal("Magician"), b -> {
            if (minecraft != null) minecraft.setScreen(new PathConfirmScreen(true));
        }).bounds(left, 80, 200, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Human"), b -> {
            if (minecraft != null) minecraft.setScreen(new PathConfirmScreen(false));
        }).bounds(left, 110, 200, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> onClose())
                .bounds(left, 150, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Vulkan-safe: no renderBackground() blur.
        int panelLeft = width / 2 - 120;
        graphics.fill(panelLeft, 40, panelLeft + 240, 190, 0xC0101010);
        graphics.drawCenteredString(font, title, width / 2, 55, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.literal("Do you want to be a Magician or a Human?"), width / 2, 70, 0xA0A0A0);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
