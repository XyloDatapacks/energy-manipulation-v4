package xylo_datapacks.energy_manipulation.spell_editor;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinDefinition;

import java.util.Optional;
import java.util.function.Consumer;

public class SpellEditor {
    public GlyphInstance currentGlyphInstance;

    public SpellEditor() {
    }

    public void Initialize(GlyphInstance glyphInstance) {
        this.currentGlyphInstance = glyphInstance;
    }
    
    public String printCompatibleGlyphs(GlyphInstance glyphInstance, String pinName) {
        int pinIndex = glyphInstance.glyph.getInputPinIndex(pinName);
        return printCompatibleGlyphs(glyphInstance, pinIndex);
    }

    public String printCompatibleGlyphs(GlyphInstance glyphInstance, int pinIndex) {
        String pinName = glyphInstance.glyph.getInputPinDefinition(pinIndex).get().pinName;

        StringBuilder output = new StringBuilder("Printing compatible Glyphs for pin [" + pinName + "] in Glyph [" + glyphInstance.glyph.getClass().getSimpleName() + "] \n");

        GlyphsRegistry.GLYPHS.values().stream()
                .filter(glyph -> isCompatibleGlyph(glyphInstance, pinName, glyph))
                .forEach(glyph -> {
                    String entry = "- " + glyph.getClass().getSimpleName() + "\n";
                    output.append(entry);
                });

        EnergyManipulation.LOGGER.info(output.toString());
        return output.toString();
    }
    
    public void forEachCompatibleGlyph(GlyphInstance glyphInstance, int pinIndex, Consumer<Glyph> consumer) {
        GlyphsRegistry.GLYPHS.values().stream()
                .filter(glyph -> isCompatibleGlyph(glyphInstance, pinIndex, glyph))
                .forEach(consumer);
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
        if (inputPin.get().valueType == null || !glyphToTest.getOutputPinDefinition().valueTypeCompatibilityPredicate.test(inputPin.get().valueType)) {
            return false;
        }

        // Verify that the glyph we are trying to connect is acceptable for this input pin
        if (!inputPinDefinition.get().glyphFilter.test(glyphToTest)) {
            return false;
        }

        // Verify that this glyph is acceptable for the output pin of the glyph we are trying to connect
        if (!glyphToTest.getOutputPinDefinition().glyphFilter.test(glyphInstance.glyph)) {
            return false;
        }
        
        return true;
    }
    
}
