package xylo_datapacks.energy_manipulation;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xylo_datapacks.energy_manipulation.font.EnergyManipulationFonts;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationComponents;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItems;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorButtonsRegistry;

public class EnergyManipulation implements ModInitializer {
	public static final String MOD_ID = "energy_manipulation";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		
		PolymerResourcePackUtils.addModAssets(MOD_ID);

		EnergyManipulationFonts.initialize();
		GlyphsRegistry.initialize();
		SpellEditorButtonsRegistry.initialize();
		EnergyManipulationComponents.initialize();
		EnergyManipulationItems.initialize();
		EnergyManipulationCommands.initialize();
		LOGGER.info("Hello Fabric world!");
	}
}