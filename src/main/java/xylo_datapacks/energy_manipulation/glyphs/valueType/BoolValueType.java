package xylo_datapacks.energy_manipulation.glyphs.valueType;

import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyphs.valueType.interfaces.ComparableGlyphValueInterface;

public class BoolValueType extends GlyphValueType implements ComparableGlyphValueInterface {
    class BoolGlyphValue extends BaseGlyphValue {
        BoolGlyphValue(boolean value) {
            this.value = value;
        }

        @Override
        public GlyphValueType getValueType() {
            return GlyphsRegistry.BOOL_VALUE_TYPE;
        }

        @Override
        public String getDebugString() {
            return value ? "true" : "false";
        }

        public boolean value;
    }

    public GlyphValue makeBoolGlyphValue(boolean value) {
        return new BoolGlyphValue(value);
    }
    
    public boolean getBoolGlyphValue(GlyphValue glyphValue) {
        if (glyphValue instanceof BoolGlyphValue) {
            return ((BoolGlyphValue) glyphValue).value;
        }
        return false;
    }

    @Override
    public GlyphValue MakeDefaulted() {
        return makeBoolGlyphValue(false);
    }

    @Override
    public boolean hasOperations() {
        return true;
    }

    @Override
    public boolean hasValueSelector() { return true; }

    /*================================================================================================================*/
    // Interfaces

    @Override
    public GlyphValue equals(GlyphValue a, GlyphValue b) {
        boolean valueA = getBoolGlyphValue(a);
        boolean valueB = getBoolGlyphValue(b);
        return makeBoolGlyphValue(valueA == valueB);
    }

    // ~Interfaces
    /*================================================================================================================*/
}
