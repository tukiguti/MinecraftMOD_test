package net.tukiguti.lolmod.stat.mana.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

public interface IMana extends INBTSerializable<CompoundTag> {
    int getMana();
    void setMana(int mana);
    int getMaxMana();
    void setMaxMana(int maxMana);
    void consumeMana(int amount);
    void regenerateMana(int amount);
    void updateMaxMana(Player player);
}