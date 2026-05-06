package xylo_datapacks.energy_manipulation.glyphs.pins;

import xylo_datapacks.energy_manipulation.glyphs.Glyph;
import xylo_datapacks.energy_manipulation.glyphs.values.GlyphValueType;

import java.lang.ref.WeakReference;

public class InputPin {

    WeakReference<Glyph> owner;
    GlyphValueType valueType;

    /** We keep a reference to the connected glyph directly since they only have one output pin. 
     * This way we avoid having to make the output pin responsible for keeping a glyph in memory. */
    Glyph connectedGlyph;
}
