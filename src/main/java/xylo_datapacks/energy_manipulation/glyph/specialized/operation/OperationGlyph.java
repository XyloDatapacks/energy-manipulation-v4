package xylo_datapacks.energy_manipulation.glyph.specialized.operation;

import xylo_datapacks.energy_manipulation.glyph.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.pin.OutputPinDefinition;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.NumericGlyphValueInterface;

public class OperationGlyph extends Glyph {
    static public String OPERATOR_PIN = "Operator";
    
    public OperationGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        RegisterPinDefinition(OPERATOR_PIN, OperatorGlyphInterface.class::isInstance);
        outputPinDefinition.valueTypeCompatibilityPredicate = GlyphValueType::hasOperations;
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        getInputPin(glyphInstance, OPERATOR_PIN).ifPresent(inputPin -> {
            inputPin.valueType = glyphInstance.outputPin.valueType;
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        return evaluatePin(executionContext, glyphInstance, OPERATOR_PIN);
    }
}
