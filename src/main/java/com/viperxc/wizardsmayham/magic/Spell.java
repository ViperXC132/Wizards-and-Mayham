package com.viperxc.wizardsmayham.magic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface Spell {
    void cast(ServerPlayer player, ItemStack wand, MagicData data);
}
