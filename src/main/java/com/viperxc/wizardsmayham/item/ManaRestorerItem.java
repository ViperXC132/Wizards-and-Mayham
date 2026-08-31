package com.viperxc.wizardsmayham.item;

import com.viperxc.wizardsmayham.magic.MagicData;
import com.viperxc.wizardsmayham.magic.MagicDataStore;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ManaRestorerItem extends Item {
    public ManaRestorerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
        var server = ((ServerLevel) serverPlayer.level()).getServer();
        if (server == null) return InteractionResult.PASS;
        MagicDataStore store = MagicDataStore.get(server);
        MagicData data = store.get(serverPlayer.getUUID());
        int missing = data.maxMana() - data.mana();
        if (missing <= 0 || data.energy() < 25) return InteractionResult.FAIL;
        data.energy(data.energy() - 25);
        data.mana(data.maxMana());
        ItemStack stack = serverPlayer.getItemInHand(hand);
        if (!serverPlayer.getAbilities().instabuild) stack.shrink(1);
        store.markDirty();
        return InteractionResult.SUCCESS;
    }
}
