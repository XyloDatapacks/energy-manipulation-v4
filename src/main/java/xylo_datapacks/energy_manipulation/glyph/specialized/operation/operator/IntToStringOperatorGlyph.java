package xylo_datapacks.energy_manipulation.glyph.specialized.operation.operator;

import xylo_datapacks.energy_manipulation.glyph.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.OperatorGlyphInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

public class IntToStringOperatorGlyph extends Glyph implements OperatorGlyphInterface {
    static public String INT_VALUE_PIN = "IntValue";

    public IntToStringOperatorGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        this.RegisterPinDefinition(INT_VALUE_PIN, glyph -> true);
        this.outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> {
            return valueType == GlyphsRegistry.STRING_VALUE_TYPE;
        };
        this.outputPinDefinition.glyphFilter = glyph -> glyph == GlyphsRegistry.OPERATION_GLYPH;
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        this.getInputPin(glyphInstance, INT_VALUE_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.INT_VALUE_TYPE;
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue intPinValue = this.evaluatePin(executionContext, glyphInstance, INT_VALUE_PIN);
        int intValue = GlyphsRegistry.INT_VALUE_TYPE.getIntGlyphValue(intPinValue);
        return GlyphsRegistry.STRING_VALUE_TYPE.makeStringGlyphValue(String.valueOf(intValue));
    }
}