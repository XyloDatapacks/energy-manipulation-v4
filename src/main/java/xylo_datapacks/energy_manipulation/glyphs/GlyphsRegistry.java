package xylo_datapacks.energy_manipulation.glyphs;

import xylo_datapacks.energy_manipulation.glyphs.runnable.PrintStringGlyph;
import xylo_datapacks.energy_manipulation.glyphs.valueType.ExecutionValueType;
import xylo_datapacks.energy_manipulation.glyphs.valueType.IntValueType;
import xylo_datapacks.energy_manipulation.glyphs.valueType.StringValueType;
import xylo_datapacks.energy_manipulation.glyphs.variable.RawValueGlyph;

public class GlyphsRegistry {
    static public RawValueGlyph RAW_VALUE_GLYPH = new RawValueGlyph();
    static public PrintStringGlyph PRINT_STRING_GLYPH = new PrintStringGlyph();
    
    static public StringValueType STRING_VALUE_TYPE = new StringValueType();
    static public IntValueType INT_VALUE_TYPE = new IntValueType();
    static public ExecutionValueType EXECUTION_VALUE_TYPE = new ExecutionValueType();
    
    public void initialize() {}
}
