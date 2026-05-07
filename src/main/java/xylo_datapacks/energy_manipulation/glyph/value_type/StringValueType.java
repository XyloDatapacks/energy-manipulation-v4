package xylo_datapacks.energy_manipulation.glyph.value_type;

import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.ComparableGlyphValueInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.StringGlyphValueInterface;

public class StringValueType extends GlyphValueType implements StringGlyphValueInterface, ComparableGlyphValueInterface {
    
    class StringGlyphValue extends BaseGlyphValue {
        public String value;
        
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
    
    /*================================================================================================================*/
    // Interfaces

    @Override
    public GlyphValue equals(GlyphValue a, GlyphValue b) {
        String stringA = getStringGlyphValue(a);
        String stringB = getStringGlyphValue(b);
        return GlyphsRegistry.BOOL_VALUE_TYPE.makeBoolGlyphValue(stringA.equals(stringB));
    }

    @Override
    public GlyphValue concat(GlyphValue a, GlyphValue b) {
        String stringA = getStringGlyphValue(a);
        String stringB = getStringGlyphValue(b);
        return makeStringGlyphValue(stringA.concat(stringB));
    }

    @Override
    public GlyphValue length(GlyphValue value) {
        String stringValue = getStringGlyphValue(value);
        return GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(stringValue.length());
    }
    
    // ~Interfaces
    /*================================================================================================================*/
}
