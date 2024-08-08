package net.tukiguti.lolmod.stat.mana.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.tukiguti.lolmod.stat.mana.ManaImpl;
import net.tukiguti.lolmod.stat.mana.ManaSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ManaProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final LazyOptional<IMana> mana = LazyOptional.of(ManaImpl::new);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return ManaSystem.MANA.orEmpty(cap, mana);
    }

    @Override
    public CompoundTag serializeNBT() {
        return mana.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        mana.ifPresent(h -> h.deserializeNBT(nbt));
    }
}