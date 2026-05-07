package xylo_datapacks.energy_manipulation.glyph.pin;

import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.util.function.Predicate;

public class OutputPinDefinition {

    /** Used to verify that this pin can provide a value type. */
    public Predicate<GlyphValueType> valueTypeCompatibilityPredicate;
    
    public OutputPinDefinition() {
        this.valueTypeCompatibilityPredicate = glyph -> true;
    }
}
