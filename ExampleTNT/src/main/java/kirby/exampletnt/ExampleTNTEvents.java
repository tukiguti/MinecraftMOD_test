package kirby.exampletnt;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleTNT.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExampleTNTEvents {

    public static CreativeModeTab TAB_TNT;

    @SubscribeEvent
    public static void creativeTabsBuildEvent(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ExampleTNTItems.SMALL_TNT.get());
            event.accept(ExampleTNTItems.BIG_TNT.get());
        }else if (event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ExampleTNTBlocks.LARGE_TNT.get());
        }
    }
    @SubscribeEvent
    public static void creativeTabRegisterEvent(CreativeModeTabEvent.Register event) {
        TAB_TNT = event.registerCreativeModeTab(new ResourceLocation(ExampleTNT.MOD_ID, "tab_tnt"),
                builder -> builder
                        .icon(() -> ExampleTNTItems.SMALL_TNT.get().getDefaultInstance())
                        .displayItems((parameters, output) -> {
                            output.accept(ExampleTNTItems.SMALL_TNT.get());
                            output.accept(ExampleTNTItems.BIG_TNT.get());
                            output.accept(ExampleTNTBlocks.LARGE_TNT.get());
                            output.accept(Blocks.TNT);
                        })
                        .title(Component.translatable("block.minecraft.tnt")));
    }
}