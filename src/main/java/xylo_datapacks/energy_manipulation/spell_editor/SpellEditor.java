package xylo_datapacks.energy_manipulation.spell_editor;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinDefinition;

import java.util.Optional;

public class SpellEditor {
    
    public void printCompatibleGlyphs(GlyphInstance glyphInstance, String pinName) {
        EnergyManipulation.LOGGER.info("Printing compatible Glyphs for pin [{}] in Glyph [{}]", pinName, glyphInstance.glyph.getClass().getSimpleName());
        GlyphsRegistry.GLYPHS.values().stream()
                .filter(glyph -> isCompatibleGlyph(glyphInstance, pinName, glyph))
                .forEach(glyph -> {
                    EnergyManipulation.LOGGER.info("- {}", glyph.getClass().getSimpleName());
                });
    }
    
    public boolean isCompatibleGlyph(GlyphInstance glyphInstance, String pinName, Glyph glyphToTest) {
        int pinIndex = glyphInstance.glyph.getInputPinIndex(pinName);
        return isCompatibleGlyph(glyphInstance, pinIndex, glyphToTest);
    }

    public boolean isCompatibleGlyph(GlyphInstance glyphInstance, int pinIndex, Glyph glyphToTest) {
        Optional<InputPin> inputPin = glyphInstance.glyph.getInputPin(glyphInstance, pinIndex);
        Optional<InputPinDefinition> inputPinDefinition = glyphInstance.glyph.getInputPinDefinition(pinIndex);
        if (inputPin.isEmpty() || inputPinDefinition.isEmpty()) {
            return false;
        }

        // Check if the input pin value type is supported by the output pin of the glyph we are tring to connect
        if (inputPin.get().valueType == null || !glyphToTest.outputPinDefinition.valueTypeCompatibilityPredicate.test(inputPin.get().valueType)) {
            return false;
        }

        // Verify that the glyph we are trying to connect is acceptable for this input pin
        if (!inputPinDefinition.get().nodeFilter.test(glyphToTest)) {
            return false;
        }
        
        return true;
    }
    
}
