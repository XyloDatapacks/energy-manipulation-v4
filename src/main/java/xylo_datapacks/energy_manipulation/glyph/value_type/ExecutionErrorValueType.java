package xylo_datapacks.energy_manipulation.glyph.value_type;

import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;

public class ExecutionErrorValueType extends GlyphValueType {
   
    class ExecutionErrorGlyphValue extends BaseGlyphValue {
        public String message;
        
        ExecutionErrorGlyphValue(String message) {
            this.message = message;
        }

        @Override
        public GlyphValueType getValueType() {
            return GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE;
        }

        @Override
        public String getDebugString() {
            return message;
        }
    }

    public GlyphValue makeExecutionErrorGlyphValue(String value) {
        return new ExecutionErrorGlyphValue(value);
    }

    public String getExecutionErrorGlyphValue(GlyphValue glyphValue) {
        if (glyphValue instanceof ExecutionErrorGlyphValue) {
            return ((ExecutionErrorGlyphValue) glyphValue).message;
        }
        return "";
    }

    @Override
    public GlyphValue MakeDefaulted() {
        return makeExecutionErrorGlyphValue("Unexpected Execution Error");
    }
}