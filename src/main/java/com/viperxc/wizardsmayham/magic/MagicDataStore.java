package com.viperxc.wizardsmayham.magic;

import com.mojang.serialization.Codec;
import com.viperxc.wizardsmayham.WizardsMayham;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Persistent world-backed store; never trusts client progression values. */
public final class MagicDataStore extends SavedData {
    private final Map<UUID, MagicData> players = new HashMap<>();

    private static final Codec<MagicDataStore> CODEC = CompoundTag.CODEC.xmap(MagicDataStore::fromTag, MagicDataStore::toTag);
    private static final SavedDataType<MagicDataStore> TYPE = new SavedDataType<>(WizardsMayham.MOD_ID + "_player_magic", MagicDataStore::new, CODEC, null);

    private static MagicDataStore fromTag(CompoundTag tag) {
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
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public MagicData get(UUID uuid) {
        return players.computeIfAbsent(uuid, ignored -> new MagicData());
    }

    public void markDirty() { setDirty(); }

    private CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        CompoundTag all = new CompoundTag();
        players.forEach((uuid, data) -> all.put(uuid.toString(), data.toTag()));
        tag.put("Players", all);
        return tag;
    }

    @Override
    public CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        CompoundTag all = new CompoundTag();
        players.forEach((uuid, data) -> all.put(uuid.toString(), data.toTag()));
        tag.put("Players", all);
        return tag;
    }
}
