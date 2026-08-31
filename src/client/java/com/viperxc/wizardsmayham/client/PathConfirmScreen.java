package com.viperxc.wizardsmayham.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Second step: confirm Magician or Human choice. */
public final class PathConfirmScreen extends Screen {
    private final boolean magician;

    public PathConfirmScreen(boolean magician) {
        super(Component.literal("Confirm Path"));
        this.magician = magician;
    }

    @Override
    protected void init() {
        int left = width / 2 - 100;
        String label = magician ? "Magician" : "Human";

        addRenderableWidget(Button.builder(Component.literal("Confirm: " + label), b -> {
            try {
                if (minecraft != null && minecraft.getConnection() != null) {
                    minecraft.getConnection().sendCommand("magic setpath " + (magician ? "magician" : "human"));
                }
            } catch (Throwable ignored) {
            }
            onClose();
        }).bounds(left, 100, 200, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Back"), b -> {
            if (minecraft != null) minecraft.setScreen(new PathChoiceScreen());
        }).bounds(left, 130, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int panelLeft = width / 2 - 120;
        graphics.fill(panelLeft, 50, panelLeft + 240, 170, 0xC0101010);
        graphics.drawCenteredString(font, title, width / 2, 65, 0xFFFFFF);
        String msg = magician
                ? "Become a Magician? (wand + book, spell system)"
                : "Remain Human? (no magic progression)";
        graphics.drawCenteredString(font, Component.literal(msg), width / 2, 82, 0xA0A0A0);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
