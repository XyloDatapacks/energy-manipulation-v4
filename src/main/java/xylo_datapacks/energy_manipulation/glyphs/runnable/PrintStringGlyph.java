package xylo_datapacks.energy_manipulation.glyphs.runnable;

import xylo_datapacks.energy_manipulation.glyphs.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyphs.Glyph;
import xylo_datapacks.energy_manipulation.glyphs.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyphs.pins.InputPinMode;
import xylo_datapacks.energy_manipulation.glyphs.pins.OutputPinDefinition;
import xylo_datapacks.energy_manipulation.glyphs.valueType.ExecutionValueType;
import xylo_datapacks.energy_manipulation.glyphs.valueType.GlyphValue;

public class PrintStringGlyph extends Glyph {
    static public String STRING_PIN = "String";
    
    public PrintStringGlyph() {
        super();
        
        this.inputPinMode = InputPinMode.STANDARD;
        RegisterPinDefinition(STRING_PIN, glyph -> true);
        outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> { 
            return valueType == GlyphsRegistry.EXECUTION_VALUE_TYPE; 
        };
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        getInputPin(glyphInstance, STRING_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.STRING_VALUE_TYPE;
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue stringValue = evaluatePin(executionContext, glyphInstance, STRING_PIN);
        String string = GlyphsRegistry.STRING_VALUE_TYPE.getStringGlyphValue(stringValue);
        System.out.println(">> " + string);
        
        return GlyphsRegistry.EXECUTION_VALUE_TYPE.makeExecutionGlyphValue(1);
    }
}
