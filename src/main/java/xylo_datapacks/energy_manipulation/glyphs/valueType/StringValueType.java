package xylo_datapacks.energy_manipulation.glyphs.valueType;

public class StringValueType extends GlyphValueType {
    class StringGlyphValue extends GlyphValue {
        StringGlyphValue(String value) {
            this.value = value;
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
    public boolean hasValueSelector() { return true; }
}
