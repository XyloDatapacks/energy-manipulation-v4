package xylo_datapacks.energy_manipulation.spell_editor;

import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.OperationGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operator.SumOperatorGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.PrintStringGlyph;

public class SpellPresetRegistry {
    
    public static GlyphInstance makeProgramTest() {
        GlyphInstance programGlyph = GlyphsRegistry.PROGRAM_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        

        return programGlyph;
    }
}
