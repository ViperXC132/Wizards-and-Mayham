package com.viperxc.wizardsmayham.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class MagicScreen extends Screen {
    public MagicScreen() { super(Component.literal("Ancient Magic")); }

    @Override
    protected void init() {
        int left = width / 2 - 100;
        addRenderableWidget(Button.builder(Component.literal("Cycle Slot 1"), b -> cycle(0)).bounds(left, 55, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cycle Slot 2"), b -> cycle(1)).bounds(left, 79, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cycle Slot 3"), b -> cycle(2)).bounds(left, 103, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cycle Slot 4"), b -> cycle(3)).bounds(left, 127, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cycle Slot 5"), b -> cycle(4)).bounds(left, 151, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose()).bounds(left, 185, 200, 20).build());
    }

    private void cycle(int slot) {
        if (minecraft != null && minecraft.player != null && minecraft.getConnection() != null)
            minecraft.getConnection().sendCommand("magic cycle " + slot);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Do not call renderBackground(): with VulkanMod it triggers "Can only blur once per frame".
        // Draw a simple translucent panel instead.
        int panelLeft = width / 2 - 110;
        int panelTop = 20;
        graphics.fill(panelLeft, panelTop, panelLeft + 220, panelTop + 200, 0xC0101010);
        graphics.drawCenteredString(font, title, width / 2, 28, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.literal("5-slot spell loadout • server validated"), width / 2, 42, 0xA0A0A0);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
