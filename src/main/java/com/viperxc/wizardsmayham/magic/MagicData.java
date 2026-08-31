package com.viperxc.wizardsmayham.magic;

import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;

/** Server-authoritative progression state for one player. */
public final class MagicData {
    public static final int LOADOUT_SIZE = 5;

    private boolean decided;
    private boolean magician;
    private int magicXp;
    private int magicLevel = 1;
    private int wandLevel = 1;
    private int mana = 100;
    private int maxMana = 100;
    private int energy = 100;
    private int maxEnergy = 100;
    private long money;
    private int worthiness;
    private int selectedSpell;
    private final boolean[] unlocked = new boolean[35];
    private final String[] loadout = new String[LOADOUT_SIZE];

    public MagicData() {
        unlocked[0] = true;
        loadout[0] = "fireball";
    }

    public static MagicData fromTag(CompoundTag tag) {
        MagicData d = new MagicData();
        d.decided = tag.getBooleanOr("Decided", false);
        d.magician = tag.getBooleanOr("Magician", false);
        d.magicXp = tag.getIntOr("MagicXp", 0);
        d.magicLevel = Math.max(1, tag.getIntOr("MagicLevel", 1));
        d.wandLevel = Math.max(1, Math.min(5, tag.getIntOr("WandLevel", 1)));
        d.maxMana = Math.max(1, tag.getIntOr("MaxMana", 100));
        d.mana = Math.max(0, Math.min(d.maxMana, tag.getIntOr("Mana", d.maxMana)));
        d.maxEnergy = Math.max(1, tag.getIntOr("MaxEnergy", 100));
        d.energy = Math.max(0, Math.min(d.maxEnergy, tag.getIntOr("Energy", d.maxEnergy)));
        d.money = Math.max(0, tag.getLongOr("Money", 0L));
        d.worthiness = Math.max(0, tag.getIntOr("Worthiness", 0));
        d.selectedSpell = Math.max(0, Math.min(4, tag.getIntOr("SelectedSpell", 0)));
        Arrays.fill(d.unlocked, false);
        CompoundTag spells = tag.getCompoundOrEmpty("Unlocked");
        for (int i = 0; i < d.unlocked.length; i++) d.unlocked[i] = spells.getBooleanOr("s" + i, false);
        for (int i = 0; i < LOADOUT_SIZE; i++) {
            String id = tag.getStringOr("Loadout" + i, "");
            d.loadout[i] = id.isBlank() ? null : id;
        }
        return d;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Decided", decided);
        tag.putBoolean("Magician", magician);
        tag.putInt("MagicXp", magicXp);
        tag.putInt("MagicLevel", magicLevel);
        tag.putInt("WandLevel", wandLevel);
        tag.putInt("Mana", mana);
        tag.putInt("MaxMana", maxMana);
        tag.putInt("Energy", energy);
        tag.putInt("MaxEnergy", maxEnergy);
        tag.putLong("Money", money);
        tag.putInt("Worthiness", worthiness);
        tag.putInt("SelectedSpell", selectedSpell);
        CompoundTag spells = new CompoundTag();
        for (int i = 0; i < unlocked.length; i++) spells.putBoolean("s" + i, unlocked[i]);
        tag.put("Unlocked", spells);
        for (int i = 0; i < LOADOUT_SIZE; i++) tag.putString("Loadout" + i, loadout[i] == null ? "" : loadout[i]);
        return tag;
    }

    public boolean decided() { return decided; }
    public void decide(boolean magician) { this.decided = true; this.magician = magician; }
    public boolean magician() { return magician; }
    public int magicXp() { return magicXp; }
    public int magicLevel() { return magicLevel; }
    public int wandLevel() { return wandLevel; }
    public void wandLevel(int value) { wandLevel = Math.max(1, Math.min(5, value)); }
    public int mana() { return mana; }
    public int maxMana() { return maxMana; }
    public int energy() { return energy; }
    public int maxEnergy() { return maxEnergy; }
    public long money() { return money; }
    public int worthiness() { return worthiness; }
    public int selectedSpell() { return selectedSpell; }
    public void selectedSpell(int slot) { selectedSpell = Math.max(0, Math.min(4, slot)); }
    public String loadout(int slot) { return loadout[slot]; }
    public void loadout(int slot, String id) { if (slot >= 0 && slot < LOADOUT_SIZE) loadout[slot] = id; }
    public boolean unlocked(int index) { return index >= 0 && index < unlocked.length && unlocked[index]; }
    public void unlock(int index) { if (index >= 0 && index < unlocked.length) unlocked[index] = true; }
    public void unlockAll() { Arrays.fill(unlocked, true); }
    public void mana(int value) { mana = Math.max(0, Math.min(maxMana, value)); }
    public void energy(int value) { energy = Math.max(0, Math.min(maxEnergy, value)); }
    public void money(long value) { money = Math.max(0, value); }
    public void addMoney(long value) { money = Math.max(0, money + value); }
    public void worthiness(int value) { worthiness = Math.max(0, value); }
    public void addWorthiness(int value) { worthiness = Math.max(0, worthiness + value); }
    public void addXp(int amount) {
        if (amount <= 0) return;
        magicXp += amount;
        while (magicXp >= xpForNextLevel() && magicLevel < 100) magicLevel++;
    }
    public int xpForNextLevel() { return 100 + (magicLevel - 1) * 75; }
    public void regen() {
        if (mana < maxMana) mana++;
        if (energy < maxEnergy) energy++;
    }
}
