package kirby.exampletnt;

import kirby.exampletnt.provider.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExampleTNT.MOD_ID)
public class ExampleTNT {

    public static final String MOD_ID = "exampletnt";

    public ExampleTNT() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::registerProviders);
        ExampleTNTBlocks.register(modEventBus);
        ExampleTNTItems.register(modEventBus);
    }

    private void registerProviders(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        gen.addProvider(event.includeClient(), new ExampleTNTItemModelProvider(packOutput, fileHelper));
        gen.addProvider(event.includeClient(), new ExampleTNTBlockStateProvider(packOutput, fileHelper));
        gen.addProvider(event.includeClient(), new ExampleTNTLangProvider.ExampleTNTLangUS(gen.getPackOutput()));
        gen.addProvider(event.includeClient(), new ExampleTNTLangProvider.ExampleTNTLangJP(gen.getPackOutput()));
        gen.addProvider(event.includeServer(), new ExampleTNTRecipeProvider(gen.getPackOutput()));
        gen.addProvider(event.includeServer(), new ExampleTNTAdvancementProvider(packOutput, event.getLookupProvider(), fileHelper));

        ExampleTNTBlockTagsProvider blockTagsProvider = new ExampleTNTBlockTagsProvider(packOutput, event.getLookupProvider(), fileHelper);
        gen.addProvider(event.includeServer(), blockTagsProvider);
        gen.addProvider(event.includeServer(), new ExampleTNTItemTagsProvider(packOutput, event.getLookupProvider(), blockTagsProvider.contentsGetter(), fileHelper));
    }
}