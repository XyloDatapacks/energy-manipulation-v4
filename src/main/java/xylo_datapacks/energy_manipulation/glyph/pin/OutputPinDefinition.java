package xylo_datapacks.energy_manipulation.glyph.pin;

import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.util.function.Predicate;

public class OutputPinDefinition {

    public OutputPinDefinition() {
        this.valueTypeCompatibilityPredicate = glyph -> true;
    }

    /** Used to verify that this pin can provide a value type. */
    public Predicate<GlyphValueType> valueTypeCompatibilityPredicate;
}
