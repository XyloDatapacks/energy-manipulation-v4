package xylo_datapacks.energy_manipulation.glyphs.operation.operator;

import xylo_datapacks.energy_manipulation.glyphs.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyphs.Glyph;
import xylo_datapacks.energy_manipulation.glyphs.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyphs.pins.InputPinMode;
import xylo_datapacks.energy_manipulation.glyphs.valueType.GlyphValue;

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