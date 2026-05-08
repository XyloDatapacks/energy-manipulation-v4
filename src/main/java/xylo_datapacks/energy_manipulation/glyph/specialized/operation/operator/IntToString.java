package xylo_datapacks.energy_manipulation.glyph.specialized.operation.operator;

import xylo_datapacks.energy_manipulation.glyph.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

public class IntToString extends Glyph {
    static public String INT_VALUE_PIN = "IntValue";

    public IntToString() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        RegisterPinDefinition(INT_VALUE_PIN, glyph -> true);
        outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> {
            return valueType == GlyphsRegistry.STRING_VALUE_TYPE;
        };
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        getInputPin(glyphInstance, INT_VALUE_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.INT_VALUE_TYPE;
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue intPinValue = evaluatePin(executionContext, glyphInstance, INT_VALUE_PIN);
        int intValue = GlyphsRegistry.INT_VALUE_TYPE.getIntGlyphValue(intPinValue);
        return GlyphsRegistry.STRING_VALUE_TYPE.makeStringGlyphValue(String.valueOf(intValue));
    }
}