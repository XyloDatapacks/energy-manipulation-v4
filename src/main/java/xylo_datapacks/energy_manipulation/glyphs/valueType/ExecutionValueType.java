package xylo_datapacks.energy_manipulation.glyphs.valueType;

import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;

public class ExecutionValueType extends GlyphValueType {
    class ExecutionGlyphValue extends BaseGlyphValue {
        ExecutionGlyphValue(int value) {
            this.value = value;
        }

        @Override
        public GlyphValueType getValueType() {
            return GlyphsRegistry.EXECUTION_VALUE_TYPE;
        }

        @Override
        public String getDebugString() {
            return value > 0 ? "success (" + value + ")" : "failure";
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

    @Override
    public GlyphValue MakeDefaulted() {
        return makeExecutionGlyphValue(0);
    }
}
