package xylo_datapacks.energy_manipulation.glyph.valueType.value_interface;

import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.valueType.GlyphValue;

public interface SortableGlyphValueInterface {
    
    /** @return BoolGlyphValue */
    public GlyphValue greaterThan(GlyphValue a, GlyphValue b);

    /** @return BoolGlyphValue */
    public GlyphValue lessThen(GlyphValue a, GlyphValue b);

    /** @return BoolGlyphValue */
    public default GlyphValue greaterThanOrEqual(GlyphValue a, GlyphValue b) { 
        boolean notLessThen = !GlyphsRegistry.BOOL_VALUE_TYPE.getBoolGlyphValue(lessThen(a, b));
        return GlyphsRegistry.BOOL_VALUE_TYPE.makeBoolGlyphValue(notLessThen); 
    }

    /** @return BoolGlyphValue */
    public default GlyphValue lessThanOrEqual(GlyphValue a, GlyphValue b) {
        boolean notGreaterThen = !GlyphsRegistry.BOOL_VALUE_TYPE.getBoolGlyphValue(greaterThan(a, b));
        return GlyphsRegistry.BOOL_VALUE_TYPE.makeBoolGlyphValue(notGreaterThen);
    }
}
