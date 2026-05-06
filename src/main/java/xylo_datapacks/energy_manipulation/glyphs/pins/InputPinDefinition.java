package xylo_datapacks.energy_manipulation.glyphs.pins;

import xylo_datapacks.energy_manipulation.glyphs.Glyph;

import java.util.function.Predicate;

public class InputPinDefinition {
    
    /** Used to filter which nodes can be connected to this pin (it allows to add extra conditions 
     * other than matching GlyphValueType). */
    Predicate<Glyph> nodeFilter;
}
