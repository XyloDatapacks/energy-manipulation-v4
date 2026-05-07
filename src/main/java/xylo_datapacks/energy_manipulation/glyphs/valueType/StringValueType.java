package xylo_datapacks.energy_manipulation.glyphs.valueType;

import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;

public class StringValueType extends GlyphValueType {
    class StringGlyphValue extends BaseGlyphValue {
        StringGlyphValue(String value) {
            this.value = value;
        }

        @Override
        public GlyphValueType getValueType() {
            return GlyphsRegistry.STRING_VALUE_TYPE;
        }

        @Override
        public String getDebugString() {
            return value;
        }

        public String value;
    }

    public GlyphValue makeStringGlyphValue(String value) {
        return new StringGlyphValue(value);
    }
    
    public String getStringGlyphValue(GlyphValue glyphValue) {
        if (glyphValue instanceof StringGlyphValue) {
            return ((StringGlyphValue) glyphValue).value;
        }
        return "";
    }

    @Override
    public GlyphValue MakeDefaulted() {
        return makeStringGlyphValue("");
    }

    @Override
    public boolean hasOperations() {
        return true;
    }

    @Override
    public boolean hasValueSelector() { return true; }
}
