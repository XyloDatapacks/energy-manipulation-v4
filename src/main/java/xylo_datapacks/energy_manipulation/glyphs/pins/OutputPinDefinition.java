package xylo_datapacks.energy_manipulation.glyphs.pins;

import xylo_datapacks.energy_manipulation.glyphs.Glyph;
import xylo_datapacks.energy_manipulation.glyphs.values.GlyphValueType;

import java.util.function.Predicate;

public class OutputPinDefinition {

    /** Used to verify that this pin can provide a value type. */
    Predicate<GlyphValueType> valueTypeCompatibilityPredicate;
}
