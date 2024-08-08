package net.tukiguti.lolmod.stat.mana;

import net.tukiguti.lolmod.stat.mana.capability.IMana;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ManaSystem {
    public static final Capability<IMana> MANA = CapabilityManager.get(new CapabilityToken<>(){});

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IMana.class);
    }
}