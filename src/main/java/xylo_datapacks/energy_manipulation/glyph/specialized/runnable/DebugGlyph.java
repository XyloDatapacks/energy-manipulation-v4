package xylo_datapacks.energy_manipulation.glyph.specialized.runnable;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

import java.util.Optional;

public class DebugGlyph extends Glyph {
    static public String INPUT_PIN = "Input";

    public DebugGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        this.RegisterPinDefinition(INPUT_PIN, glyph -> true);
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        this.getInputPin(glyphInstance, INPUT_PIN).ifPresent(inputPin -> {
            inputPin.valueType = glyphInstance.outputPin.valueType;
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue outputValue = this.evaluatePin(executionContext, glyphInstance, INPUT_PIN);

        Optional<InputPin> inputPin = this.getInputPin(glyphInstance, INPUT_PIN);
        
        if (inputPin.isPresent() && inputPin.get().connectedGlyph != null) {
            EnergyManipulation.LOGGER.info("Executing [{}] with result [{}]", inputPin.get().connectedGlyph.glyph.getClass().getSimpleName(), outputValue.getDebugString());
        }
        
        return outputValue;
    }
}
