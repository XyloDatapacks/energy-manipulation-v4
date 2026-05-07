package xylo_datapacks.energy_manipulation.glyphs.valueType;

import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;

public class IntValueType extends GlyphValueType {
    class IntGlyphValue extends BaseGlyphValue {
        IntGlyphValue(int value) {
            this.value = value;
        }

        @Override
        public GlyphValueType getValueType() {
            return GlyphsRegistry.INT_VALUE_TYPE;
        }

        @Override
        public String getDebugString() {
            return String.valueOf(value);
        }

        public int value;
    }

    public GlyphValue makeIntGlyphValue(int value) {
        return new IntGlyphValue(value);
    }
    
    public int getIntGlyphValue(GlyphValue glyphValue) {
        if (glyphValue instanceof IntGlyphValue) {
            return ((IntGlyphValue) glyphValue).value;
        }
        return 0;
    }

    @Override
    public GlyphValue MakeDefaulted() {
        return makeIntGlyphValue(0);
    }

    @Override
    public boolean hasValueSelector() { return true; }
}
