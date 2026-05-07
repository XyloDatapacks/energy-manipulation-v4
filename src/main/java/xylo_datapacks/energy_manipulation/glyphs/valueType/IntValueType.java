package xylo_datapacks.energy_manipulation.glyphs.valueType;

public class IntValueType extends GlyphValueType {
    class IntGlyphValue extends GlyphValue {
        IntGlyphValue(int value) {
            this.value = value;
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
    public boolean hasValueSelector() { return true; }
}
