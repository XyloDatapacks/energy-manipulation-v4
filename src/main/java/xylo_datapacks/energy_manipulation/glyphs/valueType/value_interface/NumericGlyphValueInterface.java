package xylo_datapacks.energy_manipulation.glyphs.valueType.value_interface;

import xylo_datapacks.energy_manipulation.glyphs.valueType.GlyphValue;

public interface NumericGlyphValueInterface {
    public GlyphValue add(GlyphValue a, GlyphValue b);
    public GlyphValue subtract(GlyphValue a, GlyphValue b);
    public GlyphValue multiply(GlyphValue a, GlyphValue b);
    public GlyphValue divide(GlyphValue a, GlyphValue b);
    public GlyphValue power(GlyphValue value, GlyphValue exponent);
    public GlyphValue sqrt(GlyphValue value);
}
