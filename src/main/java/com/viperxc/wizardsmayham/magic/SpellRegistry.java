package com.viperxc.wizardsmayham.magic;

import com.viperxc.wizardsmayham.WizardsMayham;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SpellRegistry {
    private static final Map<String, RegisteredSpell> SPELLS = new LinkedHashMap<>();

    public record RegisteredSpell(String id, int level, int manaCost, int cooldownTicks, Spell spell) {}

    public static void register(String id, int level, int manaCost, int cooldownTicks, Spell spell) {
        SPELLS.put(id, new RegisteredSpell(id, level, manaCost, cooldownTicks, spell));
    }

    public static RegisteredSpell get(String id) { return SPELLS.get(id); }
    public static Map<String, RegisteredSpell> all() { return Map.copyOf(SPELLS); }

    public static void cast(String id, ServerPlayer player, ItemStack wand) {
        RegisteredSpell registered = SPELLS.get(id);
        if (registered == null) return;
        var server = player.getServer();
        if (server == null) return;
        MagicDataStore store = MagicDataStore.get(server);
        MagicData data = store.get(player.getUUID());
        if (!data.magician() || data.wandLevel() < registered.level() || data.mana() < registered.manaCost()) return;
        data.mana(data.mana() - registered.manaCost());
        registered.spell().cast(player, wand, data);
        data.addXp(5 + registered.level() * 2);
        store.markDirty();
    }

    public static void initialize() {
        Spells.initialize();
        WizardsMayham.LOGGER.info("Registered {} magic spells", SPELLS.size());
    }
}
