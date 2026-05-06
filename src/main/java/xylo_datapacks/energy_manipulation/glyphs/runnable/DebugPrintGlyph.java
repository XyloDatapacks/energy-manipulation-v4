package xylo_datapacks.energy_manipulation.glyphs.runnable;

import xylo_datapacks.energy_manipulation.glyphs.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyphs.Glyph;
import xylo_datapacks.energy_manipulation.glyphs.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyphs.pins.InputPinMode;
import xylo_datapacks.energy_manipulation.glyphs.valueType.GlyphValue;

public class DebugPrintGlyph extends Glyph {
    static public String DEBUG_STRING_PIN = "String";
    
    public DebugPrintGlyph() {
        super();
        
        this.inputPinMode = InputPinMode.STANDARD;
        RegisterPinDefinition(DEBUG_STRING_PIN, glyph -> true);
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue stringValue = evaluatePin(executionContext, glyphInstance, DEBUG_STRING_PIN);
        String string = GlyphsRegistry.STRING_VALUE_TYPE.GetStringGlyphValue(stringValue);
        System.out.println(">> " + string);
        
        return new GlyphValue();
    }
}
