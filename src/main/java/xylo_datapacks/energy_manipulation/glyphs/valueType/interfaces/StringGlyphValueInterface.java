package xylo_datapacks.energy_manipulation.glyphs.valueType.interfaces;

import xylo_datapacks.energy_manipulation.glyphs.valueType.GlyphValue;

public interface StringGlyphValueInterface {

    /** @return StringGlyphValue */
    public GlyphValue concat(GlyphValue a, GlyphValue b);

    /** @return IntGlyphValue */
    public GlyphValue length(GlyphValue value);
}
