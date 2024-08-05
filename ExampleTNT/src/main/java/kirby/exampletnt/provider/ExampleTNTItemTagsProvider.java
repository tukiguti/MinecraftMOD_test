package kirby.exampletnt.provider;

import kirby.exampletnt.ExampleTNT;
import kirby.exampletnt.ExampleTNTItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ExampleTNTItemTagsProvider extends ItemTagsProvider {
    public static final TagKey<Item> TOSS_EXPLOSIVE = ItemTags.create(new ResourceLocation(ExampleTNT.MOD_ID, "toss_explosive"));

    public ExampleTNTItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> future, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, future, ExampleTNT.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(TOSS_EXPLOSIVE).add(Items.CREEPER_SPAWN_EGG, ExampleTNTItems.SMALL_TNT.get());
    }
}