package yuta.exampletnt.provider;

import yuta.exampletnt.ExampleTNT;
import yuta.exampletnt.ExampleTNTBlocks;
import yuta.exampletnt.ExampleTNTItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ExampleTNTAdvancementProvider extends ForgeAdvancementProvider {
    public ExampleTNTAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new ExampleTNTAdvancementGenerator()));
    }

    public static class ExampleTNTAdvancementGenerator implements AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper helper) {
            Advancement root = Advancement.Builder.advancement()
                    .display(new ItemStack(Blocks.TNT), Component.literal("ExampleTNT"), Component.translatable("block.minecraft.tnt"), new ResourceLocation(ExampleTNT.MOD_ID, "textures/block/large_tnt.png"), FrameType.TASK, true, false, true)
                    .addCriterion("has_tnt", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.TNT))
                    .save(saver, new ResourceLocation(ExampleTNT.MOD_ID, "root"), helper);

            Advancement large_tnt = Advancement.Builder.advancement()
                    .display(new ItemStack(ExampleTNTBlocks.LARGE_TNT.get()), Component.translatable("block.exampletnt.large_tnt"), Component.translatable("block.exampletnt.large_tnt"), null, FrameType.CHALLENGE, true, true, false)
                    .addCriterion("place_large_tnt", PlacedBlockTrigger.TriggerInstance.placedBlock(ExampleTNTBlocks.LARGE_TNT.get()))
                    .addCriterion("has_large_tnt", InventoryChangeTrigger.TriggerInstance.hasItems(ExampleTNTBlocks.LARGE_TNT.get()))
                    .parent(root)
                    .save(saver, new ResourceLocation(ExampleTNT.MOD_ID, "large_tnt"), helper);

            Advancement small_tnt = Advancement.Builder.advancement()
                    .display(new ItemStack(ExampleTNTItems.SMALL_TNT.get()), Component.translatable("item.exampletnt.small_tnt"), Component.translatable("item.exampletnt.small_tnt"), null, FrameType.GOAL, true, true, true)
                    .addCriterion("toss_small_tnt", new ImpossibleTrigger.TriggerInstance())
                    .rewards(AdvancementRewards.Builder.loot(new ResourceLocation("chests/spawn_bonus_chest")))
                    .parent(root)
                    .save(saver, new ResourceLocation(ExampleTNT.MOD_ID, "small_tnt"), helper);
        }
    }
}
