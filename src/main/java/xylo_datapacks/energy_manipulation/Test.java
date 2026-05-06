package xylo_datapacks.energy_manipulation;

import xylo_datapacks.energy_manipulation.glyphs.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyphs.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyphs.runnable.DebugPrintGlyph;

public class Test {

    public static void main(String[] args) {
        GlyphsRegistry glyphsRegistry = new GlyphsRegistry();
        glyphsRegistry.initialize();
        ExecutionContext executionContext = new ExecutionContext();
        
        GlyphInstance debugPrint = GlyphsRegistry.DEBUG_PRINT_GLYPH.instantiate(GlyphsRegistry.STRING_VALUE_TYPE);
        GlyphInstance rawValue = GlyphsRegistry.RAW_VALUE_GLYPH.instantiate(GlyphsRegistry.STRING_VALUE_TYPE);
        rawValue.payload.content = GlyphsRegistry.STRING_VALUE_TYPE.MakeStringGlyphValue("hello world!");
        
        debugPrint.glyph.connectGlyph(debugPrint, DebugPrintGlyph.DEBUG_STRING_PIN, rawValue);
        debugPrint.glyph.execute(executionContext, debugPrint);
    }
}
