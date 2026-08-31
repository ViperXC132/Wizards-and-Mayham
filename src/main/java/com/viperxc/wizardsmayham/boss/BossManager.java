package com.viperxc.wizardsmayham.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.raid.Ravager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Seven boss identities backed by vanilla models/entities. */
public final class BossManager {
    public record BossSpec(String id, String name, EntityType<? extends Mob> type, double health, double damage, int worthiness, String weakness) {}
    private record Active(LivingEntity entity, ServerBossEvent bar, BossSpec spec) {}

    private static final Map<UUID, Active> ACTIVE = new HashMap<>();
    private static final List<BlockPos> NATURAL_SPAWN_HISTORY = new ArrayList<>();
    private static int naturalCooldown = 20 * 60 * 30;

    public static final List<BossSpec> BOSSES = List.of(
            new BossSpec("gravebound", "Gravebound", EntityType.ZOMBIE, 180, 12, 1, "fire"),
            new BossSpec("frostmaw", "Frostmaw", EntityType.STRAY, 220, 14, 1, "fire"),
            new BossSpec("emberlord", "Emberlord", EntityType.BLAZE, 260, 16, 2, "frost/water"),
            new BossSpec("stormcaller", "Stormcaller", EntityType.SKELETON, 300, 18, 2, "earth"),
            new BossSpec("the_hollow", "The Hollow", EntityType.WITHER_SKELETON, 340, 20, 2, "light"),
            new BossSpec("bonecrusher", "Bonecrusher", EntityType.RAVAGER, 500, 25, 3, "lightning"),
            new BossSpec("archmage_malvorn", "Archmage Malvorn", EntityType.EVOKER, 800, 30, 5, "counter-magic")
    );

    public static LivingEntity summon(ServerLevel level, BlockPos pos, String id) {
        BossSpec spec = BOSSES.stream().filter(b -> b.id().equalsIgnoreCase(id)).findFirst().orElse(null);
        if (spec == null) return null;
        LivingEntity entity = spec.type().spawn(level, pos, EntitySpawnReason.COMMAND);
        if (entity == null) return null;
        AttributeInstance health = entity.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) health.setBaseValue(spec.health());
        AttributeInstance damage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) damage.setBaseValue(spec.damage());
        entity.setHealth((float) spec.health());
        entity.setCustomName(Component.literal(spec.name()));
        entity.setCustomNameVisible(true);
        ServerBossEvent bar = new ServerBossEvent(Component.literal(spec.name()), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
        bar.setProgress(1.0f);
        for (ServerPlayer player : level.players()) bar.addPlayer(player);
        ACTIVE.put(entity.getUUID(), new Active(entity, bar, spec));
        return entity;
    }

    public static void tick(ServerLevel level) {
        List<UUID> dead = new ArrayList<>();
        for (Map.Entry<UUID, Active> entry : ACTIVE.entrySet()) {
            Active active = entry.getValue();
            LivingEntity entity = active.entity();
            if (!entity.isAlive()) {
                active.bar().removeAllPlayers();
                dead.add(entry.getKey());
                continue;
            }
            active.bar().setProgress(Math.max(0.0f, entity.getHealth() / entity.getMaxHealth()));
            for (ServerPlayer player : level.players()) {
                if (player.distanceToSqr(entity) < 128 * 128) active.bar().addPlayer(player);
                else active.bar().removePlayer(player);
            }
            if (entity.tickCount % 400 == 0) specialAbility(level, active);
        }
        dead.forEach(ACTIVE::remove);
        if (naturalCooldown > 0) naturalCooldown--;
    }

    private static void specialAbility(ServerLevel level, Active active) {
        LivingEntity boss = active.entity();
        if (boss instanceof Ravager) {
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(5), e -> e != boss && e.isAlive()))
                target.hurtServer(level, boss.damageSources().mobAttack(boss), 12.0f);
            return;
        }
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(7), e -> e != boss && e.isAlive()))
            target.hurtServer(level, boss.damageSources().magic(), 8.0f);
    }

    public static void naturalTick(ServerLevel level) {
        if (naturalCooldown > 0 || !ACTIVE.isEmpty() || level.players().isEmpty()) return;
        ServerPlayer player = level.players().get(level.random.nextInt(level.players().size()));
        int x = player.blockPosition().getX() + level.random.nextInt(1001) - 500;
        int z = player.blockPosition().getZ() + level.random.nextInt(1001) - 500;
        BlockPos pos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
        if (!level.getBlockState(pos.below()).isSolidRender()) return;
        if (NATURAL_SPAWN_HISTORY.stream().anyMatch(old -> old.distSqr(pos) < 2000L * 2000L)) return;
        BossSpec spec = BOSSES.get(level.random.nextInt(BOSSES.size()));
        if (summon(level, pos, spec.id()) != null) {
            NATURAL_SPAWN_HISTORY.add(pos);
            naturalCooldown = 20 * 60 * 30;
        }
    }

    public static void initialize() {}
}
