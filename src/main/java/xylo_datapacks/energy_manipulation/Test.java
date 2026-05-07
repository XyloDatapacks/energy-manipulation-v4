package xylo_datapacks.energy_manipulation;

import xylo_datapacks.energy_manipulation.glyphs.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyphs.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyphs.runnable.PrintStringGlyph;

public class Test {

    public static void main(String[] args) {
        GlyphsRegistry glyphsRegistry = new GlyphsRegistry();
        glyphsRegistry.initialize();
        ExecutionContext executionContext = new ExecutionContext();
        
        GlyphInstance stringPrint = GlyphsRegistry.PRINT_STRING_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        GlyphInstance rawValue = GlyphsRegistry.RAW_VALUE_GLYPH.instantiate(GlyphsRegistry.STRING_VALUE_TYPE);
        rawValue.payload.content = GlyphsRegistry.STRING_VALUE_TYPE.makeStringGlyphValue("hello world!");
        
        stringPrint.glyph.connectGlyph(stringPrint, PrintStringGlyph.STRING_PIN, rawValue);
        stringPrint.glyph.execute(executionContext, stringPrint);
    }
}
