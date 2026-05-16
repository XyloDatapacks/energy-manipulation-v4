package xylo_datapacks.energy_manipulation.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import xylo_datapacks.energy_manipulation.client.datagen.EnergyManipulationLangProvider;
import xylo_datapacks.energy_manipulation.client.datagen.EnergyManipulationModelProvider;
import xylo_datapacks.energy_manipulation.client.datagen.EnergyManipulationRecipeProvider;

public class EnergyManipulationDataGenerator implements DataGeneratorEntrypoint {
	
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		
		pack.addProvider(EnergyManipulationModelProvider::new);
		pack.addProvider(EnergyManipulationLangProvider::new);
		pack.addProvider(EnergyManipulationRecipeProvider::new);
	}
}
