package com.viperxc.wizardsmayham.magic;

import com.viperxc.wizardsmayham.WizardsMayham;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Persistent world-backed store; never trusts client progression values. */
public final class MagicDataStore extends SavedData {
    private final Map<UUID, MagicData> players = new HashMap<>();

    private static final Factory<MagicDataStore> FACTORY = new Factory<>(MagicDataStore::new, MagicDataStore::load, null);

    private static MagicDataStore load(CompoundTag tag, net.minecraft.core.RegistryAccess registries) {
        MagicDataStore store = new MagicDataStore();
        CompoundTag all = tag.getCompoundOrEmpty("Players");
        for (String key : all.keySet()) {
            try { store.players.put(UUID.fromString(key), MagicData.fromTag(all.getCompoundOrEmpty(key))); }
            catch (IllegalArgumentException ignored) { }
        }
        return store;
    }

    private MagicDataStore() { }

    public static MagicDataStore get(MinecraftServer server) {
        ServerLevel level = server.overworld();
        return level.getDataStorage().computeIfAbsent(FACTORY, WizardsMayham.MOD_ID + "_player_magic");
    }

    public MagicData get(UUID uuid) {
        return players.computeIfAbsent(uuid, ignored -> new MagicData());
    }

    public void markDirty() { setDirty(); }

    @Override
    public CompoundTag save(CompoundTag tag, net.minecraft.core.RegistryAccess registries) {
        CompoundTag all = new CompoundTag();
        players.forEach((uuid, data) -> all.put(uuid.toString(), data.toTag()));
        tag.put("Players", all);
        return tag;
    }
}
