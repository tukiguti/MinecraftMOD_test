package kirby.exampletnt.provider;

import kirby.exampletnt.ExampleTNT;
import kirby.exampletnt.ExampleTNTBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ExampleTNTBlockStateProvider extends BlockStateProvider {
    public ExampleTNTBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ExampleTNT.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.simpleBlockWithItem(ExampleTNTBlocks.LARGE_TNT.get(), this.cubeAll(ExampleTNTBlocks.LARGE_TNT.get()));
    }
}