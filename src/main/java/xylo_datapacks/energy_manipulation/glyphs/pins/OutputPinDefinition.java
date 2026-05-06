package xylo_datapacks.energy_manipulation.glyphs.pins;

import xylo_datapacks.energy_manipulation.glyphs.valueType.GlyphValueType;

import java.util.function.Predicate;

public class OutputPinDefinition {

    public OutputPinDefinition() {
        valueTypeCompatibilityPredicate = glyph -> true;
    }

    /** Used to verify that this pin can provide a value type. */
    public Predicate<GlyphValueType> valueTypeCompatibilityPredicate;
}
