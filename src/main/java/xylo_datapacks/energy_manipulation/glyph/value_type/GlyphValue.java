package xylo_datapacks.energy_manipulation.glyph.value_type;

public abstract class GlyphValue {
    
    public boolean isOfType(GlyphValueType glyphValueType) { return false; }

    public GlyphValueType getValueType() { return null; }
    
    public String getDebugString() { return ""; }
    
    public abstract GlyphValue copy();
}
