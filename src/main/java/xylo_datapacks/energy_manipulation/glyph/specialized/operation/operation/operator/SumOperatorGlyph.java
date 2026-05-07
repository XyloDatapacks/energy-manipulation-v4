package xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.operator;

import xylo_datapacks.energy_manipulation.glyph.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.valueType.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.valueType.value_interface.NumericGlyphValueInterface;

public class SumOperatorGlyph extends Glyph {
    static public String FIRST_VALUE_PIN = "A";
    static public String SECOND_VALUE_PIN = "B";
    
    public SumOperatorGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        RegisterPinDefinition(FIRST_VALUE_PIN, glyph -> true);
        RegisterPinDefinition(SECOND_VALUE_PIN, glyph -> true);
        outputPinDefinition.valueTypeCompatibilityPredicate = NumericGlyphValueInterface.class::isInstance;
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        getInputPin(glyphInstance, FIRST_VALUE_PIN).ifPresent(inputPin -> {
            inputPin.valueType = glyphInstance.outputPin.valueType;
        });

        getInputPin(glyphInstance, SECOND_VALUE_PIN).ifPresent(inputPin -> {
            inputPin.valueType = glyphInstance.outputPin.valueType;
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        NumericGlyphValueInterface numericGlyphValueInterface = (NumericGlyphValueInterface) glyphInstance.outputPin.valueType;
        return numericGlyphValueInterface.add(
                evaluatePin(executionContext, glyphInstance, FIRST_VALUE_PIN),
                evaluatePin(executionContext, glyphInstance, SECOND_VALUE_PIN)
        );
    }
}
