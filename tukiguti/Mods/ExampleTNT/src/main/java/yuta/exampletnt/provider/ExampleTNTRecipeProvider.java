package yuta.exampletnt.provider;

import yuta.exampletnt.ExampleTNT;
import yuta.exampletnt.ExampleTNTBlocks;
import yuta.exampletnt.ExampleTNTItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class ExampleTNTRecipeProvider extends RecipeProvider {
    public ExampleTNTRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ExampleTNTItems.SMALL_TNT.get(), 3)
                .requires(ExampleTNTBlocks.LARGE_TNT.get())
                .group("exampletnt")
                .unlockedBy("has_large_tnt", has(ExampleTNTBlocks.LARGE_TNT.get()))
                .save(consumer, new ResourceLocation(ExampleTNT.MOD_ID, "small_tnt"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ExampleTNTBlocks.LARGE_TNT.get(), 2)
                .define('#', Blocks.TNT)
                .define('G', Items.GUNPOWDER)
                .pattern(" # ").pattern("GGG").pattern(" # ")
                .group("exampletnt")
                .unlockedBy("has_tnt", has(Blocks.TNT))
                .save(consumer, new ResourceLocation(ExampleTNT.MOD_ID, "large_tnt"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ExampleTNTItems.BIG_TNT.get(), 2)
                .define('T', ExampleTNTItems.SMALL_TNT.get())
                .define('G', Items.GUNPOWDER)
                .pattern(" T ").pattern("GGG").pattern(" T ")
                .group("exampletnt")
                .unlockedBy("has_small_tnt", has(ExampleTNTItems.SMALL_TNT.get()))
                .save(consumer, new ResourceLocation(ExampleTNT.MOD_ID, "big_tnt"));
    }
}
