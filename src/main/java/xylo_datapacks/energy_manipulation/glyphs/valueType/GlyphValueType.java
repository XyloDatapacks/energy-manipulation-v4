package xylo_datapacks.energy_manipulation.glyphs.valueType;

import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;

public class GlyphValueType {
    abstract class BaseGlyphValue extends GlyphValue {

        @Override
        public boolean isOfType(GlyphValueType glyphValueType) {
            return glyphValueType == getValueType();
        }

        public abstract GlyphValueType getValueType();
    }
    
    public boolean hasValueSelector() { return false; }
}
