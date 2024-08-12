package net.tukiguti.lolmod.mana;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "lolmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCapabilities {

    public static final Capability<ManaManager> MANA = CapabilityManager.get(new CapabilityToken<>(){});

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Player> event) {
        if (!event.getObject().getCapability(MANA).isPresent()) {
            event.addCapability(new ResourceLocation("lolmod", "mana"), new ICapabilityProvider() {
                private final LazyOptional<ManaManager> instance = LazyOptional.of(() -> new ManaManager(event.getObject()));

                @Override
                public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
                    return MANA.orEmpty(cap, instance);
                }
            });
        }
    }
}