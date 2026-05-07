package xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation;

import xylo_datapacks.energy_manipulation.glyph.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.valueType.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.valueType.GlyphValueType;

public class OperationGlyph extends Glyph {
    static public String OPERATOR_PIN = "Operator";
    
    public OperationGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        RegisterPinDefinition(OPERATOR_PIN, glyph -> true); // TODO: use compatibility filter to make operators the only accepted values and not let operators be chosen as possible values
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
