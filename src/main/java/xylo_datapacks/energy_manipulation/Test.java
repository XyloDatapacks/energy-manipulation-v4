package xylo_datapacks.energy_manipulation;

import xylo_datapacks.energy_manipulation.glyph.*;
import xylo_datapacks.energy_manipulation.glyph.payload.GlyphGenericPayload;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.OperationGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.operator.IntToString;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.operator.SumOperatorGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.runnable.DebugGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.runnable.PrintStringGlyph;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditor;
import xylo_datapacks.energy_manipulation.spell_editor.SpellPresetRegistry;

public class Test {

    public static void main(String[] args) {
        GlyphsRegistry.register();
        ExecutionContext executionContext = new ExecutionContext();

        // Print a string
        GlyphInstance debugStringPrint = GlyphsRegistry.DEBUG_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        GlyphInstance stringPrint = GlyphsRegistry.PRINT_STRING_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        
        GlyphInstance rawString = GlyphsRegistry.RAW_VALUE_GLYPH.instantiate(GlyphsRegistry.STRING_VALUE_TYPE);
        GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(rawString, GlyphsRegistry.STRING_VALUE_TYPE.makeStringGlyphValue("hello world!"));
        // ((GlyphGenericPayload)rawString.payload).content = GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(3);
        
        GlyphUtils.connectGlyph(debugStringPrint, DebugGlyph.INPUT_PIN, stringPrint);
        GlyphUtils.connectGlyph(stringPrint, PrintStringGlyph.STRING_PIN, rawString);

        GlyphUtils.execute(executionContext, debugStringPrint);


        // A + B
        GlyphInstance sumStringPrint = SpellPresetRegistry.makeSumTest();

        GlyphUtils.execute(executionContext, sumStringPrint);


        SpellEditor spellEditor = new SpellEditor();
        GlyphInstance debugStringStuff = GlyphsRegistry.DEBUG_GLYPH.instantiate(GlyphsRegistry.STRING_VALUE_TYPE);
        spellEditor.printCompatibleGlyphs(debugStringStuff, DebugGlyph.INPUT_PIN);
    }
}
