package xylo_datapacks.energy_manipulation.client.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import org.jspecify.annotations.NonNull;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItems;

public class EnergyManipulationModelProvider extends FabricModelProvider {


    public EnergyManipulationModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(@NonNull BlockModelGenerators blockModelGenerators) {
        
    }

    @Override
    public void generateItemModels(@NonNull ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(EnergyManipulationItems.SPELL_BOOK, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(EnergyManipulationItems.SPELL_SCROLL, ModelTemplates.FLAT_ITEM);
    }
}
