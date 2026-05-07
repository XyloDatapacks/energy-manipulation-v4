package xylo_datapacks.energy_manipulation.glyph.pin;

import xylo_datapacks.energy_manipulation.glyph.Glyph;

import java.util.function.Predicate;

public class InputPinDefinition {

    public InputPinDefinition(String pinName) {
        this.pinName = pinName;
        this.nodeFilter = glyph -> true;
    }

    public String pinName;
    
    /** Used to filter which nodes can be connected to this pin (it allows to add extra conditions 
     * other than matching GlyphValueType). */
    public Predicate<Glyph> nodeFilter;
}
