package xylo_datapacks.energy_manipulation.glyph.value_type.value_interface;

import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

public interface StringConvertibleValueInterface {

    /** @return StringGlyphValue */
    public GlyphValue ValueFromString(String value);
    
    public String ValueToString(GlyphValue value);
}
