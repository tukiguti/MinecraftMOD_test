package net.tukiguti.lolmod.stat.mana;

import net.tukiguti.lolmod.stat.mana.capability.IMana;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

public class ManaImpl implements IMana {
    private int mana;
    private int maxMana;

    public ManaImpl() {
        this.maxMana = 100;  // 初期の最大マナ値
        this.mana = this.maxMana;  // 初期マナを最大値に設定
    }

    @Override
    public int getMana() {
        return mana;
    }

    @Override
    public void setMana(int mana) {
        this.mana = Math.min(mana, maxMana);
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    @Override
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
        this.mana = Math.min(this.mana, maxMana);
    }

    @Override
    public void consumeMana(int amount) {
        this.mana = Math.max(0, this.mana - amount);
    }

    @Override
    public void regenerateMana(int amount) {
        this.mana = Math.min(this.maxMana, this.mana + amount);
    }

    @Override
    public void updateMaxMana(Player player) {
        int baseMaxMana = 100;
        int levelBonus = player.experienceLevel * 10;  // レベルごとに10マナ増加
        this.maxMana = baseMaxMana + levelBonus;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("mana", mana);
        tag.putInt("maxMana", maxMana);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        mana = nbt.getInt("mana");
        maxMana = nbt.getInt("maxMana");
    }
}