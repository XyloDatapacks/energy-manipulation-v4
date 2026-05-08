package xylo_datapacks.energy_manipulation.glyph.pin;

import xylo_datapacks.energy_manipulation.glyph.Glyph;

import java.util.function.Predicate;

public class InputPinDefinition {
    
    /** Identifier for this pin. if InputPinMode is STANDARD, then it must be unique, instead if ARRAY, it will 
     * be shared across all pins. */
    public String pinName;

    /** Used to filter which glyphs can be connected to this pin (it allows to add extra conditions 
     * other than matching GlyphValueType). */
    public Predicate<Glyph> glyphFilter;
    
    public InputPinDefinition(String pinName) {
        this.pinName = pinName;
        this.glyphFilter = glyph -> true;
    }
}
