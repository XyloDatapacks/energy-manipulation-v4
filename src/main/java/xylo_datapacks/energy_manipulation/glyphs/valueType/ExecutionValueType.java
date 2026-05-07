package xylo_datapacks.energy_manipulation.glyphs.valueType;

public class ExecutionValueType extends GlyphValueType {
    class ExecutionGlyphValue extends GlyphValue {
        ExecutionGlyphValue(int value) {
            this.value = value;
        }
        
        public int value;
    }

    public GlyphValue makeExecutionGlyphValue(int value) {
        return new ExecutionGlyphValue(value);
    }
    
    public int getExecutionGlyphValue(GlyphValue glyphValue) {
        if (glyphValue instanceof ExecutionGlyphValue) {
            return ((ExecutionGlyphValue) glyphValue).value;
        }
        return 0;
    }
}
