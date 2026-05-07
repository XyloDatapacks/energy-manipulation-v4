package xylo_datapacks.energy_manipulation.glyphs.specialized.runnable.runnable;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyphs.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyphs.Glyph;
import xylo_datapacks.energy_manipulation.glyphs.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyphs.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyphs.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyphs.valueType.GlyphValue;

import java.util.Optional;

public class DebugGlyph extends Glyph {
    static public String INPUT_PIN = "Input";

    public DebugGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        RegisterPinDefinition(INPUT_PIN, glyph -> true);
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        getInputPin(glyphInstance, INPUT_PIN).ifPresent(inputPin -> {
            inputPin.valueType = glyphInstance.outputPin.valueType;
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue outputValue = evaluatePin(executionContext, glyphInstance, INPUT_PIN);

        Optional<InputPin> inputPin = getInputPin(glyphInstance, INPUT_PIN);
        
        if (inputPin.isPresent() && inputPin.get().connectedGlyph != null) {
            EnergyManipulation.LOGGER.info("Executing [{}] with result [{}]", inputPin.get().connectedGlyph.glyph.getClass().toString(), outputValue.getDebugString());
        }
        
        return outputValue;
    }
}
