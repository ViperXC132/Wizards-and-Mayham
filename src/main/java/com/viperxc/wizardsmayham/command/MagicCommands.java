package com.viperxc.wizardsmayham.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.viperxc.wizardsmayham.boss.BossManager;
import com.viperxc.wizardsmayham.item.ModItems;
import com.viperxc.wizardsmayham.magic.MagicData;
import com.viperxc.wizardsmayham.magic.MagicDataStore;
import com.viperxc.wizardsmayham.magic.SpellRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class MagicCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                Commands.literal("magic")
                        .then(Commands.literal("choose")
                                .then(Commands.literal("magician").executes(ctx -> choose(ctx.getSource().getPlayerOrException(), true)))
                                .then(Commands.literal("human").executes(ctx -> choose(ctx.getSource().getPlayerOrException(), false))))
                        .then(Commands.literal("give").requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.literal("wand").executes(ctx -> give(ctx.getSource().getPlayerOrException(), ModItems.MAGIC_WAND)))
                                .then(Commands.literal("book").executes(ctx -> give(ctx.getSource().getPlayerOrException(), ModItems.ANCIENT_MAGIC_BOOK)))
                                .then(Commands.literal("restorer").executes(ctx -> give(ctx.getSource().getPlayerOrException(), ModItems.MANA_RESTORER))))
                        .then(Commands.literal("summon").requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.argument("boss", StringArgumentType.word()).executes(ctx -> {
                                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                                    ServerLevel level = (ServerLevel) p.level();
                                    var boss = BossManager.summon(level, BlockPos.containing(p.position()), StringArgumentType.getString(ctx, "boss"));
                                    return boss == null ? 0 : ok(ctx, "Boss summoned: " + boss.getName().getString());
                                })))
                        .then(Commands.literal("cycle").then(Commands.argument("slot", IntegerArgumentType.integer(0, 4)).executes(ctx -> cycle(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "slot")))))
                        .then(Commands.literal("unlock").requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.literal("all").executes(ctx -> {
                                    MagicData d = data(ctx.getSource().getPlayerOrException());
                                    d.unlockAll();
                                    dirty(ctx.getSource().getServer());
                                    return ok(ctx, "All spells unlocked.");
                                })))
                        .then(Commands.literal("money").requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.argument("amount", LongArgumentType.longArg(0)).executes(ctx -> {
                                    MagicData d = data(ctx.getSource().getPlayerOrException());
                                    d.money(LongArgumentType.getLong(ctx, "amount"));
                                    dirty(ctx.getSource().getServer());
                                    return ok(ctx, "Money set to " + d.money() + ".");
                                })))
                        .then(Commands.literal("wandlevel").requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.argument("level", IntegerArgumentType.integer(1, 5)).executes(ctx -> {
                                    MagicData d = data(ctx.getSource().getPlayerOrException());
                                    d.wandLevel(IntegerArgumentType.getInteger(ctx, "level"));
                                    dirty(ctx.getSource().getServer());
                                    return ok(ctx, "Wand level set to " + d.wandLevel() + ".");
                                })))
                        .then(Commands.literal("mana").requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes(ctx -> {
                                    MagicData d = data(ctx.getSource().getPlayerOrException());
                                    d.mana(IntegerArgumentType.getInteger(ctx, "amount"));
                                    dirty(ctx.getSource().getServer());
                                    return ok(ctx, "Mana set.");
                                })))
                        .then(Commands.literal("energy").requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes(ctx -> {
                                    MagicData d = data(ctx.getSource().getPlayerOrException());
                                    d.energy(IntegerArgumentType.getInteger(ctx, "amount"));
                                    dirty(ctx.getSource().getServer());
                                    return ok(ctx, "Energy set.");
                                })))
                        .then(Commands.literal("worthiness").requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes(ctx -> {
                                    MagicData d = data(ctx.getSource().getPlayerOrException());
                                    d.worthiness(IntegerArgumentType.getInteger(ctx, "amount"));
                                    dirty(ctx.getSource().getServer());
                                    return ok(ctx, "Worthiness set.");
                                })))
                        .then(Commands.literal("config").requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .executes(ctx -> ok(ctx, "Admin configuration framework is enabled; values are server-authoritative.")))
                        .then(Commands.literal("help").executes(ctx -> {
                            ctx.getSource().sendSuccess(() -> Component.literal("/magic choose <magician|human> | /magic cycle <slot> | /magic summon <boss> | /magic give | /magic config | /magic help"), false);
                            return 1;
                        }))
        ));
    }

    private static int choose(ServerPlayer player, boolean magician) {
        MagicData d = data(player);
        if (d.decided()) {
            player.sendSystemMessage(Component.literal("Your path is already chosen. An admin can override it."));
            return 0;
        }
        d.decide(magician);
        if (magician) {
            player.getInventory().placeItemBackInInventory(new ItemStack(ModItems.ANCIENT_MAGIC_BOOK));
            player.getInventory().placeItemBackInInventory(new ItemStack(ModItems.MAGIC_WAND));
            player.sendSystemMessage(Component.literal("You have become a Magician. Your arcane journey begins."));
        } else {
            player.sendSystemMessage(Component.literal("You remain Human. You may still explore the world without magician progression."));
        }
        dirty(player.getServer());
        return 1;
    }

    private static int cycle(ServerPlayer player, int slot) {
        MagicData d = data(player);
        if (!d.magician()) return 0;
        var spells = SpellRegistry.all().values().stream().toList();
        if (spells.isEmpty()) return 0;
        int current = -1;
        String equipped = d.loadout(slot);
        for (int i = 0; i < spells.size(); i++) {
            if (spells.get(i).id().equals(equipped)) {
                current = i;
                break;
            }
        }
        for (int step = 1; step <= spells.size(); step++) {
            int next = (Math.max(0, current) + step) % spells.size();
            if (d.unlocked(next) && d.wandLevel() >= spells.get(next).level()) {
                d.loadout(slot, spells.get(next).id());
                d.selectedSpell(slot);
                dirty(player.getServer());
                player.sendSystemMessage(Component.literal("Slot " + (slot + 1) + ": " + spells.get(next).id()));
                return 1;
            }
        }
        return 0;
    }

    private static int give(ServerPlayer player, Item item) {
        player.getInventory().placeItemBackInInventory(new ItemStack(item));
        return 1;
    }

    private static MagicData data(ServerPlayer p) {
        return MagicDataStore.get(p.getServer()).get(p.getUUID());
    }

    private static void dirty(MinecraftServer s) {
        if (s != null) MagicDataStore.get(s).markDirty();
    }

    private static int ok(com.mojang.brigadier.context.CommandContext<net.minecraft.commands.CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendSuccess(() -> Component.literal(msg), true);
        return 1;
    }
}
