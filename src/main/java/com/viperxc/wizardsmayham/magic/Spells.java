package com.viperxc.wizardsmayham.magic;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

/**
 * The initial spell catalogue. Effects are deliberately server-side and block-safe.
 * New spells can be added with one registration call.
 */
public final class Spells {
    private static final String[][] NAMES = {
            {"fireball","frost_bolt","arcane_bolt","healing_touch","hunger_restore","wind_blast","arcane_shield"},
            {"lightning_strike","flame_wave","frost_nova","dash","life_drain","arcane_sight","natures_grasp"},
            {"chain_lightning","shadow_orb","blink","barrier","gravity_well","stone_prison","soul_burst"},
            {"meteor","tornado","acid_rain","moonlight","solar_burst","necrotic_pulse","teleportation_circle"},
            {"mana_barrier","spell_overcharge","arcane_storm","forbidden_flame","dimensional_rift","soul_collapse","arcane_cataclysm"}
    };

    public static void initialize() {
        for (int level = 1; level <= NAMES.length; level++) {
            for (int i = 0; i < NAMES[level - 1].length; i++) {
                String id = NAMES[level - 1][i];
                int cost = 8 + level * 7 + i;
                int cooldown = 10 + level * 10;
                SpellRegistry.register(id, level, cost, cooldown, effect(level, i));
            }
        }
    }

    private static Spell effect(int level, int index) {
        return (player, wand, data) -> {
            if (index == 3 && level == 1) { player.heal(8.0f); return; }
            if (index == 4 && level == 1) { player.getFoodData().eat(6, 0.6f); return; }
            if (index == 3 && level == 2) { dash(player, 1.2); return; }
            if (index == 2 && level == 3) { dash(player, 2.0); return; }
            if (index == 6 && level == 4) { player.setPos(player.getX(), player.getY() + 1.0, player.getZ()); return; }
            damageArea(player, 2.5f + level * 2.5f, 3.5 + level * 1.5);
        };
    }

    private static void damageArea(ServerPlayer player, float damage, double radius) {
        ServerLevel level = player.serverLevel();
        AABB box = player.getBoundingBox().inflate(radius);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive())) {
            target.hurtServer(level, player.damageSources().magic(), damage);
        }
    }

    private static void dash(ServerPlayer player, double strength) {
        var look = player.getLookAngle().normalize();
        player.setDeltaMovement(look.x * strength, Math.max(0.35, look.y * strength), look.z * strength);
        player.hurtMarked = true;
    }
}
