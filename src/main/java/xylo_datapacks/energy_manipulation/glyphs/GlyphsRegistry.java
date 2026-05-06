package xylo_datapacks.energy_manipulation.glyphs;

import xylo_datapacks.energy_manipulation.glyphs.runnable.DebugPrintGlyph;
import xylo_datapacks.energy_manipulation.glyphs.valueType.StringValueType;
import xylo_datapacks.energy_manipulation.glyphs.variable.RawValueGlyph;

public class GlyphsRegistry {
    static public RawValueGlyph RAW_VALUE_GLYPH = new RawValueGlyph();
    static public DebugPrintGlyph DEBUG_PRINT_GLYPH = new DebugPrintGlyph();
    
    static public StringValueType STRING_VALUE_TYPE = new StringValueType();
    
    public void initialize() {}
}
