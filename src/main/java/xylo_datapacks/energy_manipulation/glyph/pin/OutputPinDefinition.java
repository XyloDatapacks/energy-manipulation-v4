package xylo_datapacks.energy_manipulation.glyph.pin;

import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.util.function.Predicate;

public class OutputPinDefinition {

    /** Used to verify that this pin can provide a value type. */
    public Predicate<GlyphValueType> valueTypeCompatibilityPredicate;

    /** Used to filter which glyphs can be connected to this pin (it allows to add extra conditions 
     * other than matching GlyphValueType). */
    public Predicate<Glyph> glyphFilter;
    
    public OutputPinDefinition() {
        this.valueTypeCompatibilityPredicate = glyph -> true;
        this.glyphFilter = glyph -> true;
    }
}
