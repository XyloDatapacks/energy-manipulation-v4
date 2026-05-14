package xylo_datapacks.energy_manipulation.glyph.specialized.operation;

import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

public class OperationGlyph extends Glyph {
    static public String OPERATOR_PIN = "operator";
    
    public OperationGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        this.RegisterPinDefinition(OPERATOR_PIN, OperatorGlyphInterface.class::isInstance);
        this.outputPinDefinition.valueTypeCompatibilityPredicate = GlyphValueType::hasOperations;
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        this.getInputPin(glyphInstance, OPERATOR_PIN).ifPresent(inputPin -> {
            inputPin.valueType = glyphInstance.outputPin.valueType;
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        return this.evaluatePin(executionContext, glyphInstance, OPERATOR_PIN);
    }
}
