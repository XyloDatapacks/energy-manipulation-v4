package xylo_datapacks.energy_manipulation.glyph.specialized.operation.operator;

import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.OperatorGlyphInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.NumericGlyphValueInterface;

public class SumOperatorGlyph extends Glyph implements OperatorGlyphInterface {
    static public String FIRST_VALUE_PIN = "first_value";
    static public String SECOND_VALUE_PIN = "second_value";
    
    public SumOperatorGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        this.RegisterPinDefinition(FIRST_VALUE_PIN, glyph -> true);
        this.RegisterPinDefinition(SECOND_VALUE_PIN, glyph -> true);
        this.outputPinDefinition.valueTypeCompatibilityPredicate = NumericGlyphValueInterface.class::isInstance;
        this.outputPinDefinition.glyphFilter = glyph -> glyph == GlyphsRegistry.OPERATION_GLYPH;
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        this.getInputPin(glyphInstance, FIRST_VALUE_PIN).ifPresent(inputPin -> {
            inputPin.valueType = glyphInstance.outputPin.valueType;
        });

        this.getInputPin(glyphInstance, SECOND_VALUE_PIN).ifPresent(inputPin -> {
            inputPin.valueType = glyphInstance.outputPin.valueType;
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        NumericGlyphValueInterface numericGlyphValueInterface = (NumericGlyphValueInterface) glyphInstance.outputPin.valueType;
        return numericGlyphValueInterface.add(
                this.evaluatePin(executionContext, glyphInstance, FIRST_VALUE_PIN),
                this.evaluatePin(executionContext, glyphInstance, SECOND_VALUE_PIN)
        );
    }
}
