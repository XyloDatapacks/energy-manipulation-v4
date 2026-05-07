package xylo_datapacks.energy_manipulation;

import xylo_datapacks.energy_manipulation.glyphs.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyphs.Glyph;
import xylo_datapacks.energy_manipulation.glyphs.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyphs.payload.GlyphGenericPayload;
import xylo_datapacks.energy_manipulation.glyphs.runnable.DebugGlyph;
import xylo_datapacks.energy_manipulation.glyphs.runnable.PrintStringGlyph;

public class Test {

    public static void main(String[] args) {
        GlyphsRegistry glyphsRegistry = new GlyphsRegistry();
        glyphsRegistry.initialize();
        ExecutionContext executionContext = new ExecutionContext();

        GlyphInstance debugGlyph_print = GlyphsRegistry.DEBUG_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        GlyphInstance stringPrint = GlyphsRegistry.PRINT_STRING_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        
        GlyphInstance debugGlyph_rawValue = GlyphsRegistry.DEBUG_GLYPH.instantiate(GlyphsRegistry.STRING_VALUE_TYPE);
        GlyphInstance rawValue = GlyphsRegistry.RAW_VALUE_GLYPH.instantiate(GlyphsRegistry.STRING_VALUE_TYPE);
        GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(rawValue, GlyphsRegistry.STRING_VALUE_TYPE.makeStringGlyphValue("hello world!"));
        
        Glyph.connectGlyphStatic(debugGlyph_print, DebugGlyph.INPUT_PIN, stringPrint);
        Glyph.connectGlyphStatic(stringPrint, PrintStringGlyph.STRING_PIN, debugGlyph_rawValue);
        Glyph.connectGlyphStatic(debugGlyph_rawValue, DebugGlyph.INPUT_PIN, rawValue);

        Glyph.executeStatic(executionContext, debugGlyph_print);
    }
}
