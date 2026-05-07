package xylo_datapacks.energy_manipulation.glyph.value_type.value_interface;

import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

public interface StringGlyphValueInterface {

    /** @return StringGlyphValue */
    public GlyphValue concat(GlyphValue a, GlyphValue b);

    /** @return IntGlyphValue */
    public GlyphValue length(GlyphValue value);
}
