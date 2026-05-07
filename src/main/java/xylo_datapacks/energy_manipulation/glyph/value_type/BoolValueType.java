package xylo_datapacks.energy_manipulation.glyph.value_type;

import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.ComparableGlyphValueInterface;

public class BoolValueType extends GlyphValueType implements ComparableGlyphValueInterface {
    
    class BoolGlyphValue extends BaseGlyphValue {
        public boolean value;
        
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
