package com.viperxc.wizardsmayham.item;

import com.viperxc.wizardsmayham.magic.MagicData;
import com.viperxc.wizardsmayham.magic.MagicDataStore;
import com.viperxc.wizardsmayham.magic.SpellRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class WandItem extends Item {
    public WandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
        var server = serverPlayer.getServer();
        if (server == null) return InteractionResult.PASS;
        MagicData data = MagicDataStore.get(server).get(serverPlayer.getUUID());
        if (!data.magician()) return InteractionResult.PASS;
        String spellId = data.loadout(data.selectedSpell());
        if (spellId != null) SpellRegistry.cast(spellId, serverPlayer, stack);
        MagicDataStore.get(server).markDirty();
        return InteractionResult.SUCCESS;
    }
}
