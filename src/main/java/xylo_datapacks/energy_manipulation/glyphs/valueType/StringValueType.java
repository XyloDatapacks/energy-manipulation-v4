package xylo_datapacks.energy_manipulation.glyphs.valueType;

import xylo_datapacks.energy_manipulation.glyphs.GlyphInstance;

public class StringValueType extends GlyphValueType {
    class StringGlyphValue extends GlyphValue {
        StringGlyphValue(String value) {
            this.value = value;
        }
        
        public String value;
    }

    public GlyphValue MakeStringGlyphValue(String value) {
        return new StringGlyphValue(value);
    }
    
    public String GetStringGlyphValue(GlyphValue glyphValue) {
        if (glyphValue instanceof StringGlyphValue) {
            return ((StringGlyphValue) glyphValue).value;
        }
        return "";
    }
}
