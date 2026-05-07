package xylo_datapacks.energy_manipulation;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;

public class EnergyManipulation implements ModInitializer {
	public static final String MOD_ID = "energy-manipulation";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		GlyphsRegistry.register();
		EnergyManipulationCommands.register();
		LOGGER.info("Hello Fabric world!");
	}
}