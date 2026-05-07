package xylo_datapacks.energy_manipulation.glyphs.valueType;

import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;

public abstract class GlyphValueType {
    abstract class BaseGlyphValue extends GlyphValue {

        @Override
        public final boolean isOfType(GlyphValueType glyphValueType) {
            return glyphValueType == getValueType();
        }

        public abstract GlyphValueType getValueType();
    }
    
    public abstract GlyphValue MakeDefaulted();

    public boolean hasOperations() { return false; }
    
    public boolean hasValueSelector() { return false; }
}
