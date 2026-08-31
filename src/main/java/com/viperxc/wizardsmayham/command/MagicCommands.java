package com.viperxc.wizardsmayham.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.viperxc.wizardsmayham.item.ModItems;
import com.viperxc.wizardsmayham.magic.MagicData;
import com.viperxc.wizardsmayham.magic.MagicDataStore;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class MagicCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                Commands.literal("magic")
                        .then(Commands.literal("choose")
                                .then(Commands.literal("magician").executes(ctx -> choose(ctx.getSource().getPlayerOrException(), true)))
                                .then(Commands.literal("human").executes(ctx -> choose(ctx.getSource().getPlayerOrException(), false))))
                        .then(Commands.literal("give")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.literal("wand").executes(ctx -> give(ctx.getSource().getPlayerOrException(), ModItems.MAGIC_WAND)))
                                .then(Commands.literal("book").executes(ctx -> give(ctx.getSource().getPlayerOrException(), ModItems.ANCIENT_MAGIC_BOOK)))
                                .then(Commands.literal("restorer").executes(ctx -> give(ctx.getSource().getPlayerOrException(), ModItems.MANA_RESTORER))))
                        .then(Commands.literal("unlock").requires(source -> source.hasPermission(2))
                                .then(Commands.literal("all").executes(ctx -> { MagicData d = data(ctx.getSource().getPlayerOrException()); d.unlockAll(); dirty(ctx.getSource().getServer()); return ok(ctx, "All spells unlocked."); })))
                        .then(Commands.literal("money").requires(source -> source.hasPermission(2)).then(Commands.argument("amount", LongArgumentType.longArg(0)).executes(ctx -> { MagicData d=data(ctx.getSource().getPlayerOrException()); d.money(LongArgumentType.getLong(ctx,"amount")); dirty(ctx.getSource().getServer()); return ok(ctx,"Money set to "+d.money()+"."); })))
                        .then(Commands.literal("wandlevel").requires(source -> source.hasPermission(2)).then(Commands.argument("level", IntegerArgumentType.integer(1,5)).executes(ctx -> { MagicData d=data(ctx.getSource().getPlayerOrException()); d.wandLevel(IntegerArgumentType.getInteger(ctx,"level")); dirty(ctx.getSource().getServer()); return ok(ctx,"Wand level set to "+d.wandLevel()+"."); })))
                        .then(Commands.literal("mana").requires(source -> source.hasPermission(2)).then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes(ctx -> { MagicData d=data(ctx.getSource().getPlayerOrException()); d.mana(IntegerArgumentType.getInteger(ctx,"amount")); dirty(ctx.getSource().getServer()); return ok(ctx,"Mana set."); })))
                        .then(Commands.literal("energy").requires(source -> source.hasPermission(2)).then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes(ctx -> { MagicData d=data(ctx.getSource().getPlayerOrException()); d.energy(IntegerArgumentType.getInteger(ctx,"amount")); dirty(ctx.getSource().getServer()); return ok(ctx,"Energy set."); })))
                        .then(Commands.literal("worthiness").requires(source -> source.hasPermission(2)).then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes(ctx -> { MagicData d=data(ctx.getSource().getPlayerOrException()); d.worthiness(IntegerArgumentType.getInteger(ctx,"amount")); dirty(ctx.getSource().getServer()); return ok(ctx,"Worthiness set."); })))
                        .then(Commands.literal("help").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("/magic choose <magician|human> | /magic config | /magic help"), false); return 1; }))
        ));
    }

    private static int choose(ServerPlayer player, boolean magician) {
        MagicData d = data(player);
        if (d.decided()) { player.sendSystemMessage(Component.literal("Your path is already chosen. An admin can override it.")); return 0; }
        d.decide(magician);
        if (magician) {
            player.getInventory().placeItemBackInInventory(new net.minecraft.world.item.ItemStack(ModItems.ANCIENT_MAGIC_BOOK));
            player.getInventory().placeItemBackInInventory(new net.minecraft.world.item.ItemStack(ModItems.MAGIC_WAND));
            player.sendSystemMessage(Component.literal("You have become a Magician. Your arcane journey begins."));
        } else player.sendSystemMessage(Component.literal("You remain Human. You may still explore the world without magician progression."));
        dirty(player.server);
        return 1;
    }

    private static int give(ServerPlayer player, net.minecraft.world.item.Item item) { player.getInventory().placeItemBackInInventory(new net.minecraft.world.item.ItemStack(item)); return 1; }
    private static MagicData data(ServerPlayer p) { return MagicDataStore.get(p.server).get(p.getUUID()); }
    private static void dirty(net.minecraft.server.MinecraftServer s) { MagicDataStore.get(s).markDirty(); }
    private static int ok(com.mojang.brigadier.context.CommandContext<net.minecraft.commands.CommandSourceStack> ctx, String msg) { ctx.getSource().sendSuccess(() -> Component.literal(msg), true); return 1; }
}
