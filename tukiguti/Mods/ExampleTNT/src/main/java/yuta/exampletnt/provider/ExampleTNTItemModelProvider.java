package yuta.exampletnt.provider;

import yuta.exampletnt.ExampleTNT;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ExampleTNTItemModelProvider extends ItemModelProvider {
    public ExampleTNTItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ExampleTNT.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.singleTexture("small_tnt", mcLoc(folder + "/generated"), "layer0",
                new ResourceLocation("exampletnt", folder + "/" + "small_tnt"));
        this.singleTexture("big_tnt", mcLoc(folder + "/generated"), "layer0",
                new ResourceLocation("exampletnt", folder + "/" + "small_tnt"));
    }
}
